package com.fit10.app.data.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.android.billingclient.api.queryProductDetails
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.EntitlementInfo
import com.fit10.app.domain.billing.EntitlementVerifier
import com.fit10.app.domain.billing.LocalEntitlementVerifier
import com.fit10.app.domain.billing.SubscriptionPlanDetails
import com.fit10.app.domain.billing.SubscriptionState
import com.fit10.app.domain.repository.BillingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Cache abstraction for persistent subscription entitlement state.
 */
interface EntitlementCache {
    fun save(info: EntitlementInfo)
    fun load(): EntitlementInfo
}

class SharedPreferencesEntitlementCache(context: Context) : EntitlementCache {
    private val prefs: SharedPreferences = context.getSharedPreferences("fit10_billing_cache", Context.MODE_PRIVATE)

    override fun save(info: EntitlementInfo) {
        prefs.edit()
            .putString("sub_state", info.subscriptionState.name)
            .putString("plan_id", info.planId ?: "")
            .putString("plan_name", info.planName ?: "")
            .putLong("expiry_epoch", info.expiryEpochMs ?: -1L)
            .putBoolean("auto_renew", info.isAutoRenewing)
            .putLong("last_verified", info.lastVerifiedEpochMs)
            .apply()
    }

    override fun load(): EntitlementInfo {
        val stateName = prefs.getString("sub_state", SubscriptionState.FREE.name) ?: SubscriptionState.FREE.name
        val state = runCatching { SubscriptionState.valueOf(stateName) }.getOrDefault(SubscriptionState.FREE)
        val planId = prefs.getString("plan_id", "")?.ifEmpty { null }
        val planName = prefs.getString("plan_name", "")?.ifEmpty { null }
        val expiry = prefs.getLong("expiry_epoch", -1L).takeIf { it > 0 }
        val autoRenew = prefs.getBoolean("auto_renew", false)
        val lastVerified = prefs.getLong("last_verified", System.currentTimeMillis())

        return EntitlementInfo(
            subscriptionState = state,
            planId = planId,
            planName = planName,
            expiryEpochMs = expiry,
            isAutoRenewing = autoRenew,
            lastVerifiedEpochMs = lastVerified
        )
    }
}

class InMemoryEntitlementCache(
    private var cached: EntitlementInfo = EntitlementInfo(SubscriptionState.FREE)
) : EntitlementCache {
    override fun save(info: EntitlementInfo) {
        cached = info
    }

    override fun load(): EntitlementInfo = cached
}

/**
 * Robust implementation of BillingRepository interfacing with Google Play Billing Library.
 * Handles lifecycle events, pending purchases, 3-day auto-acknowledgement, offline caching,
 * reconnection with backoff, and product pricing calculations.
 */
