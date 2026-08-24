package com.example.rentmanagement.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.DashboardTile
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCustomizationScreen(
    onBack: () -> Unit,
    viewModel: DashboardCustomizationViewModel = hiltViewModel()
) {
    val enabledTiles by viewModel.enabledTiles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize Dashboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(Spacing.lg), modifier = Modifier.padding(padding)) {
            item {
                SectionHeader("Dashboard Cards")
                Text(
                    "Choose which cards appear on your dashboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.md))
            }
            items(DashboardTile.values().toList()) { tile ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(tile.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = tile in enabledTiles,
                        onCheckedChange = { checked -> viewModel.setTileEnabled(tile, checked) }
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
