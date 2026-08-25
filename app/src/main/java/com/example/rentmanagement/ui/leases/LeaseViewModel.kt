package com.example.rentmanagement.ui.leases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.data.entities.PropertyEntity
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
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
class LeaseViewModel @Inject constructor(
    private val leaseRepository: LeaseRepository,
    private val unitRepository: UnitRepository,
    propertyRepository: PropertyRepository,
    tenantRepository: TenantRepository
) : ViewModel() {

    val leases: StateFlow<List<LeaseEntity>> = leaseRepository.getAllLeases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val properties: StateFlow<List<PropertyEntity>> = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<TenantEntity>> = tenantRepository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _unitsForProperty = MutableStateFlow<List<UnitEntity>>(emptyList())
    val unitsForProperty: StateFlow<List<UnitEntity>> = _unitsForProperty

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun loadUnitsForProperty(propertyId: Long) {
        viewModelScope.launch {
            unitRepository.getUnitsForProperty(propertyId).collect { _unitsForProperty.value = it }
        }
    }

    fun createLease(
        propertyId: Long,
        unitId: Long,
        tenantId: Long,
        startDate: Long,
        endDate: Long,
        rentStartDate: Long,
        monthlyRent: Double,
        securityDeposit: Double,
        rentDueDay: Int,
        gracePeriodDays: Int,
        lateFee: Double,
        noticePeriodDays: Int,
        rentEscalationPercent: Double,
        onSuccess: () -> Unit
    ) {
        if (propertyId <= 0 || unitId <= 0 || tenantId <= 0) {
            _saveError.value = "Select property, unit, and tenant"
            return
        }
        if (endDate < startDate) {
            _saveError.value = "Lease end date cannot be before start date"
            return
        }
        if (rentStartDate < startDate) {
            _saveError.value = "Rent start date cannot be before the lease start date"
            return
        }
        if (rentStartDate > endDate) {
            _saveError.value = "Rent start date cannot be after the lease end date"
            return
        }
        if (monthlyRent < 0) {
            _saveError.value = "Rent amount cannot be negative"
            return
        }
        viewModelScope.launch {
            val existingActive = leaseRepository.getActiveLeaseForUnit(unitId)
            if (existingActive != null) {
                _saveError.value = "This unit already has an active tenant/lease"
                return@launch
            }
            leaseRepository.addLease(
                LeaseEntity(
                    propertyId = propertyId, unitId = unitId, tenantId = tenantId,
                    startDate = startDate, endDate = endDate, rentStartDate = rentStartDate,
                    monthlyRent = monthlyRent,
                    securityDeposit = securityDeposit, rentDueDay = rentDueDay,
                    gracePeriodDays = gracePeriodDays, lateFee = lateFee,
                    noticePeriodDays = noticePeriodDays, rentEscalationPercent = rentEscalationPercent,
                    status = LeaseStatus.ACTIVE
                )
            )
            unitRepository.updateOccupancy(unitId, UnitStatus.OCCUPIED, tenantId)
            _saveError.value = null
            onSuccess()
        }
    }

    fun terminateLease(lease: LeaseEntity) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                leaseRepository.updateLease(lease.copy(status = LeaseStatus.TERMINATED, terminatedAt = System.currentTimeMillis()))
                unitRepository.updateOccupancy(lease.unitId, UnitStatus.VACANT, null)
            }
        }
    }
}
