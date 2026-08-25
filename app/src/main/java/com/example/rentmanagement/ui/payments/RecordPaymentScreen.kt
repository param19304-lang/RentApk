package com.example.rentmanagement.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.PaymentMethod
import com.example.rentmanagement.domain.usecase.RecordPaymentResult
import com.example.rentmanagement.ui.components.AppDatePickerField
import com.example.rentmanagement.ui.components.PrimaryButton
import com.example.rentmanagement.ui.components.SecondaryButton
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.components.dismissKeyboardOnTap
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    rentId: Long,
    onBack: () -> Unit,
    onRecorded: (String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val rent by viewModel.selectedRent.collectAsState()
    val tenantName by viewModel.tenantName.collectAsState()
    val unitName by viewModel.unitName.collectAsState()
    val result by viewModel.result.collectAsState()
    val lastPaymentId by viewModel.lastPaymentId.collectAsState()
    val receiptFile by viewModel.receiptFile.collectAsState()
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var allowAdvance by remember { mutableStateOf(true) }

    LaunchedEffect(rentId) {
        viewModel.loadRent(rentId)
        amount = ""
    }

    LaunchedEffect(rent) {
        if (amount.isBlank() && result !is RecordPaymentResult.Success) {
            rent?.let { amount = it.remainingAmount.coerceAtLeast(0.0).toString() }
        }
    }

    LaunchedEffect(receiptFile) {
        receiptFile?.let {
            ShareUtils.shareFile(context, it, "application/pdf")
            viewModel.clearReceiptFile()
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
        val successResult = result as? RecordPaymentResult.Success
        if (successResult != null) {
            Column(
                Modifier.padding(padding).padding(Spacing.xxl).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(Spacing.md))
                Text("Payment Recorded", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Receipt No. ${successResult.receiptNumber}", style = MaterialTheme.typography.bodyMedium)
                rent?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Remaining balance: ${CurrencyFormatter.format(it.remainingAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(Spacing.xxl))
                PrimaryButton(
                    text = "Share Receipt (PDF)",
                    onClick = { lastPaymentId?.let { viewModel.generateReceiptPdf(rentId, it) } }
                )
                Spacer(Modifier.height(Spacing.md))
                SecondaryButton(text = "Done", onClick = { onRecorded(successResult.receiptNumber) })
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .padding(Spacing.lg)
                .dismissKeyboardOnTap()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            rent?.let { r ->
                Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.lg)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(tenantName ?: "Tenant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                unitName?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            StatusBadge(status = r.status)
                        }
                        Spacer(Modifier.height(Spacing.lg))
                        Text("Amount Due", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            CurrencyFormatter.format(r.remainingAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (r.remainingAmount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        HorizontalDivider()
                        Spacer(Modifier.height(Spacing.sm))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(CurrencyFormatter.format(r.totalPayable), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount Paid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(CurrencyFormatter.format(r.amountPaid), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.lg))
            }

            OutlinedTextField(
                amount, { amount = it }, label = { Text("Payment amount *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.md))
            AppDatePickerField("Payment date *", paymentDate, { paymentDate = it })
            Spacer(Modifier.height(Spacing.lg))

            Text("Payment Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(Spacing.sm))
            Column {
                PaymentMethod.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = method == option, onClick = { method = option })
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = method == option, onClick = { method = option })
                        Spacer(Modifier.width(Spacing.sm))
                        Text(option.name.replace('_', ' '), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(Spacing.md))
            OutlinedTextField(reference, { reference = it }, label = { Text("Transaction / reference number") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.md))
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(Spacing.md))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Allow advance payment",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = allowAdvance, onCheckedChange = { allowAdvance = it })
            }
            Text(
                "Overpayment carries forward as credit on future rent.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (result is RecordPaymentResult.Error) {
                Spacer(Modifier.height(Spacing.sm))
                Text((result as RecordPaymentResult.Error).message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(Spacing.xl))
            PrimaryButton(
                text = "Record Payment",
                onClick = {
                    viewModel.recordPayment(
                        rentId = rentId,
                        amount = amount.toDoubleOrNull() ?: -1.0,
                        paymentDate = paymentDate ?: System.currentTimeMillis(),
                        method = method,
                        referenceNumber = reference.ifBlank { null },
                        notes = notes.ifBlank { null },
                        allowAdvance = allowAdvance,
                        onSuccess = {}
                    )
                }
            )
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}
