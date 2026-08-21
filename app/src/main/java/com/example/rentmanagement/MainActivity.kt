package com.example.rentmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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

            RentManagementTheme(themeMode = themeMode, colorTheme = colorTheme, customAccent = customAccent) {
                AppScaffold()
            }
        }
    }
}
