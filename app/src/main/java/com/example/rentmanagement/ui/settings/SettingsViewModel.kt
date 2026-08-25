package com.example.rentmanagement.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.preferences.AppPreferences
import com.example.rentmanagement.data.preferences.ThemePreferences
import com.example.rentmanagement.domain.model.AppColorTheme
import com.example.rentmanagement.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val appPreferences: AppPreferences
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val colorTheme: StateFlow<AppColorTheme> = themePreferences.colorTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppColorTheme.MONO)

    val customAccentHex: StateFlow<String?> = themePreferences.customAccentHex
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currencySymbol: StateFlow<String> = appPreferences.currencySymbol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "\u20B9")

    val landlordName: StateFlow<String> = appPreferences.landlordName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val defaultDueDay: StateFlow<Int> = appPreferences.defaultDueDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val defaultLateFee: StateFlow<Double> = appPreferences.defaultLateFee
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // NonCancellable: these are quick, deliberate user saves triggered from a
    // screen the user commonly leaves right afterward (tap Save, tap Back).
    // Settings screen's ViewModel is scoped to its nav back-stack entry, so
    // navigating away cancels viewModelScope — without NonCancellable, a save
    // that hadn't finished writing to DataStore yet would be silently
    // dropped. This was the actual cause of "landlord details not saving".

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { withContext(NonCancellable) { themePreferences.setThemeMode(mode) } }
    }

    fun setColorTheme(theme: AppColorTheme) {
        viewModelScope.launch { withContext(NonCancellable) { themePreferences.setColorTheme(theme) } }
    }

    fun setCustomAccent(hex: String) {
        viewModelScope.launch { withContext(NonCancellable) { themePreferences.setCustomAccent(hex) } }
    }

    fun setCurrencySymbol(symbol: String, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(NonCancellable) { appPreferences.setCurrencySymbol(symbol) }
            onSaved()
        }
    }

    fun setLandlordName(name: String, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(NonCancellable) { appPreferences.setLandlordName(name) }
            onSaved()
        }
    }

    fun saveRentDefaults(dueDay: Int, lateFee: Double, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                appPreferences.setDefaultDueDay(dueDay)
                appPreferences.setDefaultLateFee(lateFee)
            }
            onSaved()
        }
    }
}
