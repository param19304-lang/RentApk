package com.example.rentmanagement.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.UserAccountEntity
import com.example.rentmanagement.data.preferences.SessionPreferences
import com.example.rentmanagement.data.repository.AuthResult
import com.example.rentmanagement.data.repository.UserAccountRepository
import com.example.rentmanagement.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionState {
    object Loading : SessionState()
    object NeedsAdminSetup : SessionState()
    object LoggedOut : SessionState()
    data class LoggedIn(val user: UserAccountEntity) : SessionState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserAccountRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionState: StateFlow<SessionState> = sessionPreferences.loggedInUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                flow { emit(if (repository.hasAnyAdmin()) SessionState.LoggedOut else SessionState.NeedsAdminSetup) }
            } else {
                repository.observeById(userId).map { user ->
                    if (user != null && user.isActive) SessionState.LoggedIn(user) else SessionState.LoggedOut
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState.Loading)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = repository.login(username, password)) {
                is AuthResult.Success -> {
                    sessionPreferences.setLoggedInUserId(result.user.id)
                    _loginError.value = null
                }
                is AuthResult.Error -> _loginError.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun createFirstAdmin(username: String, password: String, fullName: String) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = repository.createUser(username, password, fullName, UserRole.ADMIN)) {
                is AuthResult.Success -> {
                    sessionPreferences.setLoggedInUserId(result.user.id)
                    _loginError.value = null
                }
                is AuthResult.Error -> _loginError.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun logout() {
        viewModelScope.launch { sessionPreferences.setLoggedInUserId(null) }
    }

    fun clearError() { _loginError.value = null }
}
