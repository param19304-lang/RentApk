package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.DocumentCategory

@Entity(
    tableName = "documents",
    indices = [Index("propertyId"), Index("tenantId"), Index("leaseId")]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long? = null,
    val tenantId: Long? = null,
    val leaseId: Long? = null,
    val category: DocumentCategory,
    val name: String,
    val uri: String,
    val uploadedAt: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val isDeleted: Boolean = false
)
