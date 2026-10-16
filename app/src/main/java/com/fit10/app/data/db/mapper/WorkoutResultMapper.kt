package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.WorkoutResultEntity
import com.fit10.app.domain.model.WorkoutResult

fun WorkoutResultEntity.toDomain(): WorkoutResult = WorkoutResult(
    sessionId = sessionId,
    workoutId = workoutId,
    workoutTitle = workoutTitle,
    status = status,
    durationSeconds = durationSeconds,
    exercisesCompletedCount = exercisesCompletedCount,
    totalExercisesCount = totalExercisesCount,
    caloriesBurned = caloriesBurned,
    feedback = feedback,
    completedAtEpochMs = completedAtEpochMs
)

fun WorkoutResult.toEntity(): WorkoutResultEntity = WorkoutResultEntity(
    sessionId = sessionId,
    workoutId = workoutId,
    workoutTitle = workoutTitle,
    status = status,
    durationSeconds = durationSeconds,
    exercisesCompletedCount = exercisesCompletedCount,
    totalExercisesCount = totalExercisesCount,
    caloriesBurned = caloriesBurned,
    feedback = feedback,
    completedAtEpochMs = completedAtEpochMs
)
