package com.example.rentmanagement.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AdminSetupScreen(viewModel: AuthViewModel) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val remoteError by viewModel.loginError.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Welcome to Rent Manager", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(
                "Create the first admin account to get started",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(fullName, { fullName = it }, label = { Text("Your name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(username, { username = it }, label = { Text("Choose a username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                password, { password = it }, label = { Text("Password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                confirmPassword, { confirmPassword = it }, label = { Text("Confirm password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            val shownError = localError ?: remoteError
            if (shownError != null) {
                Spacer(Modifier.height(8.dp))
                Text(shownError, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        localError = "Passwords do not match"
                    } else {
                        localError = null
                        viewModel.createFirstAdmin(username, password, fullName)
                    }
                },
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isBusy) "Creating..." else "Create Admin Account") }
        }
    }
}
