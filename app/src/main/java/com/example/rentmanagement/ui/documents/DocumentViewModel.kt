package com.example.rentmanagement.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmanagement.data.entities.DocumentEntity
import com.example.rentmanagement.data.repository.DocumentRepository
import com.example.rentmanagement.domain.model.DocumentCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val repository: DocumentRepository
) : ViewModel() {

    val documents: StateFlow<List<DocumentEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    fun addDocument(name: String, uri: String, category: DocumentCategory, notes: String?, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            _saveError.value = "Give this document a name"
            return
        }
        viewModelScope.launch {
            repository.addDocument(
                DocumentEntity(
                    name = name.trim(),
                    uri = uri,
                    category = category,
                    notes = notes?.trim()?.ifBlank { null }
                )
            )
            _saveError.value = null
            onSuccess()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteDocument(id) }
    }

    fun clearError() { _saveError.value = null }
}
