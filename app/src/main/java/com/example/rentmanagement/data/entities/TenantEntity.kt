package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tenants")
data class TenantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phoneNumber: String,
    val email: String? = null,
    val dateOfBirth: Long? = null,
    val idType: String? = null,
    val idNumber: String? = null,
    val address: String? = null,
    val emergencyContact: String? = null,
    val occupants: Int = 1,
    val profilePhotoUri: String? = null,
    val notes: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
