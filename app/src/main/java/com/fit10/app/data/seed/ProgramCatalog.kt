package com.fit10.app.data.seed

import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.ProgramDay

object ProgramCatalog {

    /**
     * FIX PC-01: FULL_BODY_IGNITION no longer repeated 3× — Week 2 now uses dedicated
     *             progressive overload workouts (FULL_BODY_POWER, CORE_ENDURANCE, etc.).
     *
     * FIX PC-02 / PC-03: Consecutive fatigue overlap eliminated.
     *   OLD:  Day 8 Full Body → Day 9 Core → Day 10 Lower Body  (3-day core/quad fatigue chain)
     *   NEW:  Day 8 Full Body → Day 9 REST → Day 10 Core → Day 11 Lower Body
     *         This inserts a recovery buffer between Full Body and the Core/Lower days.
     */
    val KICKSTART_14_DAY = Program(
        id = "prog_14_day_kickstart",
        title = "14-Day Fit10 Kickstart",
        description = "A progressive 2-week introduction to daily fitness. Week 1 builds foundational movement patterns; Week 2 introduces progressive overload across the same splits.",
        goal = FitnessGoal.IMPROVE_FITNESS,
        fitnessLevel = FitnessLevel.BEGINNER,
        totalDays = 14,
        daysPerWeek = 5,
        estimatedWeeks = 2,
        days = listOf(
            // ── WEEK 1: Foundation ──────────────────────────────────────────────
            ProgramDay(dayNumber = 1,  title = "Day 1: Full Body Ignition",    isRestDay = false, workout = WorkoutCatalog.FULL_BODY_IGNITION),
            ProgramDay(dayNumber = 2,  title = "Day 2: Core Fundamentals",     isRestDay = false, workout = WorkoutCatalog.CORE_SHRED),
            ProgramDay(dayNumber = 3,  title = "Day 3: Lower Body Tone",       isRestDay = false, workout = WorkoutCatalog.LOWER_BODY_BURN),
            ProgramDay(dayNumber = 4,  title = "Day 4: Active Recovery",       isRestDay = true,  workout = null),
            ProgramDay(dayNumber = 5,  title = "Day 5: Upper Body Sculpt",     isRestDay = false, workout = WorkoutCatalog.UPPER_BODY_SCULPT),
            ProgramDay(dayNumber = 6,  title = "Day 6: Mobility Reset",        isRestDay = false, workout = WorkoutCatalog.DAILY_MOBILITY_RESET),
            ProgramDay(dayNumber = 7,  title = "Day 7: Rest & Recharge",       isRestDay = true,  workout = null),

            // ── WEEK 2: Progressive Overload ────────────────────────────────────
            // FIX PC-02: Rest day inserted after Full Body Power to break the
            // Full Body → Core fatigue chain that existed in the original program.
            ProgramDay(dayNumber = 8,  title = "Day 8: Full Body Power",       isRestDay = false, workout = WorkoutCatalog.FULL_BODY_POWER),
            ProgramDay(dayNumber = 9,  title = "Day 9: Active Recovery",       isRestDay = true,  workout = null),
            ProgramDay(dayNumber = 10, title = "Day 10: Core Endurance",       isRestDay = false, workout = WorkoutCatalog.CORE_ENDURANCE),
            ProgramDay(dayNumber = 11, title = "Day 11: Lower Body Burn",      isRestDay = false, workout = WorkoutCatalog.LOWER_BODY_BURN_V2),
            ProgramDay(dayNumber = 12, title = "Day 12: Upper Body Strength",  isRestDay = false, workout = WorkoutCatalog.UPPER_BODY_STRENGTH),
            ProgramDay(dayNumber = 13, title = "Day 13: Full Body Challenge",  isRestDay = false, workout = WorkoutCatalog.FULL_BODY_CHALLENGE),
            ProgramDay(dayNumber = 14, title = "Day 14: Finish Strong — Mobility", isRestDay = false, workout = WorkoutCatalog.DAILY_MOBILITY_RESET)
        )
    )

    /**
     * Intermediate 14-day program (FIX PC-05).
     * Starts directly from Week-2-level workouts and applies a further intensity ramp.
     */
    val INTERMEDIATE_14_DAY = Program(
        id = "prog_14_day_intermediate",
        title = "14-Day Fit10 Intermediate",
        description = "A 2-week intermediate program built for users who have completed the Kickstart. All sessions begin at the Week-2 difficulty level with denser exercise selection.",
        goal = FitnessGoal.BUILD_STRENGTH,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        totalDays = 14,
        daysPerWeek = 5,
        estimatedWeeks = 2,
        days = listOf(
            ProgramDay(dayNumber = 1,  title = "Day 1: Full Body Power",       isRestDay = false, workout = WorkoutCatalog.FULL_BODY_POWER),
            ProgramDay(dayNumber = 2,  title = "Day 2: Core Endurance",        isRestDay = false, workout = WorkoutCatalog.CORE_ENDURANCE),
            ProgramDay(dayNumber = 3,  title = "Day 3: Lower Body Burn",       isRestDay = false, workout = WorkoutCatalog.LOWER_BODY_BURN_V2),
            ProgramDay(dayNumber = 4,  title = "Day 4: Active Recovery",       isRestDay = true,  workout = null),
            ProgramDay(dayNumber = 5,  title = "Day 5: Upper Body Strength",   isRestDay = false, workout = WorkoutCatalog.UPPER_BODY_STRENGTH),
            ProgramDay(dayNumber = 6,  title = "Day 6: Mobility Reset",        isRestDay = false, workout = WorkoutCatalog.DAILY_MOBILITY_RESET),
            ProgramDay(dayNumber = 7,  title = "Day 7: Rest & Recharge",       isRestDay = true,  workout = null),
            ProgramDay(dayNumber = 8,  title = "Day 8: Full Body Challenge",   isRestDay = false, workout = WorkoutCatalog.FULL_BODY_CHALLENGE),
            ProgramDay(dayNumber = 9,  title = "Day 9: Active Recovery",       isRestDay = true,  workout = null),
            ProgramDay(dayNumber = 10, title = "Day 10: Core Endurance",       isRestDay = false, workout = WorkoutCatalog.CORE_ENDURANCE),
            ProgramDay(dayNumber = 11, title = "Day 11: Lower Body Burn",      isRestDay = false, workout = WorkoutCatalog.LOWER_BODY_BURN_V2),
            ProgramDay(dayNumber = 12, title = "Day 12: Upper Body Strength",  isRestDay = false, workout = WorkoutCatalog.UPPER_BODY_STRENGTH),
            ProgramDay(dayNumber = 13, title = "Day 13: Full Body Power",      isRestDay = false, workout = WorkoutCatalog.FULL_BODY_POWER),
            ProgramDay(dayNumber = 14, title = "Day 14: Finish Strong — Mobility", isRestDay = false, workout = WorkoutCatalog.DAILY_MOBILITY_RESET)
        )
    )

    val ALL: List<Program> = listOf(
        KICKSTART_14_DAY,
        INTERMEDIATE_14_DAY
    )
}
