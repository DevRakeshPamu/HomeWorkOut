package com.fit10.app.domain.model

/**
 * Workout reminder and notification settings.
 * [hour] is in 24-hour format (0-23).
 * [minute] is in minutes (0-59).
 * [daysOfWeek] contains ISO day-of-week integers (1 = Monday, 7 = Sunday).
 */
data class Reminder(
    val id: String,
    val userId: String,
    val isEnabled: Boolean = true,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5),
    val title: String = "Time for your Fit10 workout!",
    val message: String = "Take 10 minutes for yourself today."
)
