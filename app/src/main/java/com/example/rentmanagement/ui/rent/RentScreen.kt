package com.example.rentmanagement.ui.rent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.ui.components.AppSearchBar
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

private val statusFilterOptions: List<PaymentStatus?> = listOf(null) + PaymentStatus.values().toList()

private fun statusFilterLabel(status: PaymentStatus?): String = status?.name?.replace('_', ' ') ?: "All"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentScreen(
    onMenuClick: () -> Unit,
    onRecordPayment: (Long) -> Unit,
    viewModel: RentViewModel = hiltViewModel()
) {
    val filteredRentRecords by viewModel.filteredRentRecords.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val billingMonth by viewModel.billingMonth.collectAsState()
    val summary by viewModel.monthSummary.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rent") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                },
                actions = {
                    IconButton(onClick = { viewModel.generateForCurrentMonth() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Generate this month's rent")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month") }
                Text(billingMonth, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.Default.ChevronRight, contentDescription = "Next month") }
            }

            Card(
                shape = RoundedCornerShape(Radius.card),
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)
            ) {
                Column(Modifier.padding(Spacing.lg)) {
                    SummaryRow("Expected", summary.expected)
                    SummaryRow("Collected", summary.collected)
                    SummaryRow("Pending", summary.pending)
                    SummaryRow("Overdue", summary.overdue, isLast = true)
                }
            }

            Spacer(Modifier.height(Spacing.md))
            Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    placeholder = "Search tenant..."
                )
                Spacer(Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(statusFilterOptions) { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.setStatusFilter(status) },
                            label = { Text(statusFilterLabel(status)) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            if (filteredRentRecords.isEmpty()) {
                EmptyState(
                    "No rent records found",
                    "Try a different filter, or tap refresh to generate rent for active leases",
                    Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredRentRecords, key = { it.id }) { rent ->
                        val tenantName = tenants.find { it.id == rent.tenantId }?.fullName ?: "Tenant #${rent.tenantId}"
                        Card(
                            shape = RoundedCornerShape(Radius.card),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onRecordPayment(rent.id) }
                        ) {
                            Column(Modifier.padding(Spacing.lg)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(tenantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    StatusBadge(status = rent.status)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Due: ${DateUtils.formatDate(rent.dueDate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(Spacing.sm))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Payable", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(CurrencyFormatter.format(rent.totalPayable), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column {
                                        Text("Paid", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(CurrencyFormatter.format(rent.amountPaid), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Remaining", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(CurrencyFormatter.format(rent.remainingAmount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double, isLast: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(CurrencyFormatter.format(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
