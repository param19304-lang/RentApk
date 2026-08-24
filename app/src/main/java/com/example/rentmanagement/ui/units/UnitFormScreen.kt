package com.example.rentmanagement.ui.units

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.UnitStatus
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitFormScreen(
    propertyId: Long,
    unitId: Long?,
    onBack: () -> Unit,
    viewModel: UnitViewModel = hiltViewModel()
) {
    var unitName by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var securityDeposit by remember { mutableStateOf("") }
    var maintenanceCharge by remember { mutableStateOf("") }
    var electricityCharge by remember { mutableStateOf("") }
    var waterCharge by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(UnitStatus.VACANT) }
    var notes by remember { mutableStateOf("") }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    val saveError by viewModel.saveError.collectAsState()

    LaunchedEffect(unitId) {
        if (unitId != null && unitId > 0) {
            viewModel.loadForEdit(unitId) { existing ->
                existing?.let {
                    unitName = it.unitName; floor = it.floor.orEmpty()
                    monthlyRent = it.monthlyRent.toString()
                    securityDeposit = it.securityDeposit?.toString().orEmpty()
                    maintenanceCharge = it.maintenanceCharge?.toString().orEmpty()
                    electricityCharge = it.electricityCharge?.toString().orEmpty()
                    waterCharge = it.waterCharge?.toString().orEmpty()
                    status = it.status
                    notes = it.notes.orEmpty()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (unitId != null && unitId > 0) "Edit Unit" else "Add Unit") },
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
            OutlinedTextField(unitName, { unitName = it }, label = { Text("Unit number/name *") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(floor, { floor = it }, label = { Text("Floor") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                monthlyRent, { monthlyRent = it }, label = { Text("Monthly rent *") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                securityDeposit, { securityDeposit = it }, label = { Text("Security deposit (optional)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                maintenanceCharge, { maintenanceCharge = it }, label = { Text("Maintenance charge (optional)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                electricityCharge, { electricityCharge = it }, label = { Text("Electricity charge") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                waterCharge, { waterCharge = it }, label = { Text("Water charge (optional)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = statusMenuExpanded, onExpandedChange = { statusMenuExpanded = it }) {
                OutlinedTextField(
                    value = status.name, onValueChange = {}, readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = statusMenuExpanded, onDismissRequest = { statusMenuExpanded = false }) {
                    UnitStatus.values().forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { status = it; statusMenuExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            if (saveError != null) {
                Spacer(Modifier.height(8.dp))
                Text(saveError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.save(
                        id = unitId ?: 0L,
                        propertyId = propertyId,
                        unitName = unitName,
                        floor = floor.ifBlank { null },
                        monthlyRent = monthlyRent.toDoubleOrNull() ?: -1.0,
                        securityDeposit = securityDeposit.toDoubleOrNull(),
                        maintenanceCharge = maintenanceCharge.toDoubleOrNull(),
                        electricityCharge = electricityCharge.toDoubleOrNull(),
                        waterCharge = waterCharge.toDoubleOrNull(),
                        status = status,
                        notes = notes.ifBlank { null },
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Unit") }
        }
    }
}
