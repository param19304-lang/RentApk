package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.PaymentStatus

@Entity(
    tableName = "rent_records",
    foreignKeys = [
        ForeignKey(entity = LeaseEntity::class, parentColumns = ["id"], childColumns = ["leaseId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = TenantEntity::class, parentColumns = ["id"], childColumns = ["tenantId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = PropertyEntity::class, parentColumns = ["id"], childColumns = ["propertyId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = UnitEntity::class, parentColumns = ["id"], childColumns = ["unitId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("leaseId"), Index("tenantId"), Index("propertyId"), Index("unitId"), Index("billingMonth")]
)
data class RentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leaseId: Long,
    val tenantId: Long,
    val propertyId: Long,
    val unitId: Long,
    val billingMonth: String,
    val rentAmount: Double,
    val maintenance: Double = 0.0,
    val otherCharges: Double = 0.0,
    val previousOutstanding: Double = 0.0,
    val lateFee: Double = 0.0,
    val totalPayable: Double,
    val amountPaid: Double = 0.0,
    val remainingAmount: Double,
    val dueDate: Long,
    val status: PaymentStatus = PaymentStatus.PENDING
)
