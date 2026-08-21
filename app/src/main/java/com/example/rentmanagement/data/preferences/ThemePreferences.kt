package com.example.rentmanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.rentmanagement.domain.model.AppColorTheme
import com.example.rentmanagement.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val COLOR_THEME = stringPreferencesKey("color_theme")
        val CUSTOM_ACCENT = stringPreferencesKey("custom_accent_hex")
    }

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    val colorTheme: Flow<AppColorTheme> = dataStore.data.map { prefs ->
        prefs[Keys.COLOR_THEME]?.let { runCatching { AppColorTheme.valueOf(it) }.getOrNull() } ?: AppColorTheme.MONO
    }

    val customAccentHex: Flow<String?> = dataStore.data.map { it[Keys.CUSTOM_ACCENT] }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setColorTheme(theme: AppColorTheme) {
        dataStore.edit { it[Keys.COLOR_THEME] = theme.name }
    }

    suspend fun setCustomAccent(hex: String) {
        dataStore.edit { it[Keys.CUSTOM_ACCENT] = hex }
    }
}
