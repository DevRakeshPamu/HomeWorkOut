package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val estimatedDurationMinutes: Int,
    val fitnessLevel: FitnessLevel,
    val focusArea: FocusArea,
    val intensity: IntensityPreference,
    val isCustom: Boolean,
    val caloriesBurnEstimate: Int?
)
