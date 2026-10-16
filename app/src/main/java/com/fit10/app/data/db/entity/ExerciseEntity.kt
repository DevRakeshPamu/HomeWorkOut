package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val type: ExerciseType,
    val focusArea: FocusArea,
    val equipmentRequired: Equipment,
    val impactLevel: ImpactLevel,
    val defaultDurationSeconds: Int,
    val defaultReps: Int?,
    val instructions: List<String>,
    val tips: List<String>,
    val targetMuscles: List<String>,
    val animationUrl: String?,
    val thumbnailUrl: String?
)
