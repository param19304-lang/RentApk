package com.example.rentmanagement.utils

import com.example.rentmanagement.domain.model.PaymentStatus

/**
 * Pure, side-effect free rent math. Kept separate from Room/UseCases so it is
 * trivially unit-testable (see app/src/test).
 */
object RentCalculator {

    fun calculateTotalPayable(
        rentAmount: Double,
        maintenance: Double = 0.0,
        otherCharges: Double = 0.0,
        previousOutstanding: Double = 0.0,
        lateFee: Double = 0.0
    ): Double = rentAmount + maintenance + otherCharges + previousOutstanding + lateFee

    fun calculateRemaining(totalPayable: Double, amountPaid: Double): Double =
        totalPayable - amountPaid

    /**
     * @param allowAdvance if true, remaining may go negative (advance credit); otherwise clamped to 0.
     */
    fun applyPayment(
        currentAmountPaid: Double,
        totalPayable: Double,
        paymentAmount: Double,
        allowAdvance: Boolean = true
    ): Pair<Double, Double> {
        require(paymentAmount > 0) { "Payment amount must be positive" }
        val newAmountPaid = currentAmountPaid + paymentAmount
        var remaining = calculateRemaining(totalPayable, newAmountPaid)
        if (!allowAdvance && remaining < 0) remaining = 0.0
        return newAmountPaid to remaining
    }

    fun determineStatus(remaining: Double, totalPayable: Double, isOverdue: Boolean): PaymentStatus {
        return when {
            remaining <= 0.0 -> PaymentStatus.PAID
            remaining < totalPayable -> if (isOverdue) PaymentStatus.OVERDUE else PaymentStatus.PARTIALLY_PAID
            isOverdue -> PaymentStatus.OVERDUE
            else -> PaymentStatus.PENDING
        }
    }

    /**
     * Prorates rent for a billing month in which the lease's rentStartDate
     * falls partway through — e.g. rent starting on the 15th only charges for
     * the remaining days of that month. Returns [rentAmount] unchanged when
     * rentStartDate is on or before the first day of [billingMonth] (i.e. no
     * proration needed — the normal, most common case).
     *
     * Rounded to 2 decimal places (nearest currency unit).
     */
    fun prorateFirstMonthRent(rentAmount: Double, billingMonth: String, rentStartDate: Long): Double {
        val monthStart = DateUtils.startOfMonth(billingMonth)
        if (rentStartDate <= monthStart) return rentAmount

        val daysInMonth = DateUtils.daysInMonth(billingMonth)
        val startDay = DateUtils.dayOfMonth(rentStartDate)
        val daysCharged = (daysInMonth - startDay + 1).coerceIn(0, daysInMonth)
        val prorated = rentAmount * daysCharged / daysInMonth
        return kotlin.math.round(prorated * 100) / 100.0
    }
}
