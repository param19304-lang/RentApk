package com.example.rentmanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.rentmanagement.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val LANDLORD_NAME = stringPreferencesKey("landlord_name")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val DEFAULT_DUE_DAY = intPreferencesKey("default_due_day")
        val DEFAULT_LATE_FEE = doublePreferencesKey("default_late_fee")
        val REMINDER_DAYS_BEFORE = intPreferencesKey("reminder_days_before")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val APP_PIN_ENABLED = booleanPreferencesKey("app_pin_enabled")
    }

    val landlordName: Flow<String> = dataStore.data.map { it[Keys.LANDLORD_NAME] ?: "" }
    val currencySymbol: Flow<String> = dataStore.data.map { it[Keys.CURRENCY_SYMBOL] ?: Constants.DEFAULT_CURRENCY_SYMBOL }
    val defaultDueDay: Flow<Int> = dataStore.data.map { it[Keys.DEFAULT_DUE_DAY] ?: Constants.DEFAULT_RENT_DUE_DAY }
    val defaultLateFee: Flow<Double> = dataStore.data.map { it[Keys.DEFAULT_LATE_FEE] ?: Constants.DEFAULT_LATE_FEE }
    val reminderDaysBefore: Flow<Int> = dataStore.data.map { it[Keys.REMINDER_DAYS_BEFORE] ?: 3 }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    val appPinEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.APP_PIN_ENABLED] ?: false }

    suspend fun setLandlordName(name: String) { dataStore.edit { it[Keys.LANDLORD_NAME] = name } }
    suspend fun setCurrencySymbol(symbol: String) { dataStore.edit { it[Keys.CURRENCY_SYMBOL] = symbol } }
    suspend fun setDefaultDueDay(day: Int) { dataStore.edit { it[Keys.DEFAULT_DUE_DAY] = day } }
    suspend fun setDefaultLateFee(fee: Double) { dataStore.edit { it[Keys.DEFAULT_LATE_FEE] = fee } }
    suspend fun setReminderDaysBefore(days: Int) { dataStore.edit { it[Keys.REMINDER_DAYS_BEFORE] = days } }
    suspend fun setNotificationsEnabled(enabled: Boolean) { dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled } }
    suspend fun setAppPinEnabled(enabled: Boolean) { dataStore.edit { it[Keys.APP_PIN_ENABLED] = enabled } }
}
