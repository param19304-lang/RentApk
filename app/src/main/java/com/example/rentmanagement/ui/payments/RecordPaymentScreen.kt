package com.example.rentmanagement.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.PaymentMethod
import com.example.rentmanagement.domain.usecase.RecordPaymentResult
import com.example.rentmanagement.ui.components.AppDatePickerField
import com.example.rentmanagement.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    rentId: Long,
    onBack: () -> Unit,
    onRecorded: (String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val rent by viewModel.selectedRent.collectAsState()
    val result by viewModel.result.collectAsState()

    var amount by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var allowAdvance by remember { mutableStateOf(true) }
    var methodMenu by remember { mutableStateOf(false) }

    LaunchedEffect(rentId) {
        viewModel.loadRent(rentId)
        amount = ""
    }

    LaunchedEffect(rent) {
        if (amount.isBlank()) {
            rent?.let { amount = it.remainingAmount.coerceAtLeast(0.0).toString() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Payment") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            rent?.let {
                Text("Total payable: ${CurrencyFormatter.format(it.totalPayable)}", style = MaterialTheme.typography.bodyMedium)
                Text("Already paid: ${CurrencyFormatter.format(it.amountPaid)}", style = MaterialTheme.typography.bodyMedium)
                Text("Remaining: ${CurrencyFormatter.format(it.remainingAmount)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                amount, { amount = it }, label = { Text("Payment amount *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            AppDatePickerField("Payment date *", paymentDate, { paymentDate = it })
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = methodMenu, onExpandedChange = { methodMenu = it }) {
                OutlinedTextField(
                    value = method.name, onValueChange = {}, readOnly = true,
                    label = { Text("Payment method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = methodMenu, onDismissRequest = { methodMenu = false }) {
                    PaymentMethod.values().forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { method = it; methodMenu = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(reference, { reference = it }, label = { Text("Transaction / reference number") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = allowAdvance, onCheckedChange = { allowAdvance = it })
                Text("Allow advance payment (overpayment carries forward as credit)")
            }

            if (result is RecordPaymentResult.Error) {
                Spacer(Modifier.height(8.dp))
                Text((result as RecordPaymentResult.Error).message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.recordPayment(
                        rentId = rentId,
                        amount = amount.toDoubleOrNull() ?: -1.0,
                        paymentDate = paymentDate ?: System.currentTimeMillis(),
                        method = method,
                        referenceNumber = reference.ifBlank { null },
                        notes = notes.ifBlank { null },
                        allowAdvance = allowAdvance,
                        onSuccess = onRecorded
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Record Payment") }
        }
    }
}
