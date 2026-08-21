package com.example.rentmanagement.ui.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.PropertyEntity
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.domain.model.PropertyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    val properties: StateFlow<List<PropertyEntity>> = repository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun loadForEdit(propertyId: Long, onLoaded: (PropertyEntity?) -> Unit) {
        viewModelScope.launch { onLoaded(repository.getPropertyById(propertyId)) }
    }

    fun save(
        id: Long,
        name: String,
        type: PropertyType,
        address: String,
        city: String,
        state: String,
        pinCode: String,
        ownerName: String?,
        contactNumber: String?,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank()) {
            _saveError.value = "Property name is required"
            return
        }
        viewModelScope.launch {
            val entity = PropertyEntity(
                id = id,
                name = name.trim(),
                type = type,
                address = address.trim(),
                city = city.trim(),
                state = state.trim(),
                pinCode = pinCode.trim(),
                ownerName = ownerName?.trim(),
                contactNumber = contactNumber?.trim(),
                notes = notes?.trim()
            )
            if (id == 0L) repository.addProperty(entity) else repository.updateProperty(entity)
            _saveError.value = null
            onSuccess()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteProperty(id) }
    }

    fun clearError() { _saveError.value = null }
}
