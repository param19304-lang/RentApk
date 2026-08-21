package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.UnitStatus

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("propertyId")]
)
data class UnitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val unitName: String,
    val floor: String? = null,
    val monthlyRent: Double,
    val securityDeposit: Double? = null,
    val maintenanceCharge: Double? = null,
    val electricityCharge: Double? = null,
    val waterCharge: Double? = null,
    val status: UnitStatus = UnitStatus.VACANT,
    val currentTenantId: Long? = null,
    val notes: String? = null,
    val isDeleted: Boolean = false
)
