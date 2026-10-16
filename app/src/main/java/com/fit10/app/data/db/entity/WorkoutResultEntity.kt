package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutStatus

@Entity(tableName = "workout_results")
data class WorkoutResultEntity(
    @PrimaryKey
    val sessionId: String,
    val workoutId: String,
    val workoutTitle: String,
    val status: WorkoutStatus,
    val durationSeconds: Int,
    val exercisesCompletedCount: Int,
    val totalExercisesCount: Int,
    val caloriesBurned: Int,
    val feedback: WorkoutFeedback?,
    val completedAtEpochMs: Long
)
