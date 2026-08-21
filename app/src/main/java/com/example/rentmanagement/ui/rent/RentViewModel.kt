package com.example.rentmanagement.ui.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.domain.usecase.GenerateMonthlyRentUseCase
import com.example.rentmanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val tenantRepository: TenantRepository,
    private val generateMonthlyRentUseCase: GenerateMonthlyRentUseCase
) : ViewModel() {

    private val _billingMonth = MutableStateFlow(DateUtils.currentBillingMonth())
    val billingMonth: StateFlow<String> = _billingMonth

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val rentRecords: StateFlow<List<RentEntity>> = _billingMonth
        .flatMapLatest { month -> rentRepository.getRentForMonth(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<TenantEntity>> = tenantRepository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setBillingMonth(month: String) { _billingMonth.value = month }

    fun generateForCurrentMonth() {
        viewModelScope.launch { generateMonthlyRentUseCase(_billingMonth.value) }
    }
}
