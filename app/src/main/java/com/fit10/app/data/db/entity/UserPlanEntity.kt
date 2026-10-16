package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_plans",
    foreignKeys = [
        ForeignKey(
            entity = ProgramEntity::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["programId"])
    ]
)
data class UserPlanEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val programId: String?,
    val currentDayNumber: Int,
    val totalDays: Int,
    val startDateEpochMs: Long,
    val targetCompletionDateEpochMs: Long?,
    val isCompleted: Boolean
)
