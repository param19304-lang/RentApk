package com.example.rentmanagement.ui.tenants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.TenantEntity
import com.example.rentmanagement.data.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(
    private val repository: TenantRepository
) : ViewModel() {

    val tenants: StateFlow<List<TenantEntity>> = repository.getAllTenants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
