package com.example.rentmanagement.ui.tenants

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.LeaseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantDetailViewModel @Inject constructor(
    tenantRepository: TenantRepository,
    private val leaseRepository: LeaseRepository,
    rentRepository: RentRepository,
    private val unitRepository: UnitRepository,
    private val propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tenantId: Long = savedStateHandle.get<Long>("tenantId") ?: 0L

    val tenant: StateFlow<TenantEntity?> = tenantRepository.observeTenantById(tenantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rentHistory: StateFlow<List<RentEntity>> = rentRepository.getRentForTenant(tenantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeLease = MutableStateFlow<LeaseEntity?>(null)
    val activeLease: StateFlow<LeaseEntity?> = _activeLease

    private val _unitName = MutableStateFlow<String?>(null)
    val unitName: StateFlow<String?> = _unitName

    private val _propertyName = MutableStateFlow<String?>(null)
    val propertyName: StateFlow<String?> = _propertyName

    init {
        viewModelScope.launch {
            leaseRepository.getLeasesForTenant(tenantId).collect { leases ->
                val active = leases.find { it.status == LeaseStatus.ACTIVE }
                _activeLease.value = active
                if (active != null) {
                    _unitName.value = unitRepository.getUnitById(active.unitId)?.unitName
                    _propertyName.value = propertyRepository.getPropertyById(active.propertyId)?.name
                } else {
                    _unitName.value = null
                    _propertyName.value = null
                }
            }
        }
    }
}
