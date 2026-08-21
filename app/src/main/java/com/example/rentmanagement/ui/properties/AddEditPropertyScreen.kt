package com.example.rentmanagement.ui.properties

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.PropertyType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPropertyScreen(
    propertyId: Long?,
    onBack: () -> Unit,
    viewModel: PropertyViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PropertyType.APARTMENT) }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    val saveError by viewModel.saveError.collectAsState()

    LaunchedEffect(propertyId) {
        if (propertyId != null && propertyId > 0) {
            viewModel.loadForEdit(propertyId) { existing ->
                existing?.let {
                    name = it.name; type = it.type; address = it.address; city = it.city
                    state = it.state; pinCode = it.pinCode
                    ownerName = it.ownerName.orEmpty(); contactNumber = it.contactNumber.orEmpty()
                    notes = it.notes.orEmpty()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (propertyId != null && propertyId > 0) "Edit Property" else "Add Property") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Property name *") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = typeMenuExpanded, onExpandedChange = { typeMenuExpanded = it }) {
                OutlinedTextField(
                    value = type.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Property type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                    PropertyType.values().forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { type = it; typeMenuExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(state, { state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(pinCode, { pinCode = it }, label = { Text("PIN code") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(ownerName, { ownerName = it }, label = { Text("Owner name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(contactNumber, { contactNumber = it }, label = { Text("Contact number") }, modifier = Modifier.fillMaxWidth())
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
                        id = propertyId ?: 0L,
                        name = name, type = type, address = address, city = city, state = state,
                        pinCode = pinCode, ownerName = ownerName.ifBlank { null },
                        contactNumber = contactNumber.ifBlank { null }, notes = notes.ifBlank { null },
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Property") }
        }
    }
}
