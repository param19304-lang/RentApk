package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.PropertyType

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: PropertyType,
    val address: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val photoUri: String? = null,
    val ownerName: String? = null,
    val contactNumber: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
