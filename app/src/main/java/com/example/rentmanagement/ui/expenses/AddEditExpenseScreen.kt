package com.example.rentmanagement.ui.expenses

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
import com.example.rentmanagement.domain.model.ExpenseCategory
import com.example.rentmanagement.ui.components.AppDatePickerField
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expenseId: Long?,
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val properties by viewModel.properties.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var selectedPropertyId by remember { mutableStateOf<Long?>(null) }
    var category by remember { mutableStateOf(ExpenseCategory.MAINTENANCE) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var description by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var propertyMenu by remember { mutableStateOf(false) }
    var categoryMenu by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(expenseId, expenses) {
        if (!initialized && expenseId != null && expenseId > 0) {
            expenses.find { it.id == expenseId }?.let {
                selectedPropertyId = it.propertyId
                category = it.category
                amount = it.amount.toString()
                date = it.date
                description = it.description.orEmpty()
                vendor = it.vendor.orEmpty()
                notes = it.notes.orEmpty()
                initialized = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId != null && expenseId > 0) "Edit Expense" else "Add Expense") },
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
                        DropdownMenuItem(text = { Text(p.name) }, onClick = { selectedPropertyId = p.id; propertyMenu = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = categoryMenu, onExpandedChange = { categoryMenu = it }) {
                OutlinedTextField(
                    value = category.name, onValueChange = {}, readOnly = true, label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = categoryMenu, onDismissRequest = { categoryMenu = false }) {
                    ExpenseCategory.values().forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { category = it; categoryMenu = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                amount, { amount = it }, label = { Text("Amount *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            AppDatePickerField("Date *", date, { date = it })
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(vendor, { vendor = it }, label = { Text("Vendor") }, modifier = Modifier.fillMaxWidth())
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
                        id = expenseId ?: 0L,
                        propertyId = selectedPropertyId ?: 0L,
                        category = category,
                        amount = amount.toDoubleOrNull() ?: -1.0,
                        date = date ?: System.currentTimeMillis(),
                        description = description.ifBlank { null },
                        vendor = vendor.ifBlank { null },
                        notes = notes.ifBlank { null },
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Expense") }
        }
    }
}
