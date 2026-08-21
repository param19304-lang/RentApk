package com.example.rentmanagement.utils

import com.example.rentmanagement.domain.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RentCalculatorTest {

    @Test
    fun `total payable sums all components`() {
        val total = RentCalculator.calculateTotalPayable(
            rentAmount = 15000.0, maintenance = 1000.0, otherCharges = 200.0,
            previousOutstanding = 500.0, lateFee = 100.0
        )
        assertEquals(16800.0, total, 0.001)
    }

    @Test
    fun `remaining is total minus paid`() {
        assertEquals(5000.0, RentCalculator.calculateRemaining(15000.0, 10000.0), 0.001)
    }

    @Test
    fun `three equal partial payments fully settle rent`() {
        val total = 15000.0
        var paid = 0.0
        var remaining = total
        repeat(3) {
            val (newPaid, newRemaining) = RentCalculator.applyPayment(paid, total, 5000.0)
            paid = newPaid
            remaining = newRemaining
        }
        assertEquals(15000.0, paid, 0.001)
        assertEquals(0.0, remaining, 0.001)
        assertEquals(PaymentStatus.PAID, RentCalculator.determineStatus(remaining, total, isOverdue = false))
    }

    @Test
    fun `partial payment yields PARTIALLY_PAID status when not overdue`() {
        val (paid, remaining) = RentCalculator.applyPayment(0.0, 15000.0, 5000.0)
        assertEquals(5000.0, paid, 0.001)
        assertEquals(10000.0, remaining, 0.001)
        assertEquals(PaymentStatus.PARTIALLY_PAID, RentCalculator.determineStatus(remaining, 15000.0, isOverdue = false))
    }

    @Test
    fun `unpaid overdue rent is OVERDUE not PENDING`() {
        assertEquals(PaymentStatus.OVERDUE, RentCalculator.determineStatus(15000.0, 15000.0, isOverdue = true))
    }

    @Test
    fun `advance payment allowed produces negative remaining as credit`() {
        val (paid, remaining) = RentCalculator.applyPayment(0.0, 15000.0, 16000.0, allowAdvance = true)
        assertEquals(16000.0, paid, 0.001)
        assertEquals(-1000.0, remaining, 0.001)
        assertEquals(PaymentStatus.PAID, RentCalculator.determineStatus(remaining, 15000.0, isOverdue = false))
    }

    @Test
    fun `advance payment disallowed clamps remaining to zero`() {
        val (_, remaining) = RentCalculator.applyPayment(0.0, 15000.0, 16000.0, allowAdvance = false)
        assertEquals(0.0, remaining, 0.001)
    }

    @Test
    fun `negative or zero payment amount is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            RentCalculator.applyPayment(0.0, 15000.0, 0.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            RentCalculator.applyPayment(0.0, 15000.0, -100.0)
        }
    }
}
