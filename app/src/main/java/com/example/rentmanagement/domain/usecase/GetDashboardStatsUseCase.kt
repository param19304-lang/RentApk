package com.example.rentmanagement.domain.usecase

import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.repository.ExpenseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.domain.model.UnitStatus
import com.example.rentmanagement.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DashboardStats(
    val totalProperties: Int = 0,
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val vacantUnits: Int = 0,
    val expectedRentThisMonth: Double = 0.0,
    val collectedRentThisMonth: Double = 0.0,
    val pendingRent: Double = 0.0,
    val overdueRent: Double = 0.0,
    val totalExpensesThisMonth: Double = 0.0,
    val netIncomeThisMonth: Double = 0.0
)

private data class UnitOccupancy(
    val totalProperties: Int,
    val totalUnits: Int,
    val occupied: Int,
    val vacant: Int
)

class GetDashboardStatsUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val unitRepository: UnitRepository,
    private val rentRepository: RentRepository,
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<DashboardStats> {
        val billingMonth = DateUtils.currentBillingMonth()
        val monthStart = DateUtils.startOfMonth(billingMonth)
        val monthEnd = DateUtils.endOfMonth(billingMonth)

        val occupancyFlow: Flow<UnitOccupancy> = combine(
            propertyRepository.getPropertyCount(),
            unitRepository.getTotalUnitCount(),
            unitRepository.getUnitCountByStatus(UnitStatus.OCCUPIED),
            unitRepository.getUnitCountByStatus(UnitStatus.VACANT)
        ) { totalProperties, totalUnits, occupied, vacant ->
            UnitOccupancy(totalProperties, totalUnits, occupied, vacant)
        }

        val rentAndExpensesFlow: Flow<Pair<List<RentEntity>, Double>> = combine(
            rentRepository.getRentForMonth(billingMonth),
            expenseRepository.getTotalExpensesBetween(monthStart, monthEnd)
        ) { rentList, totalExpenses -> rentList to totalExpenses }

        return combine(occupancyFlow, rentAndExpensesFlow) { occupancy, pair ->
            val (rentList, totalExpenses) = pair
            val expected = rentList.sumOf { it.totalPayable }
            val collected = rentList.sumOf { it.amountPaid }
            val pending = rentList
                .filter { it.status == PaymentStatus.PENDING || it.status == PaymentStatus.PARTIALLY_PAID }
                .sumOf { it.remainingAmount }
            val overdue = rentList.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.remainingAmount }

            DashboardStats(
                totalProperties = occupancy.totalProperties,
                totalUnits = occupancy.totalUnits,
                occupiedUnits = occupancy.occupied,
                vacantUnits = occupancy.vacant,
                expectedRentThisMonth = expected,
                collectedRentThisMonth = collected,
                pendingRent = pending,
                overdueRent = overdue,
                totalExpensesThisMonth = totalExpenses,
                netIncomeThisMonth = collected - totalExpenses
            )
        }
    }
}
