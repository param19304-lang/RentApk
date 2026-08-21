package com.example.rentmanagement.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.components.StatCard
import com.example.rentmanagement.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddProperty: () -> Unit,
    onAddTenant: () -> Unit,
    onRecordPayment: () -> Unit,
    onAddExpense: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ensureCurrentMonthRentGenerated()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Dashboard") }) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                QuickActions(onAddProperty, onAddTenant, onRecordPayment, onAddExpense)
            }
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader("Overview")
            }
            item { StatCard("Total Properties", stats.totalProperties.toString()) }
            item { StatCard("Total Units", stats.totalUnits.toString()) }
            item { StatCard("Occupied Units", stats.occupiedUnits.toString()) }
            item { StatCard("Vacant Units", stats.vacantUnits.toString()) }
            item { StatCard("Expected Rent (Month)", CurrencyFormatter.format(stats.expectedRentThisMonth)) }
            item { StatCard("Collected Rent (Month)", CurrencyFormatter.format(stats.collectedRentThisMonth)) }
            item { StatCard("Pending Rent", CurrencyFormatter.format(stats.pendingRent)) }
            item { StatCard("Overdue Rent", CurrencyFormatter.format(stats.overdueRent)) }
            item { StatCard("Total Expenses (Month)", CurrencyFormatter.format(stats.totalExpensesThisMonth)) }
            item { StatCard("Net Income (Month)", CurrencyFormatter.format(stats.netIncomeThisMonth)) }
        }
    }
}

@Composable
private fun QuickActions(
    onAddProperty: () -> Unit,
    onAddTenant: () -> Unit,
    onRecordPayment: () -> Unit,
    onAddExpense: () -> Unit
) {
    Column {
        SectionHeader("Quick Actions")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChipRow("Add Property", onAddProperty)
            AssistChipRow("Add Tenant", onAddTenant)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChipRow("Record Payment", onRecordPayment)
            AssistChipRow("Add Expense", onAddExpense)
        }
    }
}

@Composable
private fun AssistChipRow(label: String, onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text(label, fontWeight = FontWeight.Medium) })
}
