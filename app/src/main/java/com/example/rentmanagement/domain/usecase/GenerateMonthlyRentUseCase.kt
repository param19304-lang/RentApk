package com.example.rentmanagement.domain.usecase

import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.utils.DateUtils
import com.example.rentmanagement.utils.RentCalculator
import javax.inject.Inject

/**
 * Creates rent records for every ACTIVE lease for a given billing month, if one
 * doesn't already exist. Any unpaid remainder from the lease's most recent rent
 * record is carried forward as previousOutstanding (business rule #4/#10 in the
 * spec: rent tied to a lease, historical records never silently deleted).
 */
class GenerateMonthlyRentUseCase @Inject constructor(
    private val leaseRepository: LeaseRepository,
    private val rentRepository: RentRepository
) {
    suspend operator fun invoke(billingMonth: String = DateUtils.currentBillingMonth()): Int {
        val activeLeases = leaseRepository.getActiveLeasesOnce()
        var created = 0
        for (lease in activeLeases) {
            val existing = rentRepository.getRentForLeaseAndMonth(lease.id, billingMonth)
            if (existing != null) continue

            val outstanding = rentRepository.getOutstandingForLease(lease.id)
                .sumOf { it.remainingAmount.coerceAtLeast(0.0) }

            val dueDate = DateUtils.dueDateForMonth(billingMonth, lease.rentDueDay)
            val totalPayable = RentCalculator.calculateTotalPayable(
                rentAmount = lease.monthlyRent,
                previousOutstanding = outstanding
            )

            val rent = RentEntity(
                leaseId = lease.id,
                tenantId = lease.tenantId,
                propertyId = lease.propertyId,
                unitId = lease.unitId,
                billingMonth = billingMonth,
                rentAmount = lease.monthlyRent,
                previousOutstanding = outstanding,
                totalPayable = totalPayable,
                amountPaid = 0.0,
                remainingAmount = totalPayable,
                dueDate = dueDate,
                status = PaymentStatus.PENDING
            )
            rentRepository.addRent(rent)
            created++
        }
        return created
    }
}
