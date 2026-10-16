package com.fit10.app.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProgramDayWithWorkout(
    @Embedded
    val day: ProgramDayEntity,

    @Relation(
        parentColumn = "workoutId",
        entityColumn = "id"
    )
    val workout: WorkoutEntity?
)

data class ProgramWithDays(
    @Embedded
    val program: ProgramEntity,

    @Relation(
        entity = ProgramDayEntity::class,
        parentColumn = "id",
        entityColumn = "programId"
    )
    val daysWithWorkouts: List<ProgramDayWithWorkout>
)
