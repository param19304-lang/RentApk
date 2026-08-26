package com.example.rentmanagement.ui.leases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.ui.components.ConfirmDialog
import com.example.rentmanagement.ui.components.DangerButton
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseDetailScreen(
    onBack: () -> Unit,
    viewModel: LeaseDetailViewModel = hiltViewModel()
) {
    val lease by viewModel.lease.collectAsState()
    val tenant by viewModel.tenant.collectAsState()
    val property by viewModel.property.collectAsState()
    val unit by viewModel.unit.collectAsState()
    val rentHistory by viewModel.rentHistory.collectAsState()
    var showTerminateConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lease Details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        val l = lease
        if (l == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            modifier = Modifier.padding(padding)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(tenant?.fullName ?: "Tenant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(property?.name.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(status = l.status)
                }
            }

            item {
                SectionHeader("Lease Terms")
                Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.lg)) {
                        DetailRow("Unit", unit?.unitName ?: "-")
                        DetailRow("Lease Start", DateUtils.formatDate(l.startDate))
                        DetailRow("Lease End", DateUtils.formatDate(l.endDate))
                        if (l.rentStartDate != l.startDate) {
                            DetailRow("Rent Starts", DateUtils.formatDate(l.rentStartDate))
                        }
                        DetailRow("Monthly Rent", CurrencyFormatter.format(l.monthlyRent))
                        DetailRow("Security Deposit", CurrencyFormatter.format(l.securityDeposit))
                        DetailRow("Rent Due Day", "${l.rentDueDay} of each month")
                        DetailRow("Grace Period", "${l.gracePeriodDays} days")
                        DetailRow("Late Fee", CurrencyFormatter.format(l.lateFee))
                        DetailRow("Notice Period", "${l.noticePeriodDays} days")
                        DetailRow("Rent Escalation", "${l.rentEscalationPercent}%", showDivider = false)
                    }
                }
            }

            if (l.status == LeaseStatus.ACTIVE) {
                item {
                    DangerButton(text = "Terminate Lease", onClick = { showTerminateConfirm = true })
                }
            }

            item { SectionHeader("Rent History") }

            if (rentHistory.isEmpty()) {
                item {
                    Text(
                        "No rent records yet for this lease",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(rentHistory, key = { it.id }) { rent ->
                    Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rent.billingMonth, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("Due ${DateUtils.formatDate(rent.dueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(CurrencyFormatter.format(rent.totalPayable), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(2.dp))
                                StatusBadge(status = rent.status)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.md)) }
        }
    }

    if (showTerminateConfirm) {
        ConfirmDialog(
            title = "Terminate this lease?",
            message = "The unit will be marked vacant. Rent and payment history will be preserved.",
            confirmLabel = "Terminate",
            onConfirm = {
                viewModel.terminateLease()
                showTerminateConfirm = false
            },
            onDismiss = { showTerminateConfirm = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String, showDivider: Boolean = true) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    if (showDivider) HorizontalDivider()
}
