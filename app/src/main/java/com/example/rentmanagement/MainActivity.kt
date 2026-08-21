package com.example.rentmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.auth.AdminSetupScreen
import com.example.rentmanagement.ui.auth.AuthViewModel
import com.example.rentmanagement.ui.auth.LoginScreen
import com.example.rentmanagement.ui.auth.SessionState
import com.example.rentmanagement.ui.navigation.AppScaffold
import com.example.rentmanagement.ui.settings.SettingsViewModel
import com.example.rentmanagement.ui.theme.RentManagementTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val colorTheme by settingsViewModel.colorTheme.collectAsState()
            val customAccentHex by settingsViewModel.customAccentHex.collectAsState()
            val customAccent = customAccentHex?.let {
                runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it)) }.getOrNull()
            }

            val authViewModel: AuthViewModel = hiltViewModel()
            val sessionState by authViewModel.sessionState.collectAsState()

            RentManagementTheme(themeMode = themeMode, colorTheme = colorTheme, customAccent = customAccent) {
                when (val session = sessionState) {
                    is SessionState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SessionState.NeedsAdminSetup -> AdminSetupScreen(viewModel = authViewModel)
                    is SessionState.LoggedOut -> LoginScreen(viewModel = authViewModel)
                    is SessionState.LoggedIn -> AppScaffold(
                        currentUser = session.user,
                        onLogout = { authViewModel.logout() }
                    )
                }
            }
        }
    }
}
