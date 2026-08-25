package com.example.rentmanagement.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.UserAccountEntity
import com.example.rentmanagement.data.repository.AuthResult
import com.example.rentmanagement.data.repository.UserAccountRepository
import com.example.rentmanagement.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val repository: UserAccountRepository
) : ViewModel() {

    val users: StateFlow<List<UserAccountEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun addUser(username: String, password: String, fullName: String, role: UserRole, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.createUser(username, password, fullName, role)) {
                is AuthResult.Success -> { _saveError.value = null; onSuccess() }
                is AuthResult.Error -> _saveError.value = result.message
            }
        }
    }

    fun setActive(id: Long, isActive: Boolean) {
        viewModelScope.launch { withContext(NonCancellable) { repository.setActive(id, isActive) } }
    }

    fun clearError() { _saveError.value = null }
}
