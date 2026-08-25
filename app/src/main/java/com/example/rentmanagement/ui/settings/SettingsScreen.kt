package com.example.rentmanagement.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.AppColorTheme
import com.example.rentmanagement.domain.model.ThemeMode
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.theme.AppThemePalettes
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    currentUserName: String,
    currentUserRole: String,
    isAdmin: Boolean,
    onManageUsers: () -> Unit,
    onCustomizeDashboard: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val colorTheme by viewModel.colorTheme.collectAsState()
    val customAccentHex by viewModel.customAccentHex.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val landlordName by viewModel.landlordName.collectAsState()
    val defaultDueDay by viewModel.defaultDueDay.collectAsState()
    val defaultLateFee by viewModel.defaultLateFee.collectAsState()
    val context = LocalContext.current

    var landlordNameField by remember(landlordName) { mutableStateOf(landlordName) }
    var currencyField by remember(currencySymbol) { mutableStateOf(currencySymbol) }
    var dueDayField by remember(defaultDueDay) { mutableStateOf(defaultDueDay.toString()) }
    var lateFeeField by remember(defaultLateFee) { mutableStateOf(defaultLateFee.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .dismissKeyboardOnTap()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            SectionHeader("Account")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(currentUserName, style = MaterialTheme.typography.titleMedium)
                    Text(currentUserRole, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (isAdmin) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onManageUsers, modifier = Modifier.fillMaxWidth()) { Text("Manage Users") }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Log Out") }

            Spacer(Modifier.height(24.dp))
            SectionHeader("Dashboard")
            OutlinedButton(onClick = onCustomizeDashboard, modifier = Modifier.fillMaxWidth()) { Text("Customize Dashboard") }

            Spacer(Modifier.height(24.dp))
            SectionHeader("Appearance")
            SingleChoiceSegment(
                options = listOf(ThemeMode.LIGHT to "Light", ThemeMode.DARK to "Dark", ThemeMode.SYSTEM to "System"),
                selected = themeMode,
                onSelect = { viewModel.setThemeMode(it) }
            )

            Spacer(Modifier.height(24.dp))
            SectionHeader("App theme")
            ThemeGrid(
                selected = colorTheme,
                isDark = themeMode == ThemeMode.DARK,
                customAccentHex = customAccentHex,
                onSelect = { viewModel.setColorTheme(it) }
            )

            if (colorTheme == AppColorTheme.CUSTOM) {
                Spacer(Modifier.height(16.dp))
                Text("Choose accent color", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                AccentPicker(
                    selectedHex = customAccentHex,
                    onSelect = { hex -> viewModel.setCustomAccent(hex) }
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionHeader("Landlord profile")
            OutlinedTextField(
                landlordNameField, { landlordNameField = it },
                label = { Text("Landlord / manager name") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.setLandlordName(landlordNameField) {
                            android.widget.Toast.makeText(context, "Saved", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
            SectionHeader("Rent defaults")
            OutlinedTextField(
                currencyField, { currencyField = it },
                label = { Text("Currency symbol") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.setCurrencySymbol(currencyField) {
                            android.widget.Toast.makeText(context, "Saved", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(
                    dueDayField, { dueDayField = it }, label = { Text("Default due day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    lateFeeField, { lateFeeField = it }, label = { Text("Default late fee") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val day = dueDayField.toIntOrNull()
                    val fee = lateFeeField.toDoubleOrNull()
                    if (day != null && fee != null) {
                        viewModel.saveRentDefaults(day, fee) {
                            android.widget.Toast.makeText(context, "Rent defaults saved", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save rent defaults") }
        }
    }
}

@Composable
private fun SingleChoiceSegment(
    options: List<Pair<ThemeMode, String>>,
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label) ->
            val isSelected = mode == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelect(mode) },
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        label,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeGrid(
    selected: AppColorTheme,
    isDark: Boolean,
    customAccentHex: String?,
    onSelect: (AppColorTheme) -> Unit
) {
    val customAccent = customAccentHex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(AppColorTheme.values().toList()) { themeOption ->
            val palette = AppThemePalettes.paletteFor(themeOption, isDark, customAccent)
            val isSelected = themeOption == selected
            Column(
                modifier = Modifier
                    .width(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else palette.border,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(themeOption) }
                    .background(palette.background)
                    .padding(10.dp)
            ) {
                // Mini preview: a "surface card" with a "primary button"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(palette.primary)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    themeOption.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.text
                )
            }
        }
    }
}

@Composable
private fun AccentPicker(selectedHex: String?, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(AppThemePalettes.customAccentOptions.toList()) { (label, color) ->
            val hex = String.format("#%06X", 0xFFFFFF and color.toArgb())
            val isSelected = selectedHex.equals(hex, ignoreCase = true)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelect(hex) }
                )
                Spacer(Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
