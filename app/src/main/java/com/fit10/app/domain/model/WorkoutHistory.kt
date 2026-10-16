package com.fit10.app.domain.model

/**
 * Aggregated workout statistics and past workout history for a user.
 */
data class WorkoutHistory(
    val totalWorkoutsCompleted: Int = 0,
    val totalMinutesWorkedOut: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val currentStreakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val recentResults: List<WorkoutResult> = emptyList()
)
