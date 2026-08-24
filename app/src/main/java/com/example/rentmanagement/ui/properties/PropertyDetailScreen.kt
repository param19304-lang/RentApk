package com.example.rentmanagement.ui.properties

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.UnitStatus
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.ui.components.MetricCard
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.theme.LocalSemanticColors
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.ui.units.UnitViewModel
import com.example.rentmanagement.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    propertyId: Long,
    onBack: () -> Unit,
    onAddUnit: () -> Unit,
    onEditUnit: (Long) -> Unit,
    unitViewModel: UnitViewModel = hiltViewModel()
) {
    val units by unitViewModel.units.collectAsState()
    val property by unitViewModel.property.collectAsState()
    val tenants by unitViewModel.tenants.collectAsState()
    val semantic = LocalSemanticColors.current

    val occupied = units.count { it.status == UnitStatus.OCCUPIED }
    val vacant = units.count { it.status == UnitStatus.VACANT }
    val totalMonthlyRent = units.sumOf { it.monthlyRent }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(property?.name ?: "Property") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUnit) { Icon(Icons.Default.Add, contentDescription = "Add unit") }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            modifier = Modifier.padding(padding)
        ) {
            item {
                property?.let { p ->
                    val location = listOf(p.city, p.state).filter { it.isNotBlank() }.joinToString(", ")
                    if (location.isNotBlank() || p.address.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                listOf(p.address, location).filter { it.isNotBlank() }.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(Spacing.md))
                    }
                }

                SectionHeader("Overview")
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        MetricCard("Total Units", units.size.toString(), modifier = Modifier.weight(1f))
                        MetricCard("Occupied", occupied.toString(), accentColor = semantic.success, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        MetricCard("Vacant", vacant.toString(), modifier = Modifier.weight(1f))
                        MetricCard("Monthly Rent", CurrencyFormatter.format(totalMonthlyRent), modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(Spacing.sm))
                SectionHeader("Units")
            }

            if (units.isEmpty()) {
                item {
                    EmptyState(
                        "No units yet",
                        "Tap + to add a unit to this property",
                        Modifier.fillMaxWidth().padding(vertical = Spacing.xxl),
                        icon = Icons.Default.Apartment
                    )
                }
            } else {
                items(units, key = { it.id }) { unit ->
                    val tenantName = unit.currentTenantId?.let { id -> tenants.find { it.id == id }?.fullName }
                    Card(
                        shape = RoundedCornerShape(Radius.card),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEditUnit(unit.id) }
                    ) {
                        Column(Modifier.padding(Spacing.lg)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(unit.unitName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                StatusBadge(status = unit.status)
                            }
                            if (!unit.floor.isNullOrBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(unit.floor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                "${CurrencyFormatter.format(unit.monthlyRent)}/month",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (tenantName != null) {
                                Spacer(Modifier.height(Spacing.sm))
                                Text("Tenant", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(tenantName, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
