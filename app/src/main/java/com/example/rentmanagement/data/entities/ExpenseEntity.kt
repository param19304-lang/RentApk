package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.ExpenseCategory

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(entity = PropertyEntity::class, parentColumns = ["id"], childColumns = ["propertyId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = UnitEntity::class, parentColumns = ["id"], childColumns = ["unitId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("propertyId"), Index("unitId"), Index("category")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val unitId: Long? = null,
    val category: ExpenseCategory,
    val amount: Double,
    val date: Long,
    val description: String? = null,
    val vendor: String? = null,
    val invoiceImageUri: String? = null,
    val notes: String? = null,
    val isDeleted: Boolean = false
)
