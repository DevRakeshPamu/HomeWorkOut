package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "program_days",
    primaryKeys = ["programId", "dayNumber"],
    foreignKeys = [
        ForeignKey(
            entity = ProgramEntity::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["programId"]),
        Index(value = ["workoutId"])
    ]
)
data class ProgramDayEntity(
    val programId: String,
    val dayNumber: Int,
    val title: String,
    val isRestDay: Boolean,
    val workoutId: String?,
    val isCompleted: Boolean
)
