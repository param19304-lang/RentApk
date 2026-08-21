package com.example.rentmanagement.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.domain.model.UserRole
import com.example.rentmanagement.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    currentUserId: Long,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val saveError by viewModel.saveError.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.clearError(); showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add user")
            }
        }
    ) { padding ->
        if (users.isEmpty()) {
            EmptyState("No users yet", "Tap + to add an admin or user account", Modifier.padding(padding))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding)) {
                items(users, key = { it.id }) { user ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(user.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("@${user.username} · ${user.role.name}", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = user.isActive,
                                enabled = user.id != currentUserId,
                                onCheckedChange = { viewModel.setActive(user.id, it) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddUserDialog(
            error = saveError,
            onDismiss = { showAddDialog = false },
            onSave = { username, password, fullName, role ->
                viewModel.addUser(username, password, fullName, role) { showAddDialog = false }
            }
        )
    }
}

@Composable
private fun AddUserDialog(
    error: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, UserRole) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.USER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add User") },
        text = {
            Column {
                OutlinedTextField(fullName, { fullName = it }, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(username, { username = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    password, { password = it }, label = { Text("Temporary password") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    FilterChip(selected = role == UserRole.ADMIN, onClick = { role = UserRole.ADMIN }, label = { Text("Admin") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = role == UserRole.USER, onClick = { role = UserRole.USER }, label = { Text("User") })
                }
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(username, password, fullName, role) }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
