package com.example.rentmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rentmanagement.domain.model.PaymentMethod

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(entity = RentEntity::class, parentColumns = ["id"], childColumns = ["rentId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = TenantEntity::class, parentColumns = ["id"], childColumns = ["tenantId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = PropertyEntity::class, parentColumns = ["id"], childColumns = ["propertyId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = UnitEntity::class, parentColumns = ["id"], childColumns = ["unitId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("rentId"), Index("tenantId"), Index("propertyId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rentId: Long,
    val tenantId: Long,
    val propertyId: Long,
    val unitId: Long,
    val amount: Double,
    val paymentDate: Long,
    val paymentMethod: PaymentMethod,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val receiptNumber: String? = null,
    val isDeleted: Boolean = false
)
