package com.fit10.app.domain.repository

import android.app.Activity
import com.fit10.app.domain.billing.EntitlementInfo
import com.fit10.app.domain.billing.SubscriptionPlanDetails
import kotlinx.coroutines.flow.StateFlow

interface BillingRepository {
    /** Observable stream of user's active entitlement. */
    val entitlement: StateFlow<EntitlementInfo>

    /** Available subscription plans with dynamic prices from Google Play. */
    val subscriptionPlans: StateFlow<List<SubscriptionPlanDetails>>

    /** True if billing operations or product detail queries are currently in flight. */
    val isLoading: StateFlow<Boolean>

    /** Refreshes purchases from Google Play (e.g., on app start or onResume). */
    suspend fun refreshPurchases(): Result<Unit>

    /** Initiates Google Play purchase flow for the selected plan. */
    suspend fun launchBillingFlow(activity: Activity, plan: SubscriptionPlanDetails): Result<Unit>

    /** Restores past purchases from Google Play. Returns true if an active Pro entitlement was restored. */
    suspend fun restorePurchases(): Result<Boolean>

    /** Retries connecting to the Google Play Billing service if disconnected. */
    suspend fun retryBillingConnection()
}
