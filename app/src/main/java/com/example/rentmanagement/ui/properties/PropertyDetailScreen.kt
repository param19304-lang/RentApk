package com.example.rentmanagement.ui.properties

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Units") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUnit) { Icon(Icons.Default.Add, contentDescription = "Add unit") }
        }
    ) { padding ->
        if (units.isEmpty()) {
            EmptyState("No units yet", "Tap + to add a unit to this property", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(units, key = { it.id }) { unit ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { onEditUnit(unit.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(unit.unitName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                AssistChip(onClick = {}, label = { Text(unit.status.name) })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Rent: ${CurrencyFormatter.format(unit.monthlyRent)}/mo", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
