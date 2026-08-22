package com.example.rentmanagement.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.StatCard
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.ShareUtils

private val tabs = listOf("Rent", "Expenses", "Income", "Tenants")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val billingMonth by viewModel.billingMonth.collectAsState()
    val rentReport by viewModel.rentCollectionReport.collectAsState()
    val expenses by viewModel.expensesForMonth.collectAsState()
    val totalExpenses by viewModel.totalExpensesForMonth.collectAsState()
    val netIncome by viewModel.netIncomeForMonth.collectAsState()
    val tenantReport by viewModel.tenantReport.collectAsState()
    val exportedFile by viewModel.exportedFile.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(exportedFile) {
        exportedFile?.let { (file, mimeType) ->
            ShareUtils.shareFile(context, file, mimeType)
            viewModel.clearExportedFile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month") }
                Text(billingMonth, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.Default.ChevronRight, contentDescription = "Next month") }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> RentCollectionTab(rentReport, onExportCsv = { viewModel.exportRentCollection(false) }, onExportPdf = { viewModel.exportRentCollection(true) })
                1 -> ExpenseTab(expenses.size, totalExpenses, onExportCsv = { viewModel.exportExpenseReport(false) }, onExportPdf = { viewModel.exportExpenseReport(true) })
                2 -> IncomeTab(rentReport.collected, totalExpenses, netIncome)
                3 -> TenantTab(tenantReport, onExportCsv = { viewModel.exportTenantReport(false) }, onExportPdf = { viewModel.exportTenantReport(true) })
            }
        }
    }
}

@Composable
private fun ExportButtons(onExportCsv: () -> Unit, onExportPdf: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedButton(onClick = onExportCsv, modifier = Modifier.weight(1f)) { Text("Export CSV") }
        OutlinedButton(onClick = onExportPdf, modifier = Modifier.weight(1f)) { Text("Export PDF") }
    }
}

@Composable
private fun RentCollectionTab(report: RentCollectionReport, onExportCsv: () -> Unit, onExportPdf: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        StatCard("Expected Rent", CurrencyFormatter.format(report.expected), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Collected Rent", CurrencyFormatter.format(report.collected), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Pending Rent", CurrencyFormatter.format(report.pending), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Overdue Rent", CurrencyFormatter.format(report.overdue), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Collection %", "%.1f%%".format(report.collectionPercent), Modifier.fillMaxWidth())
    }
    ExportButtons(onExportCsv, onExportPdf)
}

@Composable
private fun ExpenseTab(count: Int, total: Double, onExportCsv: () -> Unit, onExportPdf: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        StatCard("Expense Entries", count.toString(), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Total Expenses", CurrencyFormatter.format(total), Modifier.fillMaxWidth())
    }
    ExportButtons(onExportCsv, onExportPdf)
}

@Composable
private fun IncomeTab(collected: Double, expenses: Double, net: Double) {
    Column(Modifier.padding(16.dp)) {
        StatCard("Total Rent Collected", CurrencyFormatter.format(collected), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Total Expenses", CurrencyFormatter.format(expenses), Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        StatCard("Net Income", CurrencyFormatter.format(net), Modifier.fillMaxWidth())
    }
}

@Composable
private fun TenantTab(rows: List<TenantReportRow>, onExportCsv: () -> Unit, onExportPdf: () -> Unit) {
    Column {
        ExportButtons(onExportCsv, onExportPdf)
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
            items(rows) { row ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(row.tenantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(row.unitName, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Rent: ${CurrencyFormatter.format(row.rent)} · Paid: ${CurrencyFormatter.format(row.paid)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Pending: ${CurrencyFormatter.format(row.pending)} · Overdue: ${CurrencyFormatter.format(row.overdue)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
