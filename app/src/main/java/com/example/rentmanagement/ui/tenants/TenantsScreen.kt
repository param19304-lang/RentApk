package com.example.rentmanagement.ui.tenants

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantsScreen(
    onBack: () -> Unit,
    onAddTenant: () -> Unit,
    onOpenTenant: (Long) -> Unit,
    viewModel: TenantViewModel = hiltViewModel()
) {
    val summaries by viewModel.tenantSummaries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tenants") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTenant) { Icon(Icons.Default.Add, contentDescription = "Add tenant") }
        }
    ) { padding ->
        if (summaries.isEmpty()) {
            EmptyState(
                "No tenants yet",
                "Add your first tenant to start tracking rent.",
                Modifier.padding(padding),
                icon = Icons.Default.People
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding)
            ) {
                items(summaries, key = { it.tenant.id }) { summary ->
                    Card(
                        shape = RoundedCornerShape(Radius.card),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenTenant(summary.tenant.id) }
                    ) {
                        Column(Modifier.padding(Spacing.lg)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(Spacing.sm))
                                    Text(summary.tenant.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                }
                                summary.paymentStatus?.let { StatusBadge(status = it) }
                            }

                            if (summary.unitName != null) {
                                Spacer(Modifier.height(Spacing.sm))
                                Text(summary.unitName, style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(summary.tenant.phoneNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            if (summary.monthlyRent != null || summary.nextDueDate != null) {
                                Spacer(Modifier.height(Spacing.sm))
                                HorizontalDivider()
                                Spacer(Modifier.height(Spacing.sm))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    if (summary.monthlyRent != null) {
                                        Column {
                                            Text("Rent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CurrencyFormatter.format(summary.monthlyRent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    if (summary.nextDueDate != null) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Next Due", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(DateUtils.formatDate(summary.nextDueDate), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
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
}
