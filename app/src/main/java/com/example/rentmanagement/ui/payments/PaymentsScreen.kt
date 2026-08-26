package com.example.rentmanagement.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.ui.components.MetricCard
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    onMenuClick: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val payments by viewModel.payments.collectAsState()
    val tenants by viewModel.tenants.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        if (payments.isEmpty()) {
            EmptyState(
                "No payments recorded yet",
                "Record a payment from the Rent tab or Dashboard quick actions",
                Modifier.padding(padding),
                icon = Icons.Default.Payments
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    MetricCard(
                        label = "Total Collected",
                        value = CurrencyFormatter.format(payments.sumOf { it.amount }),
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }
                items(payments, key = { it.id }) { payment ->
                    val tenantName = tenants.find { it.id == payment.tenantId }?.fullName ?: "Tenant"
                    Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(Spacing.lg)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tenantName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text(DateUtils.formatDate(payment.paymentDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    CurrencyFormatter.format(payment.amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(Spacing.sm))
                            AssistChip(onClick = {}, label = { Text(payment.paymentMethod.name.replace('_', ' ')) })
                            if (!payment.referenceNumber.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text("Ref: ${payment.referenceNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (!payment.receiptNumber.isNullOrBlank()) {
                                Text("Receipt: ${payment.receiptNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
