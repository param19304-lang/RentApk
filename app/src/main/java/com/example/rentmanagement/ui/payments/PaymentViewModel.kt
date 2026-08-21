package com.example.rentmanagement.ui.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.PaymentEntity
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.repository.PaymentRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.domain.model.PaymentMethod
import com.example.rentmanagement.domain.usecase.RecordPaymentResult
import com.example.rentmanagement.domain.usecase.RecordPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val rentRepository: RentRepository,
    private val recordPaymentUseCase: RecordPaymentUseCase
) : ViewModel() {

    val payments: StateFlow<List<PaymentEntity>> = paymentRepository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRent = MutableStateFlow<RentEntity?>(null)
    val selectedRent: StateFlow<RentEntity?> = _selectedRent

    private val _result = MutableStateFlow<RecordPaymentResult?>(null)
    val result: StateFlow<RecordPaymentResult?> = _result

    fun loadRent(rentId: Long) {
        viewModelScope.launch { _selectedRent.value = rentRepository.getRentById(rentId) }
    }

    fun recordPayment(
        rentId: Long,
        amount: Double,
        paymentDate: Long,
        method: PaymentMethod,
        referenceNumber: String?,
        notes: String?,
        allowAdvance: Boolean,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = recordPaymentUseCase(rentId, amount, paymentDate, method, referenceNumber, notes, allowAdvance)
            _result.value = res
            if (res is RecordPaymentResult.Success) {
                _selectedRent.value = rentRepository.getRentById(rentId)
                onSuccess(res.receiptNumber)
            }
        }
    }

    fun clearResult() { _result.value = null }
}
