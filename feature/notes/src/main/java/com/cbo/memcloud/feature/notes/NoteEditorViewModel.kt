package com.cbo.memcloud.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.data.repository.NotebooksRepository
import com.cbo.memcloud.core.model.Note
import com.cbo.memcloud.core.model.Notebook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class NoteEditorUiState(
    val note: Note? = null,
    val title: String = "",
    val content: String = "",
    val notebookId: String = "default",
    val notebooks: List<Notebook> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val notebooksRepository: NotebooksRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()
    
    init {
        loadNotebooks()
    }
    
    fun loadNote(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val note = notesRepository.getNoteById(id)
                if (note != null) {
                    _uiState.update {
                        it.copy(
                            note = note,
                            title = note.title,
                            content = note.content,
                            notebookId = note.notebookId,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Note not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading note",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun loadNotebooks() {
        viewModelScope.launch {
            notebooksRepository.getAllNotebooks().collect { notebooks ->
                _uiState.update { it.copy(notebooks = notebooks) }
                
                // If no notebook is selected, set the default one
                if (uiState.value.notebookId.isBlank()) {
                    val defaultNotebook = notebooksRepository.getDefaultNotebook()
                    _uiState.update { it.copy(notebookId = defaultNotebook.id) }
                }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun updateNotebookId(notebookId: String) {
        _uiState.update { it.copy(notebookId = notebookId) }
    }
    
    fun saveNote() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val currentNote = currentState.note
                
                val updatedNote = if (currentNote != null) {
                    currentNote.copy(
                        title = currentState.title,
                        content = currentState.content,
                        notebookId = currentState.notebookId,
                        updatedAt = Instant.now().toEpochMilli()
                    )
                } else {
                    Note(
                        id = UUID.randomUUID().toString(),
                        title = currentState.title,
                        content = currentState.content,
                        createdAt = Instant.now().toEpochMilli(),
                        updatedAt = Instant.now().toEpochMilli(),
                        notebookId = currentState.notebookId
                    )
                }
                
                notesRepository.saveNote(updatedNote)
                _uiState.update { it.copy(note = updatedNote) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error saving note")
                }
            }
        }
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            notesRepository.toggleNoteFavorite(currentNote.id, !currentNote.isFavorite)
            _uiState.update {
                it.copy(note = it.note?.copy(isFavorite = !currentNote.isFavorite))
            }
        }
    }
    
    fun archiveNote() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            notesRepository.archiveNote(currentNote.id)
            _uiState.update {
                it.copy(note = it.note?.copy(isArchived = true))
            }
        }
    }
    
    fun unarchiveNote() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            notesRepository.unarchiveNote(currentNote.id)
            _uiState.update {
                it.copy(note = it.note?.copy(isArchived = false))
            }
        }
    }
    
    fun moveToTrash() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            notesRepository.moveNoteToTrash(currentNote.id)
            _uiState.update {
                it.copy(note = it.note?.copy(isDeleted = true))
            }
        }
    }
    
    fun deleteNotePermanently() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            notesRepository.deleteNotePermanently(currentNote.id)
        }
    }
} 