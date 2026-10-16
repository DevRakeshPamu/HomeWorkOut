package com.fit10.app.domain.model

/**
 * A scheduled day within a fitness program, containing workout or rest day details.
 */
data class ProgramDay(
    val dayNumber: Int,
    val title: String,
    val isRestDay: Boolean = false,
    val workout: Workout? = null,
    val isCompleted: Boolean = false
)
