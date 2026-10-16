package com.fit10.app.domain.model

/**
 * Immutable domain model representing a structured workout routine.
 */
data class Workout(
    val id: String,
    val title: String,
    val description: String,
    val estimatedDurationMinutes: Int = 10,
    val fitnessLevel: FitnessLevel,
    val focusArea: FocusArea,
    val intensity: IntensityPreference,
    val exercises: List<WorkoutExercise> = emptyList(),
    val isCustom: Boolean = false,
    val caloriesBurnEstimate: Int? = null
)
