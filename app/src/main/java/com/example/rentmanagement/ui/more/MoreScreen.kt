package com.example.rentmanagement.ui.more

import androidx.compose.foundation.clickable
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
    isAdmin: Boolean,
    onTenants: () -> Unit,
    onExpenses: () -> Unit,
    onLeases: () -> Unit,
    onReports: () -> Unit,
    onReminders: () -> Unit,
    onDocuments: () -> Unit,
    onSettings: () -> Unit,
    onBackupRestore: () -> Unit,
    onUsers: () -> Unit
) {
    val menuItems = buildList {
        add(MoreItem("Tenants", "Manage tenant profiles", onTenants))
        add(MoreItem("Expenses", "Track property expenses", onExpenses))
        add(MoreItem("Leases", "Manage lease agreements", onLeases))
        add(MoreItem("Reports", "Rent, expense & income reports (Phase 2)", onReports))
        add(MoreItem("Reminders", "Rent & lease reminders (Phase 2)", onReminders))
        add(MoreItem("Documents", "Tenant & lease documents (Phase 2)", onDocuments))
        if (isAdmin) {
            add(MoreItem("Users", "Manage admin & user accounts", onUsers))
        }
        add(MoreItem("Settings", "App theme, currency, notifications", onSettings))
        add(MoreItem("Backup & Restore", "Export / import your data (Phase 2)", onBackupRestore))
    }

    Scaffold(topBar = { TopAppBar(title = { Text("More") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(menuItems) { item ->
                ListItem(
                    headlineContent = { Text(item.label) },
                    supportingContent = { Text(item.subtitle) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.padding(4.dp)) },
                    modifier = Modifier.clickableRow(item.onClick)
                )
                Divider()
            }
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
