package com.example.rentmanagement.ui.tenants
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTenantScreen(
    tenantId: Long?,
    onBack: () -> Unit,
    viewModel: TenantViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var occupants by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    val saveError by viewModel.saveError.collectAsState()

    LaunchedEffect(tenantId) {
        if (tenantId != null && tenantId > 0) {
            viewModel.loadForEdit(tenantId) { existing ->
                existing?.let {
                    fullName = it.fullName; phoneNumber = it.phoneNumber
                    email = it.email.orEmpty(); idType = it.idType.orEmpty()
                    idNumber = it.idNumber.orEmpty(); address = it.address.orEmpty()
                    emergencyContact = it.emergencyContact.orEmpty()
                    occupants = it.occupants.toString(); notes = it.notes.orEmpty()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (tenantId != null && tenantId > 0) "Edit Tenant" else "Add Tenant") },
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
            OutlinedTextField(fullName, { fullName = it }, label = { Text("Full name *") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                phoneNumber, { phoneNumber = it }, label = { Text("Phone number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                email, { email = it }, label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(idType, { idType = it }, label = { Text("ID type") }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(idNumber, { idNumber = it }, label = { Text("ID number") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(emergencyContact, { emergencyContact = it }, label = { Text("Emergency contact") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                occupants, { occupants = it }, label = { Text("Occupants") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
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
                        id = tenantId ?: 0L,
                        fullName = fullName, phoneNumber = phoneNumber,
                        email = email.ifBlank { null }, idType = idType.ifBlank { null },
                        idNumber = idNumber.ifBlank { null }, address = address.ifBlank { null },
                        emergencyContact = emergencyContact.ifBlank { null },
                        occupants = occupants.toIntOrNull() ?: 1,
                        notes = notes.ifBlank { null },
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Tenant") }
        }
    }
}
