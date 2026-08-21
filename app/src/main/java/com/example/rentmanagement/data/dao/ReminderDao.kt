package com.example.rentmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentmanagement.data.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 AND isSent = 0 AND scheduledAt <= :now")
    suspend fun getDueReminders(now: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders ORDER BY scheduledAt ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("UPDATE reminders SET isSent = 1 WHERE id = :id")
    suspend fun markSent(id: Long)
}
