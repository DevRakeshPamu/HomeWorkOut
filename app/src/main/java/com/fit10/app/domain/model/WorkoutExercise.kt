package com.fit10.app.domain.model

/**
 * An exercise positioned within a specific workout sequence, with timing and rest parameters.
 */
data class WorkoutExercise(
    val exercise: Exercise,
    val orderIndex: Int,
    val durationSeconds: Int = exercise.defaultDurationSeconds,
    val reps: Int? = exercise.defaultReps,
    val restDurationSeconds: Int = 15
)
