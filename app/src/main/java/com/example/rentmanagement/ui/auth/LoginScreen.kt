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
fun LoginScreen(viewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.loginError.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Rent Manager", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("Sign in to continue", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                username, { username = it }, label = { Text("Username") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                password, { password = it }, label = { Text("Password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { viewModel.login(username, password) },
                enabled = !isBusy && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isBusy) "Signing in..." else "Sign In") }
            Spacer(Modifier.height(16.dp))
            Text(
                "Don't have an account? Ask your admin to create one for you.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
