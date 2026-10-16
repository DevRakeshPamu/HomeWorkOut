package com.fit10.app.domain.model

/**
 * Summary record of a finished or ended workout session.
 */
data class WorkoutResult(
    val sessionId: String,
    val workoutId: String,
    val workoutTitle: String,
    val status: WorkoutStatus,
    val durationSeconds: Int,
    val exercisesCompletedCount: Int,
    val totalExercisesCount: Int,
    val caloriesBurned: Int,
    val feedback: WorkoutFeedback? = null,
    val completedAtEpochMs: Long = System.currentTimeMillis()
)
