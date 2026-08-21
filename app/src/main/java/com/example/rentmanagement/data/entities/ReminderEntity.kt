package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.ReminderType

@Entity(
    tableName = "reminders",
    indices = [Index("relatedId"), Index("scheduledAt")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: ReminderType,
    val relatedId: Long,
    val title: String,
    val message: String,
    val scheduledAt: Long,
    val isEnabled: Boolean = true,
    val isSent: Boolean = false
)
