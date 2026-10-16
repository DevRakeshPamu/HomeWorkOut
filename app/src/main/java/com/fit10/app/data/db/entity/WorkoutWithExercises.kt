package com.fit10.app.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded
    val workout: WorkoutEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val workoutExercises: List<WorkoutExerciseEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseEntity::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseEntity> = emptyList()
)
