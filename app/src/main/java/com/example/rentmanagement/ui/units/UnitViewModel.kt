package com.example.rentmanagement.ui.units

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.UnitStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnitViewModel @Inject constructor(
    private val repository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val propertyId: Long = savedStateHandle.get<Long>("propertyId") ?: 0L

    val units: StateFlow<List<UnitEntity>> = repository.getUnitsForProperty(propertyId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun loadForEdit(unitId: Long, onLoaded: (UnitEntity?) -> Unit) {
        viewModelScope.launch { onLoaded(repository.getUnitById(unitId)) }
    }

    fun save(
        id: Long,
        propertyId: Long,
        unitName: String,
        floor: String?,
        monthlyRent: Double,
        securityDeposit: Double?,
        maintenanceCharge: Double?,
        electricityCharge: Double?,
        waterCharge: Double?,
        status: UnitStatus,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        if (unitName.isBlank()) {
            _saveError.value = "Unit name/number is required"
            return
        }
        if (monthlyRent < 0) {
            _saveError.value = "Monthly rent cannot be negative"
            return
        }
        viewModelScope.launch {
            val entity = UnitEntity(
                id = id,
                propertyId = propertyId,
                unitName = unitName.trim(),
                floor = floor?.trim(),
                monthlyRent = monthlyRent,
                securityDeposit = securityDeposit,
                maintenanceCharge = maintenanceCharge,
                electricityCharge = electricityCharge,
                waterCharge = waterCharge,
                status = status,
                notes = notes?.trim()
            )
            if (id == 0L) repository.addUnit(entity) else repository.updateUnit(entity)
            _saveError.value = null
            onSuccess()
        }
    }
}
