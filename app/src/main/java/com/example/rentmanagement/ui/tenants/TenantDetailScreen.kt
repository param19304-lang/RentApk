package com.example.rentmanagement.ui.tenants

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.components.StatusBadge
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.CurrencyFormatter
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDetailScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: TenantDetailViewModel = hiltViewModel()
) {
    val tenant by viewModel.tenant.collectAsState()
    val activeLease by viewModel.activeLease.collectAsState()
    val unitName by viewModel.unitName.collectAsState()
    val propertyName by viewModel.propertyName.collectAsState()
    val rentHistory by viewModel.rentHistory.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tenant Details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                }
            )
        }
    ) { padding ->
        val t = tenant
        if (t == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            modifier = Modifier.padding(padding)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(Spacing.md))
                    Column(Modifier.weight(1f)) {
                        Text(t.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(t.phoneNumber, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${t.phoneNumber}"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call ${t.fullName}", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                SectionHeader("Occupancy")
                Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.lg)) {
                        if (activeLease == null) {
                            Text("No active lease", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            DetailRow("Property", propertyName ?: "-")
                            DetailRow("Unit", unitName ?: "-")
                            DetailRow("Monthly Rent", CurrencyFormatter.format(activeLease!!.monthlyRent))
                            DetailRow("Rent Starts", DateUtils.formatDate(activeLease!!.rentStartDate))
                            DetailRow("Lease Ends", DateUtils.formatDate(activeLease!!.endDate), showDivider = false)
                        }
                    }
                }
            }

            item {
                SectionHeader("Contact Information")
                Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.lg)) {
                        DetailRow("Phone", t.phoneNumber)
                        if (!t.email.isNullOrBlank()) DetailRow("Email", t.email)
                        if (!t.emergencyContact.isNullOrBlank()) DetailRow("Emergency Contact", t.emergencyContact)
                        if (!t.address.isNullOrBlank()) DetailRow("Address", t.address, showDivider = false)
                    }
                }
            }

            item { SectionHeader("Rent History") }

            if (rentHistory.isEmpty()) {
                item {
                    Text(
                        "No rent records yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(rentHistory, key = { it.id }) { rent ->
                    Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rent.billingMonth, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("Due ${DateUtils.formatDate(rent.dueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(CurrencyFormatter.format(rent.totalPayable), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(2.dp))
                                StatusBadge(status = rent.status)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, showDivider: Boolean = true) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    if (showDivider) HorizontalDivider()
}
