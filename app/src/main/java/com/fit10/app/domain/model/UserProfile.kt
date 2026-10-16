package com.fit10.app.domain.model

/**
 * Represents the profile, fitness preferences, and settings for a Fit10 user.
 */
data class UserProfile(
    val id: String,
    val name: String,
    val fitnessGoal: FitnessGoal,
    val fitnessLevel: FitnessLevel,
    val availableEquipment: Set<Equipment> = setOf(Equipment.NONE),
    val targetWorkoutDurationMinutes: Int = 10,
    val workoutsPerWeek: Int = 4,
    val preferredFocusAreas: Set<FocusArea> = setOf(FocusArea.FULL_BODY),
    val intensityPreference: IntensityPreference = IntensityPreference.MODERATE,
    val isReminderEnabled: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
