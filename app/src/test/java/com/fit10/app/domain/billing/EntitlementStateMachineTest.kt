package com.fit10.app.domain.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementStateMachineTest {

    @Test
    fun subscriptionState_entitlementFlags() {
        assertTrue(SubscriptionState.ACTIVE.isEntitled)
        assertTrue(SubscriptionState.IN_TRIAL.isEntitled)
        assertTrue(SubscriptionState.GRACE_PERIOD.isEntitled)
        assertTrue(SubscriptionState.CANCELED_ACTIVE.isEntitled)

        assertFalse(SubscriptionState.FREE.isEntitled)
        assertFalse(SubscriptionState.ON_HOLD.isEntitled)
        assertFalse(SubscriptionState.EXPIRED.isEntitled)
    }

    @Test
    fun entitlementInfo_activeWithinOfflineGraceWindow_isPro() {
        val now = 1_000_000_000L
        val verifiedRecently = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            lastVerifiedEpochMs = now - (2 * 24 * 60 * 60 * 1000L) // 2 days ago
        )
        assertTrue(verifiedRecently.isPro(currentTimeMs = now))
    }

    @Test
    fun entitlementInfo_exceedingOfflineGraceWindow_revokesProUntilReverified() {
        val now = 1_000_000_000L
        val eightDaysAgo = now - (8 * 24 * 60 * 60 * 1000L) // 8 days > 7 days window
        val staleEntitlement = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            lastVerifiedEpochMs = eightDaysAgo
        )
        assertFalse("Offline grace period should expire after 7 days", staleEntitlement.isPro(currentTimeMs = now))
    }

    @Test
    fun entitlementInfo_exactGraceBoundary() {
        val now = 10_000_000_000L
        val exactlySevenDays = now - BillingConstants.OFFLINE_ENTITLEMENT_GRACE_WINDOW_MS
        val boundaryEntitlement = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            lastVerifiedEpochMs = exactlySevenDays
        )
        assertTrue("Exactly at 7-day boundary should still be valid", boundaryEntitlement.isPro(currentTimeMs = now))

        val oneMsPastSevenDays = now - (BillingConstants.OFFLINE_ENTITLEMENT_GRACE_WINDOW_MS + 1)
        val expiredBoundaryEntitlement = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            lastVerifiedEpochMs = oneMsPastSevenDays
        )
        assertFalse("Past 7-day boundary must require network verification", expiredBoundaryEntitlement.isPro(currentTimeMs = now))
    }

    @Test
    fun entitlementInfo_explicitExpiryEpochPassed_revokesPro() {
        val now = 1_000_000_000L
        val pastExpiry = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            expiryEpochMs = now - 10_000L,
            lastVerifiedEpochMs = now - 1_000L
        )
        assertFalse(pastExpiry.isPro(currentTimeMs = now))

        val futureExpiry = EntitlementInfo(
            subscriptionState = SubscriptionState.ACTIVE,
            expiryEpochMs = now + 100_000L,
            lastVerifiedEpochMs = now - 1_000L
        )
        assertTrue(futureExpiry.isPro(currentTimeMs = now))
    }
}
