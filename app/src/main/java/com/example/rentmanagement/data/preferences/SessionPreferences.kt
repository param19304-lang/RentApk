package com.example.rentmanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds only the logged-in user's id (no credentials) so a session survives
 * app restarts. The current user record is always re-fetched from Room, so a
 * deactivated account is signed out on next read.
 */
@Singleton
class SessionPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val LOGGED_IN_USER_ID = longPreferencesKey("logged_in_user_id")
    }

    val loggedInUserId: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[Keys.LOGGED_IN_USER_ID]?.takeIf { it > 0 }
    }

    suspend fun setLoggedInUserId(id: Long?) {
        dataStore.edit { prefs ->
            if (id == null) prefs.remove(Keys.LOGGED_IN_USER_ID) else prefs[Keys.LOGGED_IN_USER_ID] = id
        }
    }
}
