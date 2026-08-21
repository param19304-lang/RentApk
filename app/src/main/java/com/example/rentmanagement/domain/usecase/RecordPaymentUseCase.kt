package com.example.rentmanagement.domain.usecase

import com.example.rentmanagement.data.entities.PaymentEntity
import com.example.rentmanagement.data.repository.PaymentRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.domain.model.PaymentMethod
import com.example.rentmanagement.utils.RentCalculator
import javax.inject.Inject

sealed class RecordPaymentResult {
    data class Success(val paymentId: Long, val receiptNumber: String) : RecordPaymentResult()
    data class Error(val message: String) : RecordPaymentResult()
}

/**
 * Records a (possibly partial) payment against a rent record, recalculates the
 * remaining balance/status, and never mutates or deletes prior payment rows
 * (business rules #6, #10 in the spec).
 */
class RecordPaymentUseCase @Inject constructor(
    private val rentRepository: RentRepository,
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        rentId: Long,
        amount: Double,
        paymentDate: Long,
        method: PaymentMethod,
        referenceNumber: String? = null,
        notes: String? = null,
        allowAdvance: Boolean = true
    ): RecordPaymentResult {
        if (amount <= 0) return RecordPaymentResult.Error("Payment amount must be greater than zero")

        val rent = rentRepository.getRentById(rentId)
            ?: return RecordPaymentResult.Error("Rent record not found")

        if (!allowAdvance && amount > rent.remainingAmount) {
            return RecordPaymentResult.Error("Payment exceeds the payable amount. Enable advance payments to allow this.")
        }

        val (newAmountPaid, newRemaining) = RentCalculator.applyPayment(
            currentAmountPaid = rent.amountPaid,
            totalPayable = rent.totalPayable,
            paymentAmount = amount,
            allowAdvance = allowAdvance
        )
        val isOverdue = rent.status == com.example.rentmanagement.domain.model.PaymentStatus.OVERDUE
        val newStatus = RentCalculator.determineStatus(newRemaining, rent.totalPayable, isOverdue)

        rentRepository.updateRent(rent.copy(amountPaid = newAmountPaid, remainingAmount = newRemaining, status = newStatus))

        val receiptNumber = "RCPT-${rent.id}-${System.currentTimeMillis()}"
        val paymentId = paymentRepository.addPayment(
            PaymentEntity(
                rentId = rent.id,
                tenantId = rent.tenantId,
                propertyId = rent.propertyId,
                unitId = rent.unitId,
                amount = amount,
                paymentDate = paymentDate,
                paymentMethod = method,
                referenceNumber = referenceNumber,
                notes = notes,
                receiptNumber = receiptNumber
            )
        )
        return RecordPaymentResult.Success(paymentId, receiptNumber)
    }
}
