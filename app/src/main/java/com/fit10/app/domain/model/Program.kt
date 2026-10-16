package com.fit10.app.domain.model

/**
 * Multi-day or multi-week structured fitness program.
 */
data class Program(
    val id: String,
    val title: String,
    val description: String,
    val goal: FitnessGoal,
    val fitnessLevel: FitnessLevel,
    val totalDays: Int,
    val daysPerWeek: Int,
    val estimatedWeeks: Int,
    val days: List<ProgramDay> = emptyList()
)
