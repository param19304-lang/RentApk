package com.example.rentmanagement.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.data.entities.PaymentEntity
import com.example.rentmanagement.data.entities.PropertyEntity
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.preferences.DashboardPreferences
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.PaymentRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.domain.model.DashboardTile
import com.example.rentmanagement.domain.usecase.DashboardStats
import com.example.rentmanagement.domain.usecase.GenerateMonthlyRentUseCase
import com.example.rentmanagement.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val generateMonthlyRentUseCase: GenerateMonthlyRentUseCase,
    paymentRepository: PaymentRepository,
    rentRepository: RentRepository,
    leaseRepository: LeaseRepository,
    tenantRepository: TenantRepository,
    propertyRepository: PropertyRepository,
    dashboardPreferences: DashboardPreferences
) : ViewModel() {

    val enabledTiles: StateFlow<Set<DashboardTile>> = dashboardPreferences.enabledTiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardTile.values().toSet())

    private val now = System.currentTimeMillis()
    private val sevenDaysOut = now + 7L * 24 * 60 * 60 * 1000
    private val thirtyDaysOut = now + 30L * 24 * 60 * 60 * 1000

    val stats: StateFlow<DashboardStats> = getDashboardStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val recentPayments: StateFlow<List<PaymentEntity>> = paymentRepository.getAllPayments()
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingDue: StateFlow<List<RentEntity>> = rentRepository.getUpcomingDue(now, sevenDaysOut)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringLeases: StateFlow<List<LeaseEntity>> = leaseRepository.getLeasesExpiringBetween(now, thirtyDaysOut)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<TenantEntity>> = tenantRepository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val properties: StateFlow<List<PropertyEntity>> = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun ensureCurrentMonthRentGenerated() {
        viewModelScope.launch {
            generateMonthlyRentUseCase()
        }
    }
}
