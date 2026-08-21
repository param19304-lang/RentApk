package com.example.rentmanagement.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
fun ExpensesScreen(
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    onOpenExpense: (Long) -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val properties by viewModel.properties.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) { Icon(Icons.Default.Add, contentDescription = "Add expense") }
        }
    ) { padding ->
        if (expenses.isEmpty()) {
            EmptyState("No expenses yet", "Tap + to record a property expense", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(expenses, key = { it.id }) { expense ->
                    val propertyName = properties.find { it.id == expense.propertyId }?.name ?: "Property #${expense.propertyId}"
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { onOpenExpense(expense.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(CurrencyFormatter.format(expense.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                AssistChip(onClick = {}, label = { Text(expense.category.name) })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(propertyName, style = MaterialTheme.typography.bodyMedium)
                            Text(DateUtils.formatDate(expense.date), style = MaterialTheme.typography.bodySmall)
                            if (!expense.description.isNullOrBlank()) {
                                Text(expense.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
