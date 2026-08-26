package com.example.rentmanagement.ui.leases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.data.entities.PropertyEntity
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.domain.model.UnitStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LeaseDetailViewModel @Inject constructor(
    private val leaseRepository: LeaseRepository,
    private val unitRepository: UnitRepository,
    rentRepository: RentRepository,
    tenantRepository: TenantRepository,
    propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val leaseId: Long = savedStateHandle.get<Long>("leaseId") ?: 0L

    private val _lease = MutableStateFlow<LeaseEntity?>(null)
    val lease: StateFlow<LeaseEntity?> = _lease

    private val _tenant = MutableStateFlow<TenantEntity?>(null)
    val tenant: StateFlow<TenantEntity?> = _tenant

    private val _property = MutableStateFlow<PropertyEntity?>(null)
    val property: StateFlow<PropertyEntity?> = _property

    private val _unit = MutableStateFlow<UnitEntity?>(null)
    val unit: StateFlow<UnitEntity?> = _unit

    val rentHistory: StateFlow<List<RentEntity>> = rentRepository.getRentForLease(leaseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val l = leaseRepository.getLeaseById(leaseId)
            _lease.value = l
            if (l != null) {
                _tenant.value = tenantRepository.getTenantById(l.tenantId)
                _property.value = propertyRepository.getPropertyById(l.propertyId)
                _unit.value = unitRepository.getUnitById(l.unitId)
            }
        }
    }

    fun terminateLease(onDone: () -> Unit = {}) {
        val current = _lease.value ?: return
        viewModelScope.launch {
            withContext(NonCancellable) {
                leaseRepository.updateLease(current.copy(status = LeaseStatus.TERMINATED, terminatedAt = System.currentTimeMillis()))
                unitRepository.updateOccupancy(current.unitId, UnitStatus.VACANT, null)
            }
            _lease.value = leaseRepository.getLeaseById(leaseId)
            onDone()
        }
    }
}
