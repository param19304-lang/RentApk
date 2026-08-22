package com.example.rentmanagement.ui.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.ExpenseEntity
import com.example.rentmanagement.data.repository.ExpenseRepository
import com.example.rentmanagement.data.repository.PropertyRepository
import com.example.rentmanagement.data.repository.RentRepository
import com.example.rentmanagement.data.repository.TenantRepository
import com.example.rentmanagement.data.repository.UnitRepository
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.utils.DateUtils
import com.example.rentmanagement.utils.ReportExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class RentCollectionReport(
    val expected: Double = 0.0,
    val collected: Double = 0.0,
    val pending: Double = 0.0,
    val overdue: Double = 0.0,
    val collectionPercent: Double = 0.0
)

data class TenantReportRow(
    val tenantName: String,
    val unitName: String,
    val rent: Double,
    val paid: Double,
    val pending: Double,
    val overdue: Double
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val expenseRepository: ExpenseRepository,
    propertyRepository: PropertyRepository,
    tenantRepository: TenantRepository,
    unitRepository: UnitRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _billingMonth = MutableStateFlow(DateUtils.currentBillingMonth())
    val billingMonth: StateFlow<String> = _billingMonth

    fun previousMonth() {
        _billingMonth.value = DateUtils.billingMonthOf(DateUtils.addMonths(DateUtils.startOfMonth(_billingMonth.value), -1))
    }

    fun nextMonth() {
        _billingMonth.value = DateUtils.billingMonthOf(DateUtils.addMonths(DateUtils.startOfMonth(_billingMonth.value), 1))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rentForMonth = _billingMonth
        .flatMapLatest { month -> rentRepository.getRentForMonth(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentCollectionReport: StateFlow<RentCollectionReport> = rentForMonth.map { list ->
        val expected = list.sumOf { it.totalPayable }
        val collected = list.sumOf { it.amountPaid }
        val pending = list.filter { it.status == PaymentStatus.PENDING || it.status == PaymentStatus.PARTIALLY_PAID }.sumOf { it.remainingAmount }
        val overdue = list.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.remainingAmount }
        val pct = if (expected > 0) (collected / expected) * 100 else 0.0
        RentCollectionReport(expected, collected, pending, overdue, pct)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RentCollectionReport())

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesForMonth: StateFlow<List<ExpenseEntity>> = _billingMonth.flatMapLatest { month ->
        expenseRepository.getAllExpenses().map { list -> list.filter { DateUtils.billingMonthOf(it.date) == month } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpensesForMonth: StateFlow<Double> = expensesForMonth
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netIncomeForMonth: StateFlow<Double> = combine(rentCollectionReport, totalExpensesForMonth) { rent, expenses ->
        rent.collected - expenses
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val tenantReport: StateFlow<List<TenantReportRow>> = combine(
        rentForMonth,
        tenantRepository.getAllTenants(),
        unitRepository.getAllUnits()
    ) { rentList, tenantList, unitList ->
        rentList.map { rent ->
            val tenant = tenantList.find { it.id == rent.tenantId }
            val unit = unitList.find { it.id == rent.unitId }
            TenantReportRow(
                tenantName = tenant?.fullName ?: "Tenant #${rent.tenantId}",
                unitName = unit?.unitName ?: "Unit #${rent.unitId}",
                rent = rent.totalPayable,
                paid = rent.amountPaid,
                pending = if (rent.status != PaymentStatus.OVERDUE) rent.remainingAmount.coerceAtLeast(0.0) else 0.0,
                overdue = if (rent.status == PaymentStatus.OVERDUE) rent.remainingAmount else 0.0
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exportedFile = MutableStateFlow<Pair<File, String>?>(null)
    val exportedFile: StateFlow<Pair<File, String>?> = _exportedFile

    fun clearExportedFile() { _exportedFile.value = null }

    fun exportRentCollection(asPdf: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val report = rentCollectionReport.value
            val month = _billingMonth.value
            val headers = listOf("Metric", "Amount")
            val rows = listOf(
                listOf("Expected Rent", report.expected.toString()),
                listOf("Collected Rent", report.collected.toString()),
                listOf("Pending Rent", report.pending.toString()),
                listOf("Overdue Rent", report.overdue.toString()),
                listOf("Collection %", "%.1f".format(report.collectionPercent))
            )
            val file = if (asPdf) {
                ReportExporter.exportPdf(appContext, "rent_collection_$month.pdf", "Rent Collection Report — $month", headers, rows)
            } else {
                ReportExporter.exportCsv(appContext, "rent_collection_$month.csv", headers, rows)
            }
            _exportedFile.value = file to if (asPdf) "application/pdf" else "text/csv"
        }
    }

    fun exportExpenseReport(asPdf: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val month = _billingMonth.value
            val headers = listOf("Category", "Amount", "Vendor", "Description", "Date")
            val rows = expensesForMonth.value.map {
                listOf(
                    it.category.name, it.amount.toString(), it.vendor.orEmpty(),
                    it.description.orEmpty(), DateUtils.formatDate(it.date)
                )
            }
            val file = if (asPdf) {
                ReportExporter.exportPdf(appContext, "expenses_$month.pdf", "Expense Report — $month", headers, rows)
            } else {
                ReportExporter.exportCsv(appContext, "expenses_$month.csv", headers, rows)
            }
            _exportedFile.value = file to if (asPdf) "application/pdf" else "text/csv"
        }
    }

    fun exportTenantReport(asPdf: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val month = _billingMonth.value
            val headers = listOf("Tenant", "Unit", "Rent", "Paid", "Pending", "Overdue")
            val rows = tenantReport.value.map {
                listOf(it.tenantName, it.unitName, it.rent.toString(), it.paid.toString(), it.pending.toString(), it.overdue.toString())
            }
            val file = if (asPdf) {
                ReportExporter.exportPdf(appContext, "tenants_$month.pdf", "Tenant Report — $month", headers, rows)
            } else {
                ReportExporter.exportCsv(appContext, "tenants_$month.csv", headers, rows)
            }
            _exportedFile.value = file to if (asPdf) "application/pdf" else "text/csv"
        }
    }
}