class BillingRepositoryImpl(
    private val context: Context? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val verifier: EntitlementVerifier = LocalEntitlementVerifier(),
    private val cache: EntitlementCache = context?.let { SharedPreferencesEntitlementCache(it) } ?: InMemoryEntitlementCache(),
    private val billingClientProvider: ((PurchasesUpdatedListener) -> BillingClient)? = null
) : BillingRepository, PurchasesUpdatedListener {

    private val _entitlement = MutableStateFlow(cache.load())
    override val entitlement: StateFlow<EntitlementInfo> = _entitlement.asStateFlow()

    private val _subscriptionPlans = MutableStateFlow<List<SubscriptionPlanDetails>>(
        listOf(SubscriptionPlanDetails.FALLBACK_ANNUAL, SubscriptionPlanDetails.FALLBACK_MONTHLY, SubscriptionPlanDetails.FALLBACK_LIFETIME)
    )
    override val subscriptionPlans: StateFlow<List<SubscriptionPlanDetails>> = _subscriptionPlans.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val cachedProductDetails = mutableMapOf<String, ProductDetails>()

    private var retryCount = 0
    private val maxRetries = 4

    private val billingClient: BillingClient by lazy {
        billingClientProvider?.invoke(this) ?: BillingClient.newBuilder(context!!)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .enablePrepaidPlans()
                    .build()
            )
            .build()
    }

    init {
        if (context != null || billingClientProvider != null) {
            startBillingConnection()
        }
    }

    fun startBillingConnection() {
        if (billingClient.isReady) {
            scope.launch {
                queryProductDetails()
                refreshPurchases()
            }
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    retryCount = 0
                    scope.launch {
                        queryProductDetails()
                        refreshPurchases()
                    }
                } else {
                    handleConnectionFailure()
                }
            }

            override fun onBillingServiceDisconnected() {
                handleConnectionFailure()
            }
        })
    }

    private fun handleConnectionFailure() {
        if (retryCount < maxRetries) {
            retryCount++
            val backoffMs = (1000L * (1 shl retryCount)).coerceAtMost(16000L)
            scope.launch {
                delay(backoffMs)
                startBillingConnection()
            }
        }
    }

    override suspend fun retryBillingConnection() {
        retryCount = 0
        startBillingConnection()
    }

    private suspend fun queryProductDetails() = withContext(Dispatchers.IO) {
        if (!billingClient.isReady) return@withContext

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BillingConstants.PRODUCT_ID_ANNUAL)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BillingConstants.PRODUCT_ID_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val subsResult = billingClient.queryProductDetails(params)
        val inAppResult = billingClient.queryProductDetails(
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(BillingConstants.PRODUCT_ID_LIFETIME)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()
        )
        val productDetailsList = buildList {
            if (subsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) addAll(subsResult.productDetailsList.orEmpty())
            if (inAppResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) addAll(inAppResult.productDetailsList.orEmpty())
        }
        if (productDetailsList.isEmpty()) return@withContext

        val detailsMap = productDetailsList.associateBy { it.productId }
        cachedProductDetails.clear()
        cachedProductDetails.putAll(detailsMap)

        val monthlyMicros = detailsMap[BillingConstants.PRODUCT_ID_MONTHLY]?.subscriptionOfferDetails?.firstOrNull()
            ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.priceAmountMicros ?: 149_000_000L

        _subscriptionPlans.value = productDetailsList.map { productDetails ->
            if (productDetails.productId == BillingConstants.PRODUCT_ID_LIFETIME) {
                val oneTime = productDetails.oneTimePurchaseOfferDetailsList?.firstOrNull()
                val price = oneTime?.formattedPrice ?: SubscriptionPlanDetails.FALLBACK_LIFETIME.formattedPrice
                return@map SubscriptionPlanDetails(
                    productId = productDetails.productId,
                    title = productDetails.title.ifBlank { "Lifetime" },
                    formattedPrice = price,
                    priceAmountMicros = oneTime?.priceAmountMicros ?: 0L,
                    currencyCode = oneTime?.priceCurrencyCode ?: "INR",
                    billingPeriod = "",
                    formattedMonthlyEquivalent = price
                )
            }
            val offer = productDetails.subscriptionOfferDetails?.firstOrNull()
            val recurringPhase = offer?.pricingPhases?.pricingPhaseList?.lastOrNull()
            val trialPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull { it.priceAmountMicros == 0L }
            val priceMicros = recurringPhase?.priceAmountMicros ?: 0L
            val formattedPrice = recurringPhase?.formattedPrice ?: "₹0"
            val currency = recurringPhase?.priceCurrencyCode ?: "INR"
            val isAnnual = productDetails.productId == BillingConstants.PRODUCT_ID_ANNUAL
            SubscriptionPlanDetails(
                productId = productDetails.productId,
                title = productDetails.title.ifBlank { if (isAnnual) "Annual Pass" else "Monthly Pass" },
                formattedPrice = formattedPrice,
                priceAmountMicros = priceMicros,
                currencyCode = currency,
                billingPeriod = recurringPhase?.billingPeriod ?: "",
                hasFreeTrial = trialPhase != null,
                freeTrialDays = if (trialPhase != null) 7 else 0,
                formattedMonthlyEquivalent = if (isAnnual) SubscriptionPlanDetails.computeMonthlyEquivalent(priceMicros, currency) else formattedPrice,
                savingsPercentage = if (isAnnual) SubscriptionPlanDetails.computeSavingsPercentage(priceMicros, monthlyMicros) else null,
                isFallbackSkeleton = false
            )
        }
    }

    private suspend fun queryAllPurchases(): List<Purchase>? {
        val subs = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        )
        val inApp = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        )
        if (subs.billingResult.responseCode != BillingClient.BillingResponseCode.OK &&
            inApp.billingResult.responseCode != BillingClient.BillingResponseCode.OK
        ) return null
        return subs.purchasesList + inApp.purchasesList
    }

    override suspend fun refreshPurchases(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!billingClient.isReady) {
            return@withContext Result.failure(IllegalStateException("BillingClient is not connected"))
        }

        _isLoading.value = true
        val purchases = queryAllPurchases()
        _isLoading.value = false
        purchases?.let(::processPurchases)
        Result.success(Unit)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { processPurchases(queryAllPurchases() ?: purchases) }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // User canceled purchase flow: no-op
        } else {
            // Other error codes
        }
    }

    fun processPurchases(purchases: List<Purchase>) {
        scope.launch {
            val validPurchases = purchases.filter { it.products.any { p -> isProProduct(p) } }

            // 1. Pending purchases check: NEVER unlock until PURCHASED
            val activePurchase = validPurchases
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .sortedByDescending { BillingConstants.PRODUCT_ID_LIFETIME in it.products }
                .firstOrNull()
            val pendingPurchase = validPurchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PENDING }

            if (activePurchase != null) {
                // Verify receipt if verifier is attached
                val productId = activePurchase.products.first()
                val isVerified = verifier.verifyPurchase(activePurchase.purchaseToken, productId)

                if (isVerified) {
                    // 2. Acknowledge within 3 days if not acknowledged
                    if (!activePurchase.isAcknowledged) {
                        val ackParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(activePurchase.purchaseToken)
                            .build()
                        billingClient.acknowledgePurchase(ackParams) { /* ack callback */ }
                    }

                    val isLifetime = productId == BillingConstants.PRODUCT_ID_LIFETIME
                    val state = if (isLifetime || activePurchase.isAutoRenewing) {
                        SubscriptionState.ACTIVE
                    } else {
                        SubscriptionState.CANCELED_ACTIVE
                    }

                    val entitlement = EntitlementInfo(
                        subscriptionState = state,
                        planId = productId,
                        planName = when (productId) {
                            BillingConstants.PRODUCT_ID_ANNUAL -> "Fit10 Pro (Annual Plan)"
                            BillingConstants.PRODUCT_ID_LIFETIME -> "Fit10 Pro (Lifetime)"
                            else -> "Fit10 Pro (Monthly Plan)"
                        },
                        expiryEpochMs = null, // In production, retrieved from backend / Play Developer API
                        isAutoRenewing = activePurchase.isAutoRenewing,
                        lastVerifiedEpochMs = System.currentTimeMillis()
                    )

                    updateEntitlement(entitlement)
                    return@launch
                }
            } else if (pendingPurchase != null) {
                // Pending: Keep user informed without granting full access yet
                // Fall back to existing cached state or Free
                val current = _entitlement.value
                if (current.subscriptionState == SubscriptionState.FREE) {
                    // Do not unlock
                }
                return@launch
            }

            // No active Pro purchase found
            val revoked = EntitlementInfo(
                subscriptionState = SubscriptionState.FREE,
                lastVerifiedEpochMs = System.currentTimeMillis()
            )
            updateEntitlement(revoked)
        }
    }

    override suspend fun launchBillingFlow(
        activity: Activity,
        plan: SubscriptionPlanDetails
    ): Result<Unit> = withContext(Dispatchers.Main) {
        val productDetails = cachedProductDetails[plan.productId]
            ?: return@withContext Result.failure(IllegalStateException("Product details not available for ${plan.productId}"))

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: productDetails.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
        if (offerToken == null && plan.productId != BillingConstants.PRODUCT_ID_LIFETIME) {
            return@withContext Result.failure(IllegalStateException("Offer token missing"))
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply { offerToken?.let(::setOfferToken) }
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val response = billingClient.launchBillingFlow(activity, flowParams)
        if (response.responseCode == BillingClient.BillingResponseCode.OK) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Launch billing flow failed with code: ${response.responseCode}"))
        }
    }

    override suspend fun restorePurchases(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!billingClient.isReady) {
            return@withContext Result.failure(IllegalStateException("Billing service disconnected"))
        }

        _isLoading.value = true
        val purchases = queryAllPurchases()
        _isLoading.value = false
        if (purchases == null) return@withContext Result.failure(IllegalStateException("Could not load purchases"))
        processPurchases(purchases)
        Result.success(purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED && it.products.any(::isProProduct) })
    }

    private fun isProProduct(productId: String): Boolean {
        return productId == BillingConstants.PRODUCT_ID_ANNUAL ||
            productId == BillingConstants.PRODUCT_ID_MONTHLY ||
            productId == BillingConstants.PRODUCT_ID_LIFETIME
    }

    private fun updateEntitlement(newEntitlement: EntitlementInfo) {
        _entitlement.value = newEntitlement
        cache.save(newEntitlement)
    }
}
