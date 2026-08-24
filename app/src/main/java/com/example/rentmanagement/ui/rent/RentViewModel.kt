package com.example.rentmanagement.ui.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.domain.usecase.GenerateMonthlyRentUseCase
import com.example.rentmanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RentMonthSummary(
    val expected: Double = 0.0,
    val collected: Double = 0.0,
    val pending: Double = 0.0,
    val overdue: Double = 0.0
)

@HiltViewModel
class RentViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val tenantRepository: TenantRepository,
    private val generateMonthlyRentUseCase: GenerateMonthlyRentUseCase
) : ViewModel() {

    private val _billingMonth = MutableStateFlow(DateUtils.currentBillingMonth())
    val billingMonth: StateFlow<String> = _billingMonth

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _statusFilter = MutableStateFlow<PaymentStatus?>(null)
    val statusFilter: StateFlow<PaymentStatus?> = _statusFilter

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val rentRecords: StateFlow<List<RentEntity>> = _billingMonth
        .flatMapLatest { month -> rentRepository.getRentForMonth(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<TenantEntity>> = tenantRepository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthSummary: StateFlow<RentMonthSummary> = rentRecords.map { list ->
        RentMonthSummary(
            expected = list.sumOf { it.totalPayable },
            collected = list.sumOf { it.amountPaid },
            pending = list.filter { it.status == PaymentStatus.PENDING || it.status == PaymentStatus.PARTIALLY_PAID }.sumOf { it.remainingAmount },
            overdue = list.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.remainingAmount }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RentMonthSummary())

    val filteredRentRecords: StateFlow<List<RentEntity>> = combine(
        rentRecords, tenants, _searchQuery, _statusFilter
    ) { records, tenantList, query, filter ->
        records.filter { rent ->
            val matchesStatus = filter == null || rent.status == filter
            val tenantName = tenantList.find { it.id == rent.tenantId }?.fullName.orEmpty()
            val matchesQuery = query.isBlank() || tenantName.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setBillingMonth(month: String) { _billingMonth.value = month }

    fun previousMonth() {
        _billingMonth.value = DateUtils.billingMonthOf(DateUtils.addMonths(DateUtils.startOfMonth(_billingMonth.value), -1))
    }

    fun nextMonth() {
        _billingMonth.value = DateUtils.billingMonthOf(DateUtils.addMonths(DateUtils.startOfMonth(_billingMonth.value), 1))
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun setStatusFilter(status: PaymentStatus?) { _statusFilter.value = status }

    fun generateForCurrentMonth() {
        viewModelScope.launch { generateMonthlyRentUseCase(_billingMonth.value) }
    }
}
