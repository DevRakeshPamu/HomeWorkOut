package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val fitnessGoal: FitnessGoal,
    val fitnessLevel: FitnessLevel,
    val availableEquipment: Set<Equipment>,
    val targetWorkoutDurationMinutes: Int,
    val workoutsPerWeek: Int,
    val preferredFocusAreas: Set<FocusArea>,
    val intensityPreference: IntensityPreference,
    val isReminderEnabled: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
