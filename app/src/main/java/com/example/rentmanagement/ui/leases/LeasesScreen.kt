package com.example.rentmanagement.ui.leases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeasesScreen(
    onBack: () -> Unit,
    onAddLease: () -> Unit,
    viewModel: LeaseViewModel = hiltViewModel()
) {
    val leases by viewModel.leases.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val properties by viewModel.properties.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leases") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLease) { Icon(Icons.Default.Add, contentDescription = "Add lease") }
        }
    ) { padding ->
        if (leases.isEmpty()) {
            EmptyState("No leases yet", "Tap + to create a lease", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(leases, key = { it.id }) { lease ->
                    val tenantName = tenants.find { it.id == lease.tenantId }?.fullName ?: "Tenant #${lease.tenantId}"
                    val propertyName = properties.find { it.id == lease.propertyId }?.name ?: "Property #${lease.propertyId}"
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tenantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                AssistChip(onClick = {}, label = { Text(lease.status.name) })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(propertyName, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${DateUtils.formatDate(lease.startDate)} — ${DateUtils.formatDate(lease.endDate)} · ${CurrencyFormatter.format(lease.monthlyRent)}/mo",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (lease.rentStartDate != lease.startDate) {
                                Text(
                                    "Rent starts ${DateUtils.formatDate(lease.rentStartDate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (lease.status.name == "ACTIVE") {
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.terminateLease(lease) }) { Text("Terminate lease") }
                            }
                        }
                    }
                }
            }
        }
    }
}
