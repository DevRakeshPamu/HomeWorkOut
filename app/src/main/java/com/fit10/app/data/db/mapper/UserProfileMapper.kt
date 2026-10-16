package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.UserProfileEntity
import com.fit10.app.domain.model.UserProfile

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    fitnessGoal = fitnessGoal,
    fitnessLevel = fitnessLevel,
    availableEquipment = availableEquipment,
    targetWorkoutDurationMinutes = targetWorkoutDurationMinutes,
    workoutsPerWeek = workoutsPerWeek,
    preferredFocusAreas = preferredFocusAreas,
    intensityPreference = intensityPreference,
    isReminderEnabled = isReminderEnabled,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    name = name,
    fitnessGoal = fitnessGoal,
    fitnessLevel = fitnessLevel,
    availableEquipment = availableEquipment,
    targetWorkoutDurationMinutes = targetWorkoutDurationMinutes,
    workoutsPerWeek = workoutsPerWeek,
    preferredFocusAreas = preferredFocusAreas,
    intensityPreference = intensityPreference,
    isReminderEnabled = isReminderEnabled,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
)
