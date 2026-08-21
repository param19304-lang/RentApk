package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.UserRole

@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val fullName: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
