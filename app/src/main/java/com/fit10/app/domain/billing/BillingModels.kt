package com.fit10.app.domain.billing

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Standard SKU IDs for Google Play In-App Subscriptions.
 */
object BillingConstants {
    const val PRODUCT_ID_ANNUAL = "fit10_pro_annual"
    const val PRODUCT_ID_MONTHLY = "fit10_pro_monthly"

    /** Bounded offline grace period (7 days) before subscription must be re-verified with Play Store. */
    const val OFFLINE_ENTITLEMENT_GRACE_WINDOW_MS = 7L * 24 * 60 * 60 * 1000

    /** Free program preview limit: only the first 3 days of Kickstart are free. */
    const val FREE_PROGRAM_DAY_LIMIT = 3
}

/**
 * Detailed subscription lifecycle states following Google Play Billing specifications.
 */
enum class SubscriptionState {
    /** Free user with no active entitlement. */
    FREE,

    /** Active subscription with recurring billing enabled. */
    ACTIVE,

    /** Active subscription within its initial free-trial period (e.g., 7 days). */
    IN_TRIAL,

    /** Payment failed; Google Play is attempting to charge the user (benefits kept active). */
    GRACE_PERIOD,

    /** Grace period ended without successful payment; benefits temporarily suspended. */
    ON_HOLD,

    /** User canceled recurring auto-renewal, but subscription period remains active until expiry. */
    CANCELED_ACTIVE,

    /** Subscription period has terminated and access is revoked. */
    EXPIRED;

    val isEntitled: Boolean
        get() = this == ACTIVE || this == IN_TRIAL || this == GRACE_PERIOD || this == CANCELED_ACTIVE
}

/**
 * Domain representation of user's current entitlement state.
 */
data class EntitlementInfo(
    val subscriptionState: SubscriptionState = SubscriptionState.FREE,
    val planId: String? = null,
    val planName: String? = null,
    val expiryEpochMs: Long? = null,
    val isAutoRenewing: Boolean = false,
    val lastVerifiedEpochMs: Long = System.currentTimeMillis()
) {
    /**
     * Determines whether Pro features are unlocked, respecting the 7-day offline grace window.
     */
    fun isPro(currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        if (!subscriptionState.isEntitled) return false

        // Check if offline cache has exceeded the bounded grace window
        val isOfflineGraceValid = (currentTimeMs - lastVerifiedEpochMs) <= BillingConstants.OFFLINE_ENTITLEMENT_GRACE_WINDOW_MS
        if (!isOfflineGraceValid) return false

        // Check explicit expiry if present
        return expiryEpochMs == null || currentTimeMs < expiryEpochMs
    }
}

/**
 * Formatted details for a subscription plan displayed on the paywall.
 */
data class SubscriptionPlanDetails(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val currencyCode: String,
    val billingPeriod: String, // e.g. "P1Y", "P1M"
    val hasFreeTrial: Boolean = false,
    val freeTrialDays: Int = 0,
    val formattedMonthlyEquivalent: String,
    val savingsPercentage: Int? = null,
    val isFallbackSkeleton: Boolean = false
) {
    companion object {
        /**
         * Calculates the monthly equivalent formatted price from annual micros and currency.
         * Example: ₹499/yr -> annualMicros = 499_000_000L / 12 = 41_583_333L -> ₹41.58 / mo.
         */
        fun computeMonthlyEquivalent(
            annualMicros: Long,
            currencyCode: String
        ): String {
            val monthlyMicros = annualMicros / 12.0
            val monthlyAmount = monthlyMicros / 1_000_000.0
            return try {
                val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                format.currency = Currency.getInstance(currencyCode)
                format.maximumFractionDigits = 2
                format.minimumFractionDigits = 2
                format.format(monthlyAmount)
            } catch (e: Exception) {
                String.format(Locale.US, "%.2f %s", monthlyAmount, currencyCode)
            }
        }

        /**
         * Calculates percentage saved with the annual plan compared to 12 months of the monthly plan.
         */
        fun computeSavingsPercentage(annualMicros: Long, monthlyMicros: Long): Int {
            val twelveMonthsMicros = monthlyMicros * 12.0
            if (twelveMonthsMicros <= 0.0) return 0
            val savings = ((twelveMonthsMicros - annualMicros) / twelveMonthsMicros) * 100.0
            return savings.coerceIn(0.0, 100.0).toInt()
        }

        /**
         * Default fallback skeleton prices for offline display (explicitly flagged as fallback).
         */
        val FALLBACK_ANNUAL = SubscriptionPlanDetails(
            productId = BillingConstants.PRODUCT_ID_ANNUAL,
            title = "Annual Pass",
            formattedPrice = "₹499",
            priceAmountMicros = 499_000_000L,
            currencyCode = "INR",
            billingPeriod = "P1Y",
            hasFreeTrial = true,
            freeTrialDays = 7,
            formattedMonthlyEquivalent = "₹41.58",
            savingsPercentage = 72,
            isFallbackSkeleton = true
        )

        val FALLBACK_MONTHLY = SubscriptionPlanDetails(
            productId = BillingConstants.PRODUCT_ID_MONTHLY,
            title = "Monthly Pass",
            formattedPrice = "₹149",
            priceAmountMicros = 149_000_000L,
            currencyCode = "INR",
            billingPeriod = "P1M",
            hasFreeTrial = false,
            freeTrialDays = 0,
            formattedMonthlyEquivalent = "₹149.00",
            savingsPercentage = null,
            isFallbackSkeleton = true
        )
    }
}

/**
 * Interface for pluggable client-side or optional server-side receipt validation.
 */
interface EntitlementVerifier {
    suspend fun verifyPurchase(purchaseToken: String, productId: String): Boolean
}

/**
 * Default local-only verifier used when no backend server is configured.
 */
class LocalEntitlementVerifier : EntitlementVerifier {
    override suspend fun verifyPurchase(purchaseToken: String, productId: String): Boolean {
        return purchaseToken.isNotBlank() && (productId == BillingConstants.PRODUCT_ID_ANNUAL || productId == BillingConstants.PRODUCT_ID_MONTHLY)
    }
}
