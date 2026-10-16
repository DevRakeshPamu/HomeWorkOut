package com.fit10.app.domain.model

/**
 * Represents the active execution state of a workout session.
 */
data class WorkoutSession(
    val id: String,
    val workoutId: String,
    val workoutTitle: String,
    val status: WorkoutStatus = WorkoutStatus.NOT_STARTED,
    val currentExerciseIndex: Int = 0,
    val elapsedSeconds: Int = 0,
    val startedAtEpochMs: Long? = null,
    val completedAtEpochMs: Long? = null,
    val exercises: List<WorkoutExercise> = emptyList()
)
