package com.example.rentmanagement.ui.leases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.AppDatePickerField
import com.example.rentmanagement.utils.Constants
import com.example.rentmanagement.utils.DateUtils
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeaseScreen(
    onBack: () -> Unit,
    viewModel: LeaseViewModel = hiltViewModel()
) {
    val properties by viewModel.properties.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val units by viewModel.unitsForProperty.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var selectedPropertyId by remember { mutableStateOf<Long?>(null) }
    var selectedUnitId by remember { mutableStateOf<Long?>(null) }
    var selectedTenantId by remember { mutableStateOf<Long?>(null) }
    var startDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(DateUtils.addMonths(System.currentTimeMillis(), 11)) }
    var rentStartDate by remember { mutableStateOf<Long?>(null) }
    var monthlyRent by remember { mutableStateOf("") }
    var securityDeposit by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf(Constants.DEFAULT_RENT_DUE_DAY.toString()) }
    var gracePeriod by remember { mutableStateOf("0") }
    var lateFee by remember { mutableStateOf("0") }
    var noticePeriod by remember { mutableStateOf("30") }
    var escalation by remember { mutableStateOf("0") }

    var propertyMenu by remember { mutableStateOf(false) }
    var unitMenu by remember { mutableStateOf(false) }
    var tenantMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Lease") },
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

            ExposedDropdownMenuBox(expanded = propertyMenu, onExpandedChange = { propertyMenu = it }) {
                OutlinedTextField(
                    value = properties.find { it.id == selectedPropertyId }?.name ?: "Select property",
                    onValueChange = {}, readOnly = true, label = { Text("Property *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = propertyMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = propertyMenu, onDismissRequest = { propertyMenu = false }) {
                    properties.forEach { p ->
                        DropdownMenuItem(text = { Text(p.name) }, onClick = {
                            selectedPropertyId = p.id; selectedUnitId = null
                            viewModel.loadUnitsForProperty(p.id)
                            propertyMenu = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = unitMenu, onExpandedChange = { unitMenu = it }) {
                OutlinedTextField(
                    value = units.find { it.id == selectedUnitId }?.unitName ?: "Select unit",
                    onValueChange = {}, readOnly = true, label = { Text("Unit *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = unitMenu, onDismissRequest = { unitMenu = false }) {
                    units.forEach { u ->
                        DropdownMenuItem(text = { Text(u.unitName) }, onClick = {
                            selectedUnitId = u.id
                            if (monthlyRent.isBlank()) monthlyRent = u.monthlyRent.toString()
                            unitMenu = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = tenantMenu, onExpandedChange = { tenantMenu = it }) {
                OutlinedTextField(
                    value = tenants.find { it.id == selectedTenantId }?.fullName ?: "Select tenant",
                    onValueChange = {}, readOnly = true, label = { Text("Tenant *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tenantMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = tenantMenu, onDismissRequest = { tenantMenu = false }) {
                    tenants.forEach { t ->
                        DropdownMenuItem(text = { Text(t.fullName) }, onClick = { selectedTenantId = t.id; tenantMenu = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            AppDatePickerField("Lease start date *", startDate, { startDate = it })
            Spacer(Modifier.height(12.dp))
            AppDatePickerField("Lease end date *", endDate, { endDate = it })
            Spacer(Modifier.height(12.dp))
            AppDatePickerField("Rent start date *", rentStartDate ?: startDate, { rentStartDate = it })
            Text(
                "Defaults to the lease start date. Change this if rent begins later (e.g. a free first month).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                monthlyRent, { monthlyRent = it }, label = { Text("Monthly rent *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                securityDeposit, { securityDeposit = it }, label = { Text("Security deposit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(
                    dueDay, { dueDay = it }, label = { Text("Rent due day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    gracePeriod, { gracePeriod = it }, label = { Text("Grace period (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(
                    lateFee, { lateFee = it }, label = { Text("Late fee") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    noticePeriod, { noticePeriod = it }, label = { Text("Notice period (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                escalation, { escalation = it }, label = { Text("Rent escalation %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()
            )

            if (saveError != null) {
                Spacer(Modifier.height(8.dp))
                Text(saveError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.createLease(
                        propertyId = selectedPropertyId ?: 0L,
                        unitId = selectedUnitId ?: 0L,
                        tenantId = selectedTenantId ?: 0L,
                        startDate = startDate ?: System.currentTimeMillis(),
                        endDate = endDate ?: System.currentTimeMillis(),
                        rentStartDate = rentStartDate ?: startDate ?: System.currentTimeMillis(),
                        monthlyRent = monthlyRent.toDoubleOrNull() ?: -1.0,
                        securityDeposit = securityDeposit.toDoubleOrNull() ?: 0.0,
                        rentDueDay = dueDay.toIntOrNull() ?: Constants.DEFAULT_RENT_DUE_DAY,
                        gracePeriodDays = gracePeriod.toIntOrNull() ?: 0,
                        lateFee = lateFee.toDoubleOrNull() ?: 0.0,
                        noticePeriodDays = noticePeriod.toIntOrNull() ?: 30,
                        rentEscalationPercent = escalation.toDoubleOrNull() ?: 0.0,
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Create Lease") }
        }
    }
}
