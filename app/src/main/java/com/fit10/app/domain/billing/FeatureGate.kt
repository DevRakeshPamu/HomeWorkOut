package com.fit10.app.domain.billing

/**
 * Single source of truth for feature entitlement gating across Fit10.
 * Replaces scattered isPro checks with centralized logic.
 */
object FeatureGate {

    /**
     * Checks if a given feature is unlocked under the user's current entitlement.
     */
    fun isUnlocked(
        feature: ProFeature,
        entitlement: EntitlementInfo,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        return when (feature) {
            // Free Tier features are always accessible
            ProFeature.STANDARD_WORKOUTS,
            ProFeature.DAILY_RECOMMENDATION,
            ProFeature.BASIC_HISTORY_AND_STREAKS,
            ProFeature.REMINDERS_AND_EXPORT,
            ProFeature.BASIC_EXERCISE_SWAP,
            ProFeature.PROGRAM_PREVIEW -> true

            // Pro Tier features require an active entitlement within the valid grace window
            ProFeature.FULL_PROGRAMS,
            ProFeature.FULL_EXERCISE_REGRESSION,
            ProFeature.ADAPTIVE_DIFFICULTY,
            ProFeature.ADVANCED_ANALYTICS,
            ProFeature.STREAK_REPAIR,
            ProFeature.WORKOUT_VARIANTS -> entitlement.isPro(currentTimeMs)
        }
    }

    /**
     * Program days 1–3 are free preview days on Kickstart; Day 4+ requires Pro.
     */
    fun isProgramDayUnlocked(
        dayNumber: Int,
        entitlement: EntitlementInfo,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        if (dayNumber <= BillingConstants.FREE_PROGRAM_DAY_LIMIT) return true
        return isUnlocked(ProFeature.FULL_PROGRAMS, entitlement, currentTimeMs)
    }

    /**
     * Basic high-impact swaps (jumping jacks, mountain climbers, high knees) are free;
     * full regression engine with custom substitutions requires Pro.
     */
    fun isExerciseSwapUnlocked(
        isBasicSwap: Boolean,
        entitlement: EntitlementInfo,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        if (isBasicSwap) return true
        return isUnlocked(ProFeature.FULL_EXERCISE_REGRESSION, entitlement, currentTimeMs)
    }
}
