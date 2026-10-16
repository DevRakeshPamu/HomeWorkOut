package com.fit10.app.data.billing

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.AlternativeBillingOnlyAvailabilityListener
import com.android.billingclient.api.AlternativeBillingOnlyInformationDialogListener
import com.android.billingclient.api.AlternativeBillingOnlyReportingDetailsListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingConfigResponseListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingProgramAvailabilityListener
import com.android.billingclient.api.BillingProgramInformationDialogListener
import com.android.billingclient.api.BillingProgramInformationDialogParams
import com.android.billingclient.api.BillingProgramReportingDetailsListener
import com.android.billingclient.api.BillingProgramReportingDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ExternalOfferAvailabilityListener
import com.android.billingclient.api.ExternalOfferInformationDialogListener
import com.android.billingclient.api.ExternalOfferReportingDetailsListener
import com.android.billingclient.api.GetBillingChoiceInfoParams
import com.android.billingclient.api.BillingChoiceInfoResponseListener
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.LaunchExternalLinkParams
import com.android.billingclient.api.LaunchExternalLinkResponseListener
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.EntitlementInfo
import com.fit10.app.domain.billing.EntitlementVerifier
import com.fit10.app.domain.billing.LocalEntitlementVerifier
import com.fit10.app.domain.billing.SubscriptionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingRepositoryTest {

    @Suppress("DEPRECATION")
    private open class FakeBillingClient : BillingClient() {
        val acknowledgedTokens = mutableListOf<String>()
        var setupResponseCode: Int = BillingResponseCode.OK

        override fun acknowledgePurchase(params: AcknowledgePurchaseParams, listener: AcknowledgePurchaseResponseListener) {
            acknowledgedTokens.add(params.purchaseToken)
            listener.onAcknowledgePurchaseResponse(
                BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()
            )
        }

        override fun isReady(): Boolean = true

        override fun startConnection(listener: BillingClientStateListener) {
            listener.onBillingSetupFinished(
                BillingResult.newBuilder().setResponseCode(setupResponseCode).build()
            )
        }

        override fun endConnection() {}
        override fun getConnectionState(): Int = 2
        override fun isFeatureSupported(feature: String): BillingResult =
            BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()

        override fun launchBillingFlow(activity: android.app.Activity, params: BillingFlowParams): BillingResult =
            BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()

        override fun showAlternativeBillingOnlyInformationDialog(activity: android.app.Activity, listener: AlternativeBillingOnlyInformationDialogListener): BillingResult =
            BillingResult.newBuilder().build()

        override fun showExternalOfferInformationDialog(activity: android.app.Activity, listener: ExternalOfferInformationDialogListener): BillingResult =
            BillingResult.newBuilder().build()

        override fun showInAppMessages(activity: android.app.Activity, params: InAppMessageParams, listener: InAppMessageResponseListener): BillingResult =
            BillingResult.newBuilder().build()

        override fun consumeAsync(params: ConsumeParams, listener: ConsumeResponseListener) {}
        override fun createAlternativeBillingOnlyReportingDetailsAsync(listener: AlternativeBillingOnlyReportingDetailsListener) {}
        override fun createBillingProgramReportingDetailsAsync(params: BillingProgramReportingDetailsParams, listener: BillingProgramReportingDetailsListener) {}
        override fun createExternalOfferReportingDetailsAsync(listener: ExternalOfferReportingDetailsListener) {}
        override fun getBillingChoiceInfoAsync(params: GetBillingChoiceInfoParams, listener: BillingChoiceInfoResponseListener) {}
        override fun getBillingConfigAsync(params: GetBillingConfigParams, listener: BillingConfigResponseListener) {}
        override fun isAlternativeBillingOnlyAvailableAsync(listener: AlternativeBillingOnlyAvailabilityListener) {}
        override fun isBillingProgramAvailableAsync(accountType: Int, listener: BillingProgramAvailabilityListener) {}
        override fun isExternalOfferAvailableAsync(listener: ExternalOfferAvailabilityListener) {}
        override fun launchExternalLink(activity: android.app.Activity, params: LaunchExternalLinkParams, listener: LaunchExternalLinkResponseListener) {}
        override fun queryProductDetailsAsync(params: QueryProductDetailsParams, listener: ProductDetailsResponseListener) {}
        override fun queryPurchasesAsync(params: QueryPurchasesParams, listener: PurchasesResponseListener) {}
        override fun showBillingProgramInformationDialog(activity: android.app.Activity, params: BillingProgramInformationDialogParams, listener: BillingProgramInformationDialogListener) {}
    }

    private fun createPurchase(
        productId: String,
        state: Int,
        token: String = "token_${System.currentTimeMillis()}",
        acknowledged: Boolean = true,
        autoRenewing: Boolean = true
    ): Purchase {
        val rawJsonState = if (state == Purchase.PurchaseState.PENDING) 4 else 1
        val json = """
            {
                "orderId": "GPA.test-1234",
                "packageName": "com.fit10.app",
                "productId": "$productId",
                "purchaseTime": 1700000000000,
                "purchaseState": $rawJsonState,
                "purchaseToken": "$token",
                "acknowledged": $acknowledged,
                "autoRenewing": $autoRenewing
            }
        """.trimIndent()
        return Purchase(json, "sig")
    }

    @Test
    fun localVerifier_validatesTokenCorrectly() = runBlocking {
        val verifier = LocalEntitlementVerifier()
        assertTrue(verifier.verifyPurchase("test_token_123", BillingConstants.PRODUCT_ID_ANNUAL))
        assertTrue(verifier.verifyPurchase("test_token_123", BillingConstants.PRODUCT_ID_MONTHLY))
        assertFalse(verifier.verifyPurchase("", BillingConstants.PRODUCT_ID_ANNUAL))
        assertFalse(verifier.verifyPurchase("test_token_123", "unknown_sku"))
    }

    @Test
    fun inMemoryCache_savesAndLoadsState() {
        val cache = InMemoryEntitlementCache()
        val initial = cache.load()
        assertEquals(SubscriptionState.FREE, initial.subscriptionState)

        val updated = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            planId = BillingConstants.PRODUCT_ID_ANNUAL,
            planName = "Fit10 Pro (Annual Plan)",
            isAutoRenewing = true,
            lastVerifiedEpochMs = 123456789L
        )
        cache.save(updated)

        val loaded = cache.load()
        assertEquals(SubscriptionState.ACTIVE, loaded.subscriptionState)
        assertEquals(BillingConstants.PRODUCT_ID_ANNUAL, loaded.planId)
        assertEquals(123456789L, loaded.lastVerifiedEpochMs)
    }

    @Test
    fun pendingPurchases_neverGrantProAccess() {
        val cache = InMemoryEntitlementCache()
        val fakeBilling = FakeBillingClient()
        val repo = BillingRepositoryImpl(
            context = null,
            scope = CoroutineScope(Dispatchers.Unconfined),
            cache = cache,
            billingClientProvider = { fakeBilling }
        )

        // PurchaseState.PENDING is 2
        val pendingPurchase = createPurchase(
            productId = BillingConstants.PRODUCT_ID_ANNUAL,
            state = Purchase.PurchaseState.PENDING,
            acknowledged = false
        )

        repo.processPurchases(listOf(pendingPurchase))

        val currentEntitlement = repo.entitlement.value
        assertEquals("Pending purchase must not unlock Pro", SubscriptionState.FREE, currentEntitlement.subscriptionState)
        assertFalse(currentEntitlement.isPro())
        assertEquals("Pending purchase must not be auto-acknowledged", 0, fakeBilling.acknowledgedTokens.size)
    }

    @Test
    fun activePurchase_grantsProAccessAndAcknowledges() {
        val cache = InMemoryEntitlementCache()
        val fakeBilling = FakeBillingClient()
        val repo = BillingRepositoryImpl(
            context = null,
            scope = CoroutineScope(Dispatchers.Unconfined),
            cache = cache,
            billingClientProvider = { fakeBilling }
        )

        // PurchaseState.PURCHASED is 1, not yet acknowledged
        val activePurchase = createPurchase(
            productId = BillingConstants.PRODUCT_ID_ANNUAL,
            state = Purchase.PurchaseState.PURCHASED,
            token = "token_ack_test",
            acknowledged = false,
            autoRenewing = true
        )

        repo.processPurchases(listOf(activePurchase))

        val currentEntitlement = repo.entitlement.value
        assertEquals("Purchased state must unlock Pro", SubscriptionState.ACTIVE, currentEntitlement.subscriptionState)
        assertTrue(currentEntitlement.isPro())
        assertEquals(BillingConstants.PRODUCT_ID_ANNUAL, currentEntitlement.planId)
        assertTrue("Unacknowledged purchase must be acknowledged", fakeBilling.acknowledgedTokens.contains("token_ack_test"))
        assertEquals("Cache must be updated", SubscriptionState.ACTIVE, cache.load().subscriptionState)
    }

    @Test
    fun activePurchase_nonAutoRenewing_setsCanceledActive() {
        val cache = InMemoryEntitlementCache()
        val fakeBilling = FakeBillingClient()
        val repo = BillingRepositoryImpl(
            context = null,
            scope = CoroutineScope(Dispatchers.Unconfined),
            cache = cache,
            billingClientProvider = { fakeBilling }
        )

        val nonRenewingPurchase = createPurchase(
            productId = BillingConstants.PRODUCT_ID_MONTHLY,
            state = Purchase.PurchaseState.PURCHASED,
            acknowledged = true,
            autoRenewing = false
        )

        repo.processPurchases(listOf(nonRenewingPurchase))

        val currentEntitlement = repo.entitlement.value
        assertEquals("Canceled but active subscription should be CANCELED_ACTIVE", SubscriptionState.CANCELED_ACTIVE, currentEntitlement.subscriptionState)
        assertTrue("Canceled active subscription still enjoys Pro benefits", currentEntitlement.isPro())
        assertFalse(currentEntitlement.isAutoRenewing)
    }

    @Test
    fun emptyPurchases_revokesProAccess() {
        val cache = InMemoryEntitlementCache(
            cached = EntitlementInfo(
                subscriptionState = SubscriptionState.ACTIVE,
                planId = BillingConstants.PRODUCT_ID_ANNUAL
            )
        )
        val fakeBilling = FakeBillingClient()
        val repo = BillingRepositoryImpl(
            context = null,
            scope = CoroutineScope(Dispatchers.Unconfined),
            cache = cache,
            billingClientProvider = { fakeBilling }
        )

        repo.processPurchases(emptyList())

        val currentEntitlement = repo.entitlement.value
        assertEquals(SubscriptionState.FREE, currentEntitlement.subscriptionState)
        assertFalse(currentEntitlement.isPro())
        assertEquals(SubscriptionState.FREE, cache.load().subscriptionState)
    }

    @Test
    fun customVerifier_rejectionDoesNotGrantPro() {
        val cache = InMemoryEntitlementCache()
        val fakeBilling = FakeBillingClient()
        val rejectingVerifier = object : EntitlementVerifier {
            override suspend fun verifyPurchase(purchaseToken: String, productId: String): Boolean = false
        }

        val repo = BillingRepositoryImpl(
            context = null,
            scope = CoroutineScope(Dispatchers.Unconfined),
            verifier = rejectingVerifier,
            cache = cache,
            billingClientProvider = { fakeBilling }
        )

        val purchase = createPurchase(
            productId = BillingConstants.PRODUCT_ID_ANNUAL,
            state = Purchase.PurchaseState.PURCHASED
        )

        repo.processPurchases(listOf(purchase))

        val currentEntitlement = repo.entitlement.value
        assertEquals("Rejected verification must result in FREE tier", SubscriptionState.FREE, currentEntitlement.subscriptionState)
        assertFalse(currentEntitlement.isPro())
    }
}
