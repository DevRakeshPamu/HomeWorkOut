package com.fit10.app.domain.model

/**
 * Performance metrics captured for a completed or attempted workout session.
 */
data class WorkoutPerformance(
    val sessionId: String,
    val workoutId: String,
    val completionRate: Float,
    val actualDurationSeconds: Int,
    val targetDurationSeconds: Int,
    val perceivedExertion: Int? = null,
    val exercisePerformances: List<ExercisePerformance> = emptyList()
)

/**
 * Performance metrics captured for a single exercise within a workout session.
 */
data class ExercisePerformance(
    val exerciseId: String,
    val exerciseName: String,
    val completedReps: Int? = null,
    val completedSeconds: Int = 0,
    val isSkipped: Boolean = false
)
