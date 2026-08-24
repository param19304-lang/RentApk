package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.LeaseStatus

@Entity(
    tableName = "leases",
    foreignKeys = [
        ForeignKey(entity = PropertyEntity::class, parentColumns = ["id"], childColumns = ["propertyId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = UnitEntity::class, parentColumns = ["id"], childColumns = ["unitId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = TenantEntity::class, parentColumns = ["id"], childColumns = ["tenantId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("propertyId"), Index("unitId"), Index("tenantId")]
)
data class LeaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val unitId: Long,
    val tenantId: Long,
    val startDate: Long,
    val endDate: Long,
    val rentStartDate: Long = startDate,
    val monthlyRent: Double,
    val securityDeposit: Double = 0.0,
    val rentDueDay: Int = 5,
    val gracePeriodDays: Int = 0,
    val lateFee: Double = 0.0,
    val noticePeriodDays: Int = 30,
    val rentEscalationPercent: Double = 0.0,
    val agreementDocumentUri: String? = null,
    val status: LeaseStatus = LeaseStatus.ACTIVE,
    val terminatedAt: Long? = null,
    val isDeleted: Boolean = false
)
