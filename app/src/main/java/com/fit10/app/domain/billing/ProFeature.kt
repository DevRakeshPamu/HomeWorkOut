package com.fit10.app.domain.billing

/**
 * Enumerates all feature gates across Fit10 to enforce a consistent Free vs. Pro entitlement split.
 */
enum class ProFeature {
    // ── Free Tier Capabilities ───────────────────────────────────────
    /** All 10 Foundation & Progressive Overload 10-minute routines. */
    STANDARD_WORKOUTS,

    /** Basic daily workout recommendation via PersonalizationEngine. */
    DAILY_RECOMMENDATION,

    /** Completed workout history, durations, calories, and streak tracking. */
    BASIC_HISTORY_AND_STREAKS,

    /** Daily push reminders and local workout data export/backup. */
    REMINDERS_AND_EXPORT,

    /** Basic high-impact swaps (e.g., Jumping Jacks → Arm Circles, High Knees → Glute Bridges). */
    BASIC_EXERCISE_SWAP,

    /** Free program trial preview: Days 1–3 of the 14-Day Kickstart. */
    PROGRAM_PREVIEW,

    // ── Pro Tier Capabilities (Fit10 Pro) ─────────────────────────────
    /** Full multi-week guided tracks (Kickstart Day 4+ and Intermediate). */
    FULL_PROGRAMS,

    /** Full joint-friendly regression engine across all exercises and injury/limitation profiles. */
    FULL_EXERCISE_REGRESSION,

    /** Adaptive difficulty between sessions based on user feedback and completion rates. */
    ADAPTIVE_DIFFICULTY,

    /** Advanced analytics, muscle volume balance distributions, and fatigue tracking. */
    ADVANCED_ANALYTICS,

    /** Streak freeze and historical streak restoration. */
    STREAK_REPAIR,

    /** Equipment-specific micro-workouts and 5-min / 15-min duration variants. */
    WORKOUT_VARIANTS
}
