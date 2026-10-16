package com.fit10.app.domain.repository

import com.fit10.app.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminder(userId: String): Flow<Reminder?>
    suspend fun saveReminder(reminder: Reminder)
    suspend fun setReminderEnabled(reminderId: String, isEnabled: Boolean)
}
