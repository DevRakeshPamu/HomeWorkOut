package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "workout_exercises",
    primaryKeys = ["workoutId", "exerciseId", "orderIndex"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["exerciseId"])
    ]
)
data class WorkoutExerciseEntity(
    val workoutId: String,
    val exerciseId: String,
    val orderIndex: Int,
    val durationSeconds: Int,
    val reps: Int?,
    val restDurationSeconds: Int
)
