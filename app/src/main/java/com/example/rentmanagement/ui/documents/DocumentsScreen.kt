package com.example.rentmanagement.ui.documents

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.DocumentCategory
import com.example.rentmanagement.ui.components.EmptyState
import com.example.rentmanagement.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    onBack: () -> Unit,
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState()
    val saveError by viewModel.saveError.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var pickedUri by remember { mutableStateOf<String?>(null) }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickedUri = uri.toString()
            showAddDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documents") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { pickerLauncher.launch(arrayOf("application/pdf", "image/*")) }) {
                Icon(Icons.Default.Add, contentDescription = "Add document")
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            EmptyState("No documents yet", "Tap + to upload a lease agreement, ID, bill, or photo", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(documents, key = { it.id }) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = {
                            runCatching {
                                val uri = android.net.Uri.parse(doc.uri)
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(doc.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                AssistChip(onClick = {}, label = { Text(doc.category.name) })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(DateUtils.formatDate(doc.uploadedAt), style = MaterialTheme.typography.bodySmall)
                            if (!doc.notes.isNullOrBlank()) {
                                Text(doc.notes, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && pickedUri != null) {
        AddDocumentDialog(
            error = saveError,
            onDismiss = { showAddDialog = false; pickedUri = null; viewModel.clearError() },
            onSave = { name, category, notes ->
                viewModel.addDocument(name, pickedUri!!, category, notes) {
                    showAddDialog = false
                    pickedUri = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDocumentDialog(
    error: String?,
    onDismiss: () -> Unit,
    onSave: (String, DocumentCategory, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(DocumentCategory.OTHER) }
    var notes by remember { mutableStateOf("") }
    var categoryMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Document") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(name, { name = it }, label = { Text("Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = categoryMenu, onExpandedChange = { categoryMenu = it }) {
                    OutlinedTextField(
                        value = category.name, onValueChange = {}, readOnly = true, label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenu) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = categoryMenu, onDismissRequest = { categoryMenu = false }) {
                        DocumentCategory.values().forEach {
                            DropdownMenuItem(text = { Text(it.name) }, onClick = { category = it; categoryMenu = false })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name, category, notes.ifBlank { null }) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
