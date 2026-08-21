package com.example.rentmanagement.ui.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class MoreItem(val label: String, val subtitle: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onTenants: () -> Unit,
    onExpenses: () -> Unit,
    onLeases: () -> Unit,
    onReports: () -> Unit,
    onReminders: () -> Unit,
    onDocuments: () -> Unit,
    onSettings: () -> Unit,
    onBackupRestore: () -> Unit
) {
    val menuItems = listOf(
        MoreItem("Tenants", "Manage tenant profiles", onTenants),
        MoreItem("Expenses", "Track property expenses (Phase 2)", onExpenses),
        MoreItem("Leases", "Manage lease agreements", onLeases),
        MoreItem("Reports", "Rent, expense & income reports (Phase 2)", onReports),
        MoreItem("Reminders", "Rent & lease reminders (Phase 2)", onReminders),
        MoreItem("Documents", "Tenant & lease documents (Phase 2)", onDocuments),
        MoreItem("Settings", "App theme, currency, notifications", onSettings),
        MoreItem("Backup & Restore", "Export / import your data (Phase 2)", onBackupRestore)
    )

    Scaffold(topBar = { TopAppBar(title = { androidx.compose.material3.Text("More") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(menuItems) { item ->
                ListItem(
                    headlineContent = { androidx.compose.material3.Text(item.label) },
                    supportingContent = { androidx.compose.material3.Text(item.subtitle) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.padding(4.dp)) },
                    modifier = Modifier.clickableRow(item.onClick)
                )
                Divider()
            }
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
