package com.example.rentmanagement.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.preferences.DashboardPreferences
import com.example.rentmanagement.domain.model.DashboardTile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardCustomizationViewModel @Inject constructor(
    private val preferences: DashboardPreferences
) : ViewModel() {

    val enabledTiles: StateFlow<Set<DashboardTile>> = preferences.enabledTiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardTile.values().toSet())

    fun setTileEnabled(tile: DashboardTile, enabled: Boolean) {
        viewModelScope.launch { withContext(NonCancellable) { preferences.setTileEnabled(tile, enabled) } }
    }
}
