package com.fit10.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit10.app.data.db.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE userId = :userId LIMIT 1")
    fun getReminderForUser(userId: String): Flow<ReminderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isEnabled = :isEnabled WHERE id = :reminderId")
    suspend fun setReminderEnabled(reminderId: String, isEnabled: Boolean)

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}
