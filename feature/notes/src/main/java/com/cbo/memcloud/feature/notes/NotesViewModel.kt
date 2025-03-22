package com.cbo.memcloud.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(NotesViewState())
    val viewState: StateFlow<NotesViewState> = _viewState

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _notesViewType = MutableStateFlow(NotesViewType.ALL)

    val notesUiState = combine(
        _notesViewType,
        _searchQuery,
        _isSearchActive
    ) { viewType, searchQuery, isSearchActive ->
        val notesFlow = when {
            isSearchActive && searchQuery.isNotEmpty() -> notesRepository.searchNotes(searchQuery)
            viewType == NotesViewType.FAVORITES -> notesRepository.getFavoriteNotes()
            viewType == NotesViewType.ARCHIVED -> notesRepository.getArchivedNotes()
            viewType == NotesViewType.TRASH -> notesRepository.getDeletedNotes()
            else -> notesRepository.getAllNotes()
        }
        
        val notes = notesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        NotesUiState(
            notes = notes,
            searchQuery = searchQuery,
            isSearchActive = isSearchActive,
            viewType = viewType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun setViewType(viewType: NotesViewType) {
        _notesViewType.value = viewType
    }

    fun toggleNoteFavorite(noteId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            notesRepository.toggleNoteFavorite(noteId, isFavorite)
        }
    }

    fun moveNoteToTrash(noteId: String) {
        viewModelScope.launch {
            notesRepository.moveNoteToTrash(noteId)
        }
    }

    fun archiveNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.archiveNote(noteId)
        }
    }

    fun unarchiveNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.unarchiveNote(noteId)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            notesRepository.emptyTrash()
        }
    }

    fun deleteNotePermanently(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNotePermanently(noteId)
        }
    }
}

data class NotesViewState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class NotesUiState(
    val notes: StateFlow<List<Note>> = MutableStateFlow(emptyList()),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val viewType: NotesViewType = NotesViewType.ALL
)

enum class NotesViewType {
    ALL,
    FAVORITES,
    ARCHIVED,
    TRASH
} 