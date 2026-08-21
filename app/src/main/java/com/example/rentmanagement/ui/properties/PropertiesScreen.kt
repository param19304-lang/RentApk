package com.example.rentmanagement.ui.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    onAddProperty: () -> Unit,
    onOpenProperty: (Long) -> Unit,
    viewModel: PropertyViewModel = hiltViewModel()
) {
    val properties by viewModel.properties.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Properties") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProperty) {
                Icon(Icons.Default.Add, contentDescription = "Add property")
            }
        }
    ) { padding ->
        if (properties.isEmpty()) {
            EmptyState(
                title = "No properties yet",
                subtitle = "Tap + to add your first property",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(properties, key = { it.id }) { property ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOpenProperty(property.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(property.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text("${property.type} · ${property.city}, ${property.state}", style = MaterialTheme.typography.bodyMedium)
                            if (!property.address.isNullOrBlank()) {
                                Text(property.address, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
