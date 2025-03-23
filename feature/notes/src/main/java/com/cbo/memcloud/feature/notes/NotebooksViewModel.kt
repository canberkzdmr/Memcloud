package com.cbo.memcloud.feature.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbo.memcloud.core.data.repository.NotebooksRepository
import com.cbo.memcloud.core.model.Notebook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotebooksUiState(
    val notebooks: List<Notebook> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingNew: Boolean = false,
    val isEditing: Boolean = false,
    val newNotebookName: String = "",
    val newNotebookDescription: String = "",
    val editingNotebookId: String? = null
)

@HiltViewModel
class NotebooksViewModel @Inject constructor(
    private val notebooksRepository: NotebooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotebooksUiState(isLoading = true))
    
    val uiState: StateFlow<NotebooksUiState> = _uiState.asStateFlow()

    init {
        loadNotebooks()
    }

    private fun loadNotebooks() {
        viewModelScope.launch {
            notebooksRepository.getAllNotebooks().collect { notebooks ->
                _uiState.update { it.copy(notebooks = notebooks, isLoading = false) }
            }
        }
    }

    fun startCreatingNotebook() {
        _uiState.update {
            it.copy(
                isCreatingNew = true,
                newNotebookName = "",
                newNotebookDescription = ""
            ) 
        }
    }
    
    fun cancelCreatingNotebook() {
        _uiState.update { it.copy(isCreatingNew = false) }
    }
    
    fun updateNewNotebookName(name: String) {
        _uiState.update { it.copy(newNotebookName = name) }
    }
    
    fun updateNewNotebookDescription(description: String) {
        _uiState.update { it.copy(newNotebookDescription = description) }
    }
    
    fun saveNewNotebook() {
        val name = _uiState.value.newNotebookName
        val description = _uiState.value.newNotebookDescription
        
        if (name.isBlank()) return
        
        viewModelScope.launch {
            try {
                val notebook = Notebook.createEmpty().copy(
                    name = name,
                    description = description
                )
                notebooksRepository.saveNotebook(notebook)
                _uiState.update { it.copy(isCreatingNew = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error creating notebook") }
            }
        }
    }
    
    fun startEditingNotebook(notebook: Notebook) {
        _uiState.update { 
            it.copy(
                isEditing = true,
                editingNotebookId = notebook.id,
                newNotebookName = notebook.name,
                newNotebookDescription = notebook.description
            ) 
        }
    }
    
    fun cancelEditingNotebook() {
        _uiState.update { 
            it.copy(
                isEditing = false,
                editingNotebookId = null
            ) 
        }
    }
    
    fun saveEditedNotebook() {
        val editingId = _uiState.value.editingNotebookId ?: return
        val name = _uiState.value.newNotebookName
        val description = _uiState.value.newNotebookDescription
        
        if (name.isBlank()) return
        
        viewModelScope.launch {
            try {
                val notebook = _uiState.value.notebooks.find { it.id == editingId } ?: return@launch
                val updatedNotebook = notebook.copy(
                    name = name,
                    description = description
                )
                notebooksRepository.updateNotebook(updatedNotebook)
                _uiState.update { 
                    it.copy(
                        isEditing = false,
                        editingNotebookId = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error updating notebook") }
            }
        }
    }
    
    fun deleteNotebook(id: String) {
        viewModelScope.launch {
            try {
                val result = notebooksRepository.deleteNotebook(id)
                if (!result) {
                    _uiState.update { 
                        it.copy(
                            error = "Cannot delete default notebook"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error deleting notebook") }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 