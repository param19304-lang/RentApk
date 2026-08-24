package com.example.rentmanagement.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.DashboardTile
import com.example.rentmanagement.ui.components.MetricCard
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.theme.LocalSemanticColors
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils
import java.util.Calendar

private data class QuickStat(val label: String, val value: String, val icon: ImageVector, val accentColor: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onMenuClick: () -> Unit,
    onAddProperty: () -> Unit,
    onAddTenant: () -> Unit,
    onRecordPayment: () -> Unit,
    onAddExpense: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val recentPayments by viewModel.recentPayments.collectAsState()
    val upcomingDue by viewModel.upcomingDue.collectAsState()
    val expiringLeases by viewModel.expiringLeases.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val enabledTiles by viewModel.enabledTiles.collectAsState()
    val semantic = LocalSemanticColors.current

    LaunchedEffect(Unit) {
        viewModel.ensureCurrentMonthRentGenerated()
    }

    val quickStats = buildList {
        if (DashboardTile.PROPERTIES in enabledTiles) {
            add(QuickStat("Properties", stats.totalProperties.toString(), Icons.Default.Apartment, MaterialTheme.colorScheme.primary))
        }
        if (DashboardTile.UNITS in enabledTiles) {
            add(QuickStat("Units", stats.totalUnits.toString(), Icons.Default.Home, MaterialTheme.colorScheme.primary))
        }
        if (DashboardTile.TENANTS in enabledTiles) {
            add(QuickStat("Tenants", stats.totalTenants.toString(), Icons.Default.Person, MaterialTheme.colorScheme.primary))
        }
        if (DashboardTile.OCCUPIED in enabledTiles) {
            add(QuickStat("Occupied", stats.occupiedUnits.toString(), Icons.Default.CheckCircle, semantic.success))
        }
        if (DashboardTile.VACANT in enabledTiles) {
            add(QuickStat("Vacant", stats.vacantUnits.toString(), Icons.Default.Group, MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }

    val expenseIncomeStats = buildList {
        if (DashboardTile.TOTAL_EXPENSES in enabledTiles) {
            add(QuickStat("Total Expenses", CurrencyFormatter.format(stats.totalExpensesThisMonth), Icons.Default.Receipt, semantic.warning))
        }
        if (DashboardTile.NET_INCOME in enabledTiles) {
            add(
                QuickStat(
                    "Net Income", CurrencyFormatter.format(stats.netIncomeThisMonth), Icons.Default.AttachMoney,
                    if (stats.netIncomeThisMonth >= 0) semantic.success else semantic.error
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item { GreetingHeader() }

            if (quickStats.isNotEmpty()) {
                item { QuickStatsGrid(quickStats) }
            }

            item {
                Column {
                    SectionHeader("Quick Actions")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton("Property", Icons.Default.Apartment, onAddProperty)
                        QuickActionButton("Tenant", Icons.Default.Person, onAddTenant)
                        QuickActionButton("Payment", Icons.Default.Payments, onRecordPayment)
                        QuickActionButton("Expense", Icons.Default.AttachMoney, onAddExpense)
                    }
                }
            }

            if (DashboardTile.RENT_OVERVIEW in enabledTiles) {
                item {
                    Column {
                        SectionHeader("Rent Overview")
                        Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.card)) {
                            Column(Modifier.padding(Spacing.lg)) {
                                RentOverviewRow("Expected", stats.expectedRentThisMonth, MaterialTheme.colorScheme.onSurface)
                                RentOverviewRow("Collected", stats.collectedRentThisMonth, semantic.success)
                                RentOverviewRow("Pending", stats.pendingRent, semantic.warning)
                                RentOverviewRow("Overdue", stats.overdueRent, semantic.error, showDivider = false)
                            }
                        }
                    }
                }
            }

            if (expenseIncomeStats.isNotEmpty()) {
                item { QuickStatsGrid(expenseIncomeStats) }
            }

            if (DashboardTile.RECENT_PAYMENTS in enabledTiles) {
                item {
                    Column {
                        SectionHeader("Recent Payments")
                        if (recentPayments.isEmpty()) {
                            Text(
                                "No payments recorded yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                recentPayments.forEach { payment ->
                                    val tenantName = tenants.find { it.id == payment.tenantId }?.fullName ?: "Tenant"
                                    InfoRow(
                                        title = tenantName,
                                        subtitle = DateUtils.formatDate(payment.paymentDate),
                                        trailing = CurrencyFormatter.format(payment.amount),
                                        trailingColor = semantic.success
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (DashboardTile.UPCOMING_RENT in enabledTiles) {
                item {
                    Column {
                        SectionHeader("Upcoming Rent")
                        if (upcomingDue.isEmpty()) {
                            Text(
                                "Nothing due in the next 7 days",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                upcomingDue.forEach { rent ->
                                    val tenantName = tenants.find { it.id == rent.tenantId }?.fullName ?: "Tenant"
                                    InfoRow(
                                        title = tenantName,
                                        subtitle = "Due ${DateUtils.formatDate(rent.dueDate)}",
                                        trailing = CurrencyFormatter.format(rent.remainingAmount),
                                        trailingColor = semantic.warning
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (DashboardTile.LEASE_EXPIRY in enabledTiles) {
                item {
                    Column {
                        SectionHeader("Lease Expiry")
                        if (expiringLeases.isEmpty()) {
                            Text(
                                "No leases expiring in the next 30 days",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                expiringLeases.forEach { lease ->
                                    val tenantName = tenants.find { it.id == lease.tenantId }?.fullName ?: "Tenant"
                                    val propertyName = properties.find { it.id == lease.propertyId }?.name ?: "Property"
                                    InfoRow(
                                        title = tenantName,
                                        subtitle = propertyName,
                                        trailing = DateUtils.formatDate(lease.endDate),
                                        trailingColor = semantic.warning
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.md)) }
        }
    }
}

@Composable
private fun QuickStatsGrid(items: List<QuickStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                rowItems.forEach { stat ->
                    MetricCard(
                        label = stat.label, value = stat.value,
                        icon = stat.icon, accentColor = stat.accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GreetingHeader() {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    Column {
        Text("$greeting \uD83D\uDC4B", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Manage your properties and rent payments easily.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp).clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
private fun RentOverviewRow(label: String, amount: Double, valueColor: Color, showDivider: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(CurrencyFormatter.format(amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
    if (showDivider) HorizontalDivider()
}

@Composable
private fun InfoRow(title: String, subtitle: String, trailing: String, trailingColor: Color) {
    Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.card)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(trailing, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = trailingColor)
        }
    }
}
