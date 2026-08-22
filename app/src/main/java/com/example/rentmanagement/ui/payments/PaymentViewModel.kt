package com.example.rentmanagement.ui.payments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.PaymentEntity
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.data.preferences.AppPreferences
import com.example.rentmanagement.data.repository.PaymentRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.PaymentMethod
import com.example.rentmanagement.domain.usecase.RecordPaymentResult
import com.example.rentmanagement.domain.usecase.RecordPaymentUseCase
import com.example.rentmanagement.utils.PdfReceiptGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val rentRepository: RentRepository,
    private val propertyRepository: PropertyRepository,
    private val unitRepository: UnitRepository,
    private val tenantRepository: TenantRepository,
    private val appPreferences: AppPreferences,
    private val recordPaymentUseCase: RecordPaymentUseCase,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val payments: StateFlow<List<PaymentEntity>> = paymentRepository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRent = MutableStateFlow<RentEntity?>(null)
    val selectedRent: StateFlow<RentEntity?> = _selectedRent

    private val _result = MutableStateFlow<RecordPaymentResult?>(null)
    val result: StateFlow<RecordPaymentResult?> = _result

    private val _lastPaymentId = MutableStateFlow<Long?>(null)
    val lastPaymentId: StateFlow<Long?> = _lastPaymentId

    private val _receiptFile = MutableStateFlow<File?>(null)
    val receiptFile: StateFlow<File?> = _receiptFile

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
                _lastPaymentId.value = res.paymentId
                onSuccess(res.receiptNumber)
            }
        }
    }

    fun generateReceiptPdf(rentId: Long, paymentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val rent = rentRepository.getRentById(rentId) ?: return@launch
            val payment = paymentRepository.getPaymentById(paymentId) ?: return@launch
            val property = propertyRepository.getPropertyById(rent.propertyId)
            val unit = unitRepository.getUnitById(rent.unitId)
            val tenant = tenantRepository.getTenantById(rent.tenantId)
            val landlordName = appPreferences.landlordName.first()
            val currencySymbol = appPreferences.currencySymbol.first()

            val data = PdfReceiptGenerator.ReceiptData(
                landlordName = landlordName,
                propertyName = property?.name ?: "Property",
                unitName = unit?.unitName ?: "Unit",
                tenantName = tenant?.fullName ?: "Tenant",
                amount = payment.amount,
                paymentDate = payment.paymentDate,
                paymentMethod = payment.paymentMethod.name,
                transactionId = payment.referenceNumber,
                billingMonth = rent.billingMonth,
                remainingBalance = rent.remainingAmount,
                receiptNumber = payment.receiptNumber ?: "RCPT-${payment.id}",
                currencySymbol = currencySymbol
            )
            _receiptFile.value = PdfReceiptGenerator.generate(appContext, data)
        }
    }

    fun clearReceiptFile() { _receiptFile.value = null }
    fun clearResult() { _result.value = null }
}
