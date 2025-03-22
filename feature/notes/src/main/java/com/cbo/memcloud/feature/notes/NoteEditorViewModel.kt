package com.cbo.memcloud.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.model.Note
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
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()
    
    fun loadNote(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val note = notesRepository.getNoteById(id)
                if (note != null) {
                    _uiState.update { state ->
                        state.copy(
                            note = note,
                            title = note.title,
                            content = note.content,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentNote = currentState.note
            
            if (currentState.title.isBlank() && currentState.content.isBlank()) {
                return@launch // Don't save empty notes
            }
            
            val now = Instant.now().toEpochMilli()
            
            val updatedNote = currentNote?.copy(
                title = currentState.title,
                content = currentState.content,
                updatedAt = now
            )
                ?: Note(
                    id = UUID.randomUUID().toString(),
                    title = currentState.title,
                    content = currentState.content,
                    createdAt = now,
                    updatedAt = now
                )
            
            try {
                notesRepository.saveNote(updatedNote)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            val isFavorite = currentNote.isFavorite
            
            try {
                notesRepository.toggleNoteFavorite(currentNote.id, !isFavorite)
                _uiState.update { 
                    it.copy(note = it.note?.copy(isFavorite = !isFavorite))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun archiveNote() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            
            try {
                notesRepository.archiveNote(currentNote.id)
                _uiState.update { 
                    it.copy(note = it.note?.copy(isArchived = true))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun unarchiveNote() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            
            try {
                notesRepository.unarchiveNote(currentNote.id)
                _uiState.update { 
                    it.copy(note = it.note?.copy(isArchived = false))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun moveToTrash() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            
            try {
                notesRepository.moveNoteToTrash(currentNote.id)
                _uiState.update { 
                    it.copy(note = it.note?.copy(isDeleted = true))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteNotePermanently() {
        viewModelScope.launch {
            val currentNote = _uiState.value.note ?: return@launch
            
            try {
                notesRepository.deleteNotePermanently(currentNote.id)
                _uiState.update { 
                    it.copy(note = null)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
} 