package com.example.rentmanagement.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.preferences.AppPreferences
import com.example.rentmanagement.data.preferences.ThemePreferences
import com.example.rentmanagement.domain.model.AppColorTheme
import com.example.rentmanagement.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { themePreferences.setThemeMode(mode) }
    fun setColorTheme(theme: AppColorTheme) = viewModelScope.launch { themePreferences.setColorTheme(theme) }
    fun setCustomAccent(hex: String) = viewModelScope.launch { themePreferences.setCustomAccent(hex) }
    fun setCurrencySymbol(symbol: String) = viewModelScope.launch { appPreferences.setCurrencySymbol(symbol) }
    fun setLandlordName(name: String) = viewModelScope.launch { appPreferences.setLandlordName(name) }
    fun setDefaultDueDay(day: Int) = viewModelScope.launch { appPreferences.setDefaultDueDay(day) }
    fun setDefaultLateFee(fee: Double) = viewModelScope.launch { appPreferences.setDefaultLateFee(fee) }
}
