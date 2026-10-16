package com.fit10.app.domain.model

/**
 * Immutable domain model representing an individual exercise definition.
 */
data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val type: ExerciseType,
    val focusArea: FocusArea,
    val equipmentRequired: Equipment = Equipment.NONE,
    val impactLevel: ImpactLevel = ImpactLevel.LOW,
    val defaultDurationSeconds: Int = 45,
    val defaultReps: Int? = null,
    val instructions: List<String> = emptyList(),
    val tips: List<String> = emptyList(),
    val targetMuscles: List<String> = emptyList(),
    val animationUrl: String? = null,
    val thumbnailUrl: String? = null
)
