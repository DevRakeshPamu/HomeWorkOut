package com.fit10.app.data.repository

import com.fit10.app.data.db.dao.ReminderDao
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.data.db.mapper.toEntity
import com.fit10.app.domain.model.Reminder
import com.fit10.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getReminder(userId: String): Flow<Reminder?> =
        reminderDao.getReminderForUser(userId).map { it?.toDomain() }

    override suspend fun saveReminder(reminder: Reminder) {
        reminderDao.insertOrUpdate(reminder.toEntity())
    }

    override suspend fun setReminderEnabled(reminderId: String, isEnabled: Boolean) {
        reminderDao.setReminderEnabled(reminderId, isEnabled)
    }
}
