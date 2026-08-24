package com.example.rentmanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.rentmanagement.domain.model.DashboardTile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Which dashboard cards/sections the user has chosen to show. No stored value
 * (first launch, or nothing ever toggled) means everything is visible.
 */
@Singleton
class DashboardPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val ENABLED_TILES = stringSetPreferencesKey("enabled_dashboard_tiles")
    }

    val enabledTiles: Flow<Set<DashboardTile>> = dataStore.data.map { prefs ->
        val stored = prefs[Keys.ENABLED_TILES]
        if (stored == null) {
            DashboardTile.values().toSet()
        } else {
            stored.mapNotNull { name -> runCatching { DashboardTile.valueOf(name) }.getOrNull() }.toSet()
        }
    }

    suspend fun setTileEnabled(tile: DashboardTile, enabled: Boolean) {
        dataStore.edit { prefs ->
            val current = (prefs[Keys.ENABLED_TILES] ?: DashboardTile.values().map { it.name }.toSet()).toMutableSet()
            if (enabled) current.add(tile.name) else current.remove(tile.name)
            prefs[Keys.ENABLED_TILES] = current
        }
    }
}
