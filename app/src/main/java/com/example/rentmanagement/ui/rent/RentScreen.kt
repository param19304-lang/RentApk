package com.example.rentmanagement.ui.rent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentScreen(
    onRecordPayment: (Long) -> Unit,
    viewModel: RentViewModel = hiltViewModel()
) {
    val rentRecords by viewModel.rentRecords.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val billingMonth by viewModel.billingMonth.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rent · $billingMonth") },
                actions = {
                    IconButton(onClick = { viewModel.generateForCurrentMonth() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Generate this month's rent")
                    }
                }
            )
        }
    ) { padding ->
        if (rentRecords.isEmpty()) {
            EmptyState(
                "No rent records for $billingMonth",
                "Tap refresh to generate rent for active leases",
                Modifier.padding(padding)
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(rentRecords, key = { it.id }) { rent ->
                    val tenantName = tenants.find { it.id == rent.tenantId }?.fullName ?: "Tenant #${rent.tenantId}"
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { onRecordPayment(rent.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tenantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                StatusChip(rent.status)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Due: ${DateUtils.formatDate(rent.dueDate)}", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Payable: ${CurrencyFormatter.format(rent.totalPayable)} · Paid: ${CurrencyFormatter.format(rent.amountPaid)} · Remaining: ${CurrencyFormatter.format(rent.remainingAmount)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: PaymentStatus) {
    val (label, color) = when (status) {
        PaymentStatus.PAID -> "Paid" to MaterialTheme.colorScheme.primary
        PaymentStatus.PARTIALLY_PAID -> "Partial" to MaterialTheme.colorScheme.tertiary
        PaymentStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.secondary
        PaymentStatus.OVERDUE -> "Overdue" to MaterialTheme.colorScheme.error
    }
    AssistChip(onClick = {}, label = { Text(label) }, colors = AssistChipDefaults.assistChipColors(labelColor = color))
}
