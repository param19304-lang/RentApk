package com.example.rentmanagement.ui.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.UnitStatus
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    onMenuClick: () -> Unit,
    onAddProperty: () -> Unit,
    onOpenProperty: (Long) -> Unit,
    viewModel: PropertyViewModel = hiltViewModel()
) {
    val properties by viewModel.properties.collectAsState()
    val units by viewModel.units.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Properties") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProperty) {
                Icon(Icons.Default.Add, contentDescription = "Add property")
            }
        }
    ) { padding ->
        if (properties.isEmpty()) {
            EmptyState(
                title = "No Properties Yet",
                subtitle = "Add your first property to start managing rent.",
                modifier = Modifier.padding(padding),
                icon = Icons.Default.Apartment
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding)
            ) {
                items(properties, key = { it.id }) { property ->
                    val propertyUnits = units.filter { it.propertyId == property.id }
                    val occupied = propertyUnits.count { it.status == UnitStatus.OCCUPIED }
                    val fraction = if (propertyUnits.isNotEmpty()) occupied.toFloat() / propertyUnits.size else 0f

                    Card(
                        shape = RoundedCornerShape(Radius.card),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProperty(property.id) }
                    ) {
                        Column(Modifier.padding(Spacing.lg)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    property.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(Spacing.sm))
                            val location = listOf(property.city, property.state).filter { it.isNotBlank() }.joinToString(", ")
                            if (location.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(Modifier.height(Spacing.sm))
                            }

                            Text(
                                "${propertyUnits.size} Units  •  $occupied Occupied",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(Spacing.sm))

                            if (propertyUnits.isNotEmpty()) {
                                LinearProgressIndicator(
                                    progress = fraction,
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${(fraction * 100).toInt()}% occupied",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
