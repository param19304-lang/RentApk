package com.example.rentmanagement.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.database.AppDatabase
import com.example.rentmanagement.utils.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val database: AppDatabase,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _restoredSuccessfully = MutableStateFlow(false)
    val restoredSuccessfully: StateFlow<Boolean> = _restoredSuccessfully

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy

    fun exportBackup(destination: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isBusy.value = true
            val success = BackupManager.exportTo(appContext, database, destination)
            _status.value = if (success) "Backup saved successfully" else "Backup failed. Please try again."
            _isBusy.value = false
        }
    }

    fun importBackup(source: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isBusy.value = true
            val success = BackupManager.importFrom(appContext, database, source)
            _status.value = if (success) "Restore complete. Restarting app..." else "Restore failed. Your existing data is unchanged."
            _restoredSuccessfully.value = success
            _isBusy.value = false
        }
    }

    fun clearStatus() { _status.value = null }
}
