package com.fit10.app.domain.model

/**
 * An active, personalized plan assigned to a specific user.
 */
data class UserPlan(
    val id: String,
    val userId: String,
    val programId: String? = null,
    val currentDayNumber: Int = 1,
    val totalDays: Int,
    val startDateEpochMs: Long = System.currentTimeMillis(),
    val targetCompletionDateEpochMs: Long? = null,
    val scheduledDays: List<ProgramDay> = emptyList(),
    val isCompleted: Boolean = false
)
