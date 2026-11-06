package com.fit10.app.core.ui.figure

internal object BreathTimeline {
    private val ALL: Map<String, Pair<FloatArray, BooleanArray>> = mapOf(
        "ex_pelvic_tilt" to (floatArrayOf(0f, 2f, 4f, 6f, 8f) to booleanArrayOf(false, true, false, true, false)),
        "ex_heel_slides" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(true, false, true, false)),
        "ex_dead_bug" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(true, false, true, false)),
        "ex_clamshell" to (floatArrayOf(0f, 1.5f, 2.5f, 4f, 5f, 6.5f, 7.5f, 9f) to booleanArrayOf(true, false, true, false, true, false, true, false)),
        "ex_side_leg_lift" to (floatArrayOf(0f, 1.5f, 2.5f, 4f, 5f, 6.5f, 7.5f, 9f) to booleanArrayOf(true, false, true, false, true, false, true, false)),
        "ex_wall_push_ups" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_knee_push_ups" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_standing_march" to (floatArrayOf(0f, 1.5f, 2.5f, 4f, 5f, 6.5f, 7.5f, 9f) to booleanArrayOf(true, false, true, false, true, false, true, false)),
        "ex_arm_circles" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_cat_cow" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_childs_pose" to (floatArrayOf(0f, 3f, 5f, 7f) to booleanArrayOf(true, false, true, false)),
        "ex_bird_dog" to (floatArrayOf(0f, 3f, 5f, 8f) to booleanArrayOf(true, false, true, false)),
        "ex_glute_bridges" to (floatArrayOf(0f, 3f, 5f, 8f) to booleanArrayOf(true, false, true, false)),
        "ex_glute_bridge_march" to (floatArrayOf(0f, 2f, 3f, 5f, 6f, 7f, 8f, 9f) to booleanArrayOf(true, false, true, false, true, false, true, false)),
        "ex_wall_sit" to (floatArrayOf(0f, 2f, 4f, 6f, 8f) to booleanArrayOf(false, true, false, true, false)),
        "ex_squats" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_reverse_lunges" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_prone_ytw" to (floatArrayOf(0f, 1.5f, 3f, 4.5f, 6f, 7.5f) to booleanArrayOf(true, false, true, false, true, false)),
        "ex_superman" to (floatArrayOf(0f, 2f, 4f, 6f, 8f) to booleanArrayOf(true, false, true, false, true)),
        "ex_lunges" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_wide_push_ups" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_push_ups" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_plank" to (floatArrayOf(0f, 2f, 4f, 6f, 8f) to booleanArrayOf(false, true, false, true, false)),
        "ex_shoulder_taps" to (floatArrayOf(0f, 2f, 4f, 6f, 8f) to booleanArrayOf(true, false, true, false, true)),
        "ex_diamond_push_ups" to (floatArrayOf(0f, 3f, 5f, 8f) to booleanArrayOf(false, true, false, true)),
        "ex_bicycle_crunches" to (floatArrayOf(0f, 1.5f, 2.5f, 4f, 5f, 6.5f, 7.5f, 9f) to booleanArrayOf(true, false, true, false, true, false, true, false)),
        "ex_jumping_jacks" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_high_knees" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true)),
        "ex_mountain_climbers" to (floatArrayOf(0f, 2.5f, 5f, 7.5f) to booleanArrayOf(false, true, false, true))
    )

    fun at(exerciseId: String, seconds: Float): Breath? {
        val (starts, outs) = ALL[exerciseId] ?: return null
        var i = starts.lastIndex
        while (i > 0 && seconds < starts[i]) i--
        return if (outs[i]) Breath.OUT else Breath.IN
    }
}
