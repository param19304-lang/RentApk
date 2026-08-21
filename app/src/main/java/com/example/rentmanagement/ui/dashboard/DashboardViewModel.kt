package com.example.rentmanagement.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.domain.usecase.DashboardStats
import com.example.rentmanagement.domain.usecase.GenerateMonthlyRentUseCase
import com.example.rentmanagement.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val generateMonthlyRentUseCase: GenerateMonthlyRentUseCase
) : ViewModel() {

    val stats: StateFlow<DashboardStats> = getDashboardStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    fun ensureCurrentMonthRentGenerated() {
        viewModelScope.launch {
            generateMonthlyRentUseCase()
        }
    }
}
