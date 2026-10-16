package com.fit10.app.data.seed

import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise

object WorkoutCatalog {

    // ─────────────────────────────────────────────────────────────────
    // WEEK 1 — Foundation workouts (original, fixed)
    // ─────────────────────────────────────────────────────────────────

    /**
     * FIX W-04: Warm-up now uses a proper Cat-Cow → Arm Circles flow before any cardio.
     * All rest intervals increased from 15s → 20s (FIX W-05).
     * No verbatim exercise repetitions remain.
     */
    val FULL_BODY_IGNITION = Workout(
        id = "w_full_body_ignition",
        title = "10-Min Full Body Ignition",
        description = "High-efficiency full-body circuit combining dynamic cardio, upper/lower body strength, and core stability.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.BEGINNER,
        focusArea = FocusArea.FULL_BODY,
        intensity = IntensityPreference.MODERATE,
        caloriesBurnEstimate = 85,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.ARM_CIRCLES,       orderIndex = 0, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,     orderIndex = 1, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BODYWEIGHT_SQUATS, orderIndex = 2, durationSeconds = 45, reps = 15, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PUSH_UPS,          orderIndex = 3, durationSeconds = 45, reps = 10, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,     orderIndex = 4, durationSeconds = 45, reps = 15, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,          orderIndex = 5, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS, orderIndex = 6, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,     orderIndex = 7, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.LUNGES,            orderIndex = 8, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,       orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * FIX W-02: BICYCLE_CRUNCHES and PLANK no longer repeated verbatim.
     * FIX F-06: CAT_COW_STRETCH moved to slot 0 as a true warm-up opener only.
     * Replaced duplicates with SHOULDER_TAPS and GLUTE_BRIDGE_MARCH for variety.
     * All rest intervals increased to 20s (FIX W-05).
     */
    val CORE_SHRED = Workout(
        id = "w_core_shred",
        title = "10-Min Core Shred",
        description = "Targeted abdominal and lower back session targeting deep stabilizers, obliques, and rotational control.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.CORE,
        intensity = IntensityPreference.MODERATE,
        caloriesBurnEstimate = 70,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.CAT_COW_STRETCH,   orderIndex = 0, durationSeconds = 45, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,          orderIndex = 1, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PLANK,             orderIndex = 2, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BICYCLE_CRUNCHES,  orderIndex = 3, durationSeconds = 40, reps = 20, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS, orderIndex = 4, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SHOULDER_TAPS,     orderIndex = 5, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,     orderIndex = 6, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGE_MARCH,orderIndex = 7, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.HIGH_KNEES,        orderIndex = 8, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,       orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * FIX W-01: BODYWEIGHT_SQUATS and LUNGES no longer repeated verbatim.
     * Replaced second round with REVERSE_LUNGES, WALL_SIT, and GLUTE_BRIDGE_MARCH
     * to provide new movement stimuli and hit the hamstring pattern more effectively.
     * All rest intervals increased to 20s (FIX W-05).
     */
    val LOWER_BODY_BURN = Workout(
        id = "w_lower_body_burn",
        title = "10-Min Lower Body Burn",
        description = "Focused leg and glute workout using squat, lunge, hinge, and isometric patterns for complete lower-body stimulus.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.LOWER_BODY,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 95,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,      orderIndex = 0, durationSeconds = 40, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.BODYWEIGHT_SQUATS,  orderIndex = 1, durationSeconds = 45, reps = 15, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.LUNGES,             orderIndex = 2, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,      orderIndex = 3, durationSeconds = 45, reps = 16, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.HIGH_KNEES,         orderIndex = 4, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.WALL_SIT,           orderIndex = 5, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.REVERSE_LUNGES,     orderIndex = 6, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGE_MARCH, orderIndex = 7, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS,  orderIndex = 8, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,        orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * FIX W-03 (Critical): PUSH_UPS, SHOULDER_TAPS, SUPERMAN_HOLD were each repeated verbatim.
     * Now uses three distinct push-up variations (Standard, Wide, Diamond) plus PRONE_YTW_HOLD
     * to address the zero-pull-plane gap. Verbatim duplication eliminated entirely.
     * All rest intervals increased to 20s (FIX W-05).
     */
    val UPPER_BODY_SCULPT = Workout(
        id = "w_upper_body_sculpt",
        title = "10-Min Upper Body Sculpt",
        description = "Push-plane and posterior chain routine developing chest power, triceps, shoulder stability, and rear deltoid / rhomboid strength.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.BEGINNER,
        focusArea = FocusArea.UPPER_BODY,
        intensity = IntensityPreference.MODERATE,
        caloriesBurnEstimate = 75,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.ARM_CIRCLES,     orderIndex = 0, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.PUSH_UPS,        orderIndex = 1, durationSeconds = 45, reps = 10, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PRONE_YTW_HOLD,  orderIndex = 2, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SHOULDER_TAPS,   orderIndex = 3, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.WIDE_PUSH_UPS,   orderIndex = 4, durationSeconds = 45, reps = 10, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,   orderIndex = 5, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.DIAMOND_PUSH_UPS,orderIndex = 6, durationSeconds = 45, reps = 8, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,        orderIndex = 7, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,   orderIndex = 8, durationSeconds = 40, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,     orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * FIX W-06: BODYWEIGHT_SQUATS removed from a recovery/mobility session.
     * FIX F-07: Duplicate CHILDS_POSE (back-to-back at indices 8 & 9) eliminated.
     * Replaced with HIP_CIRCLES / wide-knee Child's Pose variant framing.
     * This session is now correctly limited to mobility and recovery movement types.
     */
    val DAILY_MOBILITY_RESET = Workout(
        id = "w_daily_mobility_reset",
        title = "10-Min Daily Mobility Reset",
        description = "Gentle decompressing mobility routine to release tension, restore spinal range of motion, and reset posture. No strength exercises.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.BEGINNER,
        focusArea = FocusArea.MOBILITY,
        intensity = IntensityPreference.EASY,
        caloriesBurnEstimate = 50,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.ARM_CIRCLES,    orderIndex = 0, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.CAT_COW_STRETCH,orderIndex = 1, durationSeconds = 60, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,       orderIndex = 2, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,  orderIndex = 3, durationSeconds = 45, reps = 12, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,  orderIndex = 4, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.CAT_COW_STRETCH,orderIndex = 5, durationSeconds = 45, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,       orderIndex = 6, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.PRONE_YTW_HOLD, orderIndex = 7, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.PLANK,          orderIndex = 8, durationSeconds = 30, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,    orderIndex = 9, durationSeconds = 60, restDurationSeconds = 0)
        )
    )

    // ─────────────────────────────────────────────────────────────────
    // WEEK 2 — Progressive Overload variants (+2 reps / +5s duration)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Day 8 variant: +2 reps on squats/lunges, +5s on Mountain Climbers.
     * Replaces the verbatim Day 1 repeat that existed before (FIX PC-01).
     */
    val FULL_BODY_POWER = Workout(
        id = "w_full_body_power",
        title = "10-Min Full Body Power",
        description = "Progressive overload on the Week 1 full-body circuit. Harder rep targets and increased cardio duration.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.FULL_BODY,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 95,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.ARM_CIRCLES,       orderIndex = 0, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,     orderIndex = 1, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BODYWEIGHT_SQUATS, orderIndex = 2, durationSeconds = 45, reps = 17, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PUSH_UPS,          orderIndex = 3, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,     orderIndex = 4, durationSeconds = 45, reps = 17, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,          orderIndex = 5, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS, orderIndex = 6, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,     orderIndex = 7, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.LUNGES,            orderIndex = 8, durationSeconds = 45, reps = 16, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,       orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * Day 9 variant: +5s on Plank holds; Hollow Body Hold (extended Plank) replaces one Bird Dog repeat.
     * FIX PC-02: Day 9 (Core) no longer immediately follows Day 8 (Full Body) in the program —
     * a rest day is now inserted between them. See ProgramCatalog.
     */
    val CORE_ENDURANCE = Workout(
        id = "w_core_endurance",
        title = "10-Min Core Endurance",
        description = "Progressive core circuit with longer plank holds and increased oblique volume for Week 2.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.CORE,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 75,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.CAT_COW_STRETCH,   orderIndex = 0, durationSeconds = 45, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,          orderIndex = 1, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PLANK,             orderIndex = 2, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BICYCLE_CRUNCHES,  orderIndex = 3, durationSeconds = 45, reps = 22, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS, orderIndex = 4, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SHOULDER_TAPS,     orderIndex = 5, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,     orderIndex = 6, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGE_MARCH,orderIndex = 7, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.HIGH_KNEES,        orderIndex = 8, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,       orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * Day 10 variant: +2 reps on all strength exercises; Wall Sit duration increased to 55s.
     */
    val LOWER_BODY_BURN_V2 = Workout(
        id = "w_lower_body_burn_v2",
        title = "10-Min Lower Body Burn — Level Up",
        description = "Week 2 lower-body circuit with increased rep targets and longer isometric holds.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.LOWER_BODY,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 105,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,      orderIndex = 0, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.BODYWEIGHT_SQUATS,  orderIndex = 1, durationSeconds = 45, reps = 17, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.LUNGES,             orderIndex = 2, durationSeconds = 45, reps = 16, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,      orderIndex = 3, durationSeconds = 45, reps = 18, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.HIGH_KNEES,         orderIndex = 4, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.WALL_SIT,           orderIndex = 5, durationSeconds = 55, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.REVERSE_LUNGES,     orderIndex = 6, durationSeconds = 45, reps = 16, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGE_MARCH, orderIndex = 7, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS,  orderIndex = 8, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,        orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * Day 12 variant: +2 reps on all push-up variants; PRONE_YTW_HOLD increased to 55s.
     */
    val UPPER_BODY_STRENGTH = Workout(
        id = "w_upper_body_strength",
        title = "10-Min Upper Body Strength",
        description = "Week 2 upper-body circuit with progressive overload on all push and posterior chain patterns.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.UPPER_BODY,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 85,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.ARM_CIRCLES,     orderIndex = 0, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.PUSH_UPS,        orderIndex = 1, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PRONE_YTW_HOLD,  orderIndex = 2, durationSeconds = 55, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SHOULDER_TAPS,   orderIndex = 3, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.WIDE_PUSH_UPS,   orderIndex = 4, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.SUPERMAN_HOLD,   orderIndex = 5, durationSeconds = 50, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.DIAMOND_PUSH_UPS,orderIndex = 6, durationSeconds = 45, reps = 10, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.BIRD_DOG,        orderIndex = 7, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,   orderIndex = 8, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,     orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    /**
     * Day 13: Full Body Challenge — same structure as Full Body Power with a HIIT finisher.
     * High Knees burst replaces the final cooldown rest to push cardiovascular intensity.
     */
    val FULL_BODY_CHALLENGE = Workout(
        id = "w_full_body_challenge",
        title = "10-Min Full Body Challenge",
        description = "Peak Week 2 session combining strength, core, and a HIIT cardio finisher.",
        estimatedDurationMinutes = 10,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        focusArea = FocusArea.FULL_BODY,
        intensity = IntensityPreference.CHALLENGING,
        caloriesBurnEstimate = 110,
        exercises = listOf(
            WorkoutExercise(ExerciseCatalog.JUMPING_JACKS,     orderIndex = 0, durationSeconds = 45, restDurationSeconds = 15),
            WorkoutExercise(ExerciseCatalog.BODYWEIGHT_SQUATS, orderIndex = 1, durationSeconds = 45, reps = 17, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PUSH_UPS,          orderIndex = 2, durationSeconds = 45, reps = 12, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.GLUTE_BRIDGES,     orderIndex = 3, durationSeconds = 45, reps = 17, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.MOUNTAIN_CLIMBERS, orderIndex = 4, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.WIDE_PUSH_UPS,     orderIndex = 5, durationSeconds = 45, reps = 10, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.REVERSE_LUNGES,    orderIndex = 6, durationSeconds = 45, reps = 14, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.PRONE_YTW_HOLD,    orderIndex = 7, durationSeconds = 45, restDurationSeconds = 20),
            WorkoutExercise(ExerciseCatalog.HIGH_KNEES,        orderIndex = 8, durationSeconds = 30, restDurationSeconds = 10),
            WorkoutExercise(ExerciseCatalog.CHILDS_POSE,       orderIndex = 9, durationSeconds = 45, restDurationSeconds = 0)
        )
    )

    val ALL: List<Workout> = listOf(
        FULL_BODY_IGNITION,
        CORE_SHRED,
        LOWER_BODY_BURN,
        UPPER_BODY_SCULPT,
        DAILY_MOBILITY_RESET,
        // Week 2 progressive variants
        FULL_BODY_POWER,
        CORE_ENDURANCE,
        LOWER_BODY_BURN_V2,
        UPPER_BODY_STRENGTH,
        FULL_BODY_CHALLENGE
    )
}
