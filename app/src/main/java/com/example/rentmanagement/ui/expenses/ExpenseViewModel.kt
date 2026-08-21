package com.example.rentmanagement.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.ExpenseEntity
import com.example.rentmanagement.data.entities.PropertyEntity
import com.example.rentmanagement.data.repository.ExpenseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    propertyRepository: PropertyRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val properties: StateFlow<List<PropertyEntity>> = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun save(
        id: Long,
        propertyId: Long,
        category: ExpenseCategory,
        amount: Double,
        date: Long,
        description: String?,
        vendor: String?,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        if (propertyId <= 0) {
            _saveError.value = "Select a property"
            return
        }
        if (amount < 0) {
            _saveError.value = "Expense amount cannot be negative"
            return
        }
        viewModelScope.launch {
            val entity = ExpenseEntity(
                id = id,
                propertyId = propertyId,
                category = category,
                amount = amount,
                date = date,
                description = description?.trim()?.ifBlank { null },
                vendor = vendor?.trim()?.ifBlank { null },
                notes = notes?.trim()?.ifBlank { null }
            )
            if (id == 0L) repository.addExpense(entity) else repository.updateExpense(entity)
            _saveError.value = null
            onSuccess()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteExpense(id) }
    }
}
