package com.example.rentmanagement.domain

import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.domain.usecase.CalculateLeaseStatusUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateLeaseStatusUseCaseTest {
    private val useCase = CalculateLeaseStatusUseCase()

    private fun lease(startDate: Long, endDate: Long, status: LeaseStatus = LeaseStatus.ACTIVE) = LeaseEntity(
        id = 1, propertyId = 1, unitId = 1, tenantId = 1,
        startDate = startDate, endDate = endDate, monthlyRent = 10000.0, status = status
    )

    @Test
    fun `active lease well before end date stays ACTIVE`() {
        val now = 1_700_000_000_000L
        val end = now + 60L * 24 * 60 * 60 * 1000 // 60 days out
        assertEquals(LeaseStatus.ACTIVE, useCase(lease(now - 1000, end), now))
    }

    @Test
    fun `lease ending within 30 days is EXPIRING_SOON`() {
        val now = 1_700_000_000_000L
        val end = now + 10L * 24 * 60 * 60 * 1000
        assertEquals(LeaseStatus.EXPIRING_SOON, useCase(lease(now - 1000, end), now))
    }

    @Test
    fun `lease past end date is EXPIRED`() {
        val now = 1_700_000_000_000L
        val end = now - 1000
        assertEquals(LeaseStatus.EXPIRED, useCase(lease(now - 2000, end), now))
    }

    @Test
    fun `terminated lease stays TERMINATED regardless of dates`() {
        val now = 1_700_000_000_000L
        val end = now + 100000
        assertEquals(LeaseStatus.TERMINATED, useCase(lease(now - 1000, end, LeaseStatus.TERMINATED), now))
    }
}
