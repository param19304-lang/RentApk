package com.example.rentmanagement.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(viewModel: PaymentViewModel = hiltViewModel()) {
    val payments by viewModel.payments.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Payments") }) }) { padding ->
        if (payments.isEmpty()) {
            EmptyState(
                "No payments recorded yet",
                "Record a payment from the Rent tab or Dashboard quick actions",
                Modifier.padding(padding)
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(payments, key = { it.id }) { payment ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(CurrencyFormatter.format(payment.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                AssistChip(onClick = {}, label = { Text(payment.paymentMethod.name) })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(DateUtils.formatDate(payment.paymentDate), style = MaterialTheme.typography.bodySmall)
                            if (!payment.referenceNumber.isNullOrBlank()) {
                                Text("Ref: ${payment.referenceNumber}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (!payment.receiptNumber.isNullOrBlank()) {
                                Text("Receipt: ${payment.receiptNumber}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
