package com.example.rentmanagement.ui.tenants

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantsScreen(
    onBack: () -> Unit,
    onAddTenant: () -> Unit,
    onOpenTenant: (Long) -> Unit,
    viewModel: TenantViewModel = hiltViewModel()
) {
    val tenants by viewModel.tenants.collectAsState()

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
        if (tenants.isEmpty()) {
            EmptyState("No tenants yet", "Tap + to add a tenant", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(tenants, key = { it.id }) { tenant ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { onOpenTenant(tenant.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(tenant.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(tenant.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
