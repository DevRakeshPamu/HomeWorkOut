package com.fit10.app.domain.model

/**
 * Result of the AI/logic personalization adaptation engine evaluating user feedback or performance.
 */
data class AdaptationResult(
    val previousLevel: FitnessLevel,
    val recommendedLevel: FitnessLevel,
    val intensityAdjustment: IntensityPreference,
    val recommendedDurationMinutes: Int = 10,
    val suggestedExerciseReplacements: Map<String, String> = emptyMap(),
    val adaptationReason: String,
    val appliedAtEpochMs: Long = System.currentTimeMillis()
)
