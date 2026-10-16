package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.ReminderEntity
import com.fit10.app.domain.model.Reminder

fun ReminderEntity.toDomain(): Reminder = Reminder(
    id = id,
    userId = userId,
    isEnabled = isEnabled,
    hour = hour,
    minute = minute,
    daysOfWeek = daysOfWeek,
    title = title,
    message = message
)

fun Reminder.toEntity(): ReminderEntity = ReminderEntity(
    id = id,
    userId = userId,
    isEnabled = isEnabled,
    hour = hour,
    minute = minute,
    daysOfWeek = daysOfWeek,
    title = title,
    message = message
)
