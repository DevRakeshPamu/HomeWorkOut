package com.fit10.app.domain.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureGateTest {

    private val freeEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.FREE
    )

    private val activeProEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.ACTIVE,
        planId = BillingConstants.PRODUCT_ID_ANNUAL,
        isAutoRenewing = true
    )

    private val trialProEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.IN_TRIAL,
        planId = BillingConstants.PRODUCT_ID_ANNUAL,
        isAutoRenewing = true
    )

    private val gracePeriodEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.GRACE_PERIOD,
        planId = BillingConstants.PRODUCT_ID_ANNUAL
    )

    private val onHoldEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.ON_HOLD,
        planId = BillingConstants.PRODUCT_ID_ANNUAL
    )

    private val expiredEntitlement = EntitlementInfo(
        subscriptionState = SubscriptionState.EXPIRED,
        planId = BillingConstants.PRODUCT_ID_ANNUAL
    )

    @Test
    fun freeTier_hasAccessToAllCoreFeatures() {
        val freeFeatures = listOf(
            ProFeature.STANDARD_WORKOUTS,
            ProFeature.DAILY_RECOMMENDATION,
            ProFeature.BASIC_HISTORY_AND_STREAKS,
            ProFeature.REMINDERS_AND_EXPORT,
            ProFeature.BASIC_EXERCISE_SWAP,
            ProFeature.PROGRAM_PREVIEW
        )

        freeFeatures.forEach { feature ->
            assertTrue(
                "Feature $feature should be unlocked on free tier",
                FeatureGate.isUnlocked(feature, freeEntitlement)
            )
        }
    }

    @Test
    fun freeTier_isLockedOutOfProFeatures() {
        val proFeatures = listOf(
            ProFeature.FULL_PROGRAMS,
            ProFeature.FULL_EXERCISE_REGRESSION,
            ProFeature.ADAPTIVE_DIFFICULTY,
            ProFeature.ADVANCED_ANALYTICS,
            ProFeature.STREAK_REPAIR,
            ProFeature.WORKOUT_VARIANTS
        )

        proFeatures.forEach { feature ->
            assertFalse(
                "Feature $feature must be locked on free tier",
                FeatureGate.isUnlocked(feature, freeEntitlement)
            )
        }
    }

    @Test
    fun proTier_hasAccessToAllFeatures() {
        ProFeature.entries.forEach { feature ->
            assertTrue(
                "Active Pro user must unlock feature $feature",
                FeatureGate.isUnlocked(feature, activeProEntitlement)
            )
            assertTrue(
                "Trial Pro user must unlock feature $feature",
                FeatureGate.isUnlocked(feature, trialProEntitlement)
            )
            assertTrue(
                "Grace period user must unlock feature $feature",
                FeatureGate.isUnlocked(feature, gracePeriodEntitlement)
            )
        }
    }

    @Test
    fun onHoldAndExpired_loseProAccess() {
        assertFalse(FeatureGate.isUnlocked(ProFeature.FULL_PROGRAMS, onHoldEntitlement))
        assertFalse(FeatureGate.isUnlocked(ProFeature.FULL_PROGRAMS, expiredEntitlement))
        assertFalse(FeatureGate.isUnlocked(ProFeature.FULL_EXERCISE_REGRESSION, onHoldEntitlement))
        assertFalse(FeatureGate.isUnlocked(ProFeature.ADAPTIVE_DIFFICULTY, expiredEntitlement))
    }

    @Test
    fun programDays_previewGating() {
        // Days 1, 2, 3 must be unlocked for free users
        assertTrue(FeatureGate.isProgramDayUnlocked(1, freeEntitlement))
        assertTrue(FeatureGate.isProgramDayUnlocked(2, freeEntitlement))
        assertTrue(FeatureGate.isProgramDayUnlocked(3, freeEntitlement))

        // Day 4 and beyond must be locked for free users
        assertFalse(FeatureGate.isProgramDayUnlocked(4, freeEntitlement))
        assertFalse(FeatureGate.isProgramDayUnlocked(5, freeEntitlement))
        assertFalse(FeatureGate.isProgramDayUnlocked(14, freeEntitlement))

        // Pro users have all days unlocked
        for (day in 1..14) {
            assertTrue(FeatureGate.isProgramDayUnlocked(day, activeProEntitlement))
        }
    }

    @Test
    fun exerciseSwap_basicVsFullRegressionGating() {
        // Basic swaps (high-impact to low-impact) are unlocked for free users
        assertTrue(FeatureGate.isExerciseSwapUnlocked(isBasicSwap = true, freeEntitlement))

        // Full joint-friendly regression engine requires Pro
        assertFalse(FeatureGate.isExerciseSwapUnlocked(isBasicSwap = false, freeEntitlement))

        // Pro users have all swaps unlocked
        assertTrue(FeatureGate.isExerciseSwapUnlocked(isBasicSwap = true, activeProEntitlement))
        assertTrue(FeatureGate.isExerciseSwapUnlocked(isBasicSwap = false, activeProEntitlement))
    }
}
