package com.example.rentmanagement.ui.tenants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.domain.model.PaymentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TenantSummary(
    val tenant: TenantEntity,
    val unitName: String?,
    val monthlyRent: Double?,
    val paymentStatus: PaymentStatus?,
    val nextDueDate: Long?
)

@HiltViewModel
class TenantViewModel @Inject constructor(
    private val repository: TenantRepository,
    leaseRepository: LeaseRepository,
    rentRepository: RentRepository,
    unitRepository: UnitRepository
) : ViewModel() {

    val tenants: StateFlow<List<TenantEntity>> = repository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val activeLeases = leaseRepository.getAllLeases()
        .map { leases -> leases.filter { it.status == LeaseStatus.ACTIVE } }

    private val allRent: StateFlow<List<RentEntity>> = rentRepository.getAllRentRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allUnits: StateFlow<List<UnitEntity>> = unitRepository.getAllUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenantSummaries: StateFlow<List<TenantSummary>> = combine(
        tenants, activeLeases, allRent, allUnits
    ) { tenantList, leases, rentList, unitList ->
        tenantList.map { tenant ->
            val lease = leases.find { it.tenantId == tenant.id }
            val unit = lease?.let { l -> unitList.find { it.id == l.unitId } }
            val latestRent = rentList.filter { it.tenantId == tenant.id }.maxByOrNull { it.dueDate }
            TenantSummary(
                tenant = tenant,
                unitName = unit?.unitName,
                monthlyRent = lease?.monthlyRent,
                paymentStatus = latestRent?.status,
                nextDueDate = latestRent?.dueDate
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun loadForEdit(tenantId: Long, onLoaded: (TenantEntity?) -> Unit) {
        viewModelScope.launch { onLoaded(repository.getTenantById(tenantId)) }
    }

    fun save(
        id: Long,
        fullName: String,
        phoneNumber: String,
        email: String?,
        idType: String?,
        idNumber: String?,
        address: String?,
        emergencyContact: String?,
        occupants: Int,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank()) {
            _saveError.value = "Full name is required"
            return
        }
        if (!phoneNumber.matches(Regex("^[+0-9 ()-]{7,15}$"))) {
            _saveError.value = "Enter a valid phone number"
            return
        }
        viewModelScope.launch {
            val entity = TenantEntity(
                id = id,
                fullName = fullName.trim(),
                phoneNumber = phoneNumber.trim(),
                email = email?.trim()?.ifBlank { null },
                idType = idType?.trim()?.ifBlank { null },
                idNumber = idNumber?.trim()?.ifBlank { null },
                address = address?.trim()?.ifBlank { null },
                emergencyContact = emergencyContact?.trim()?.ifBlank { null },
                occupants = occupants,
                notes = notes?.trim()?.ifBlank { null }
            )
            if (id == 0L) repository.addTenant(entity) else repository.updateTenant(entity)
            _saveError.value = null
            onSuccess()
        }
    }
}
