package com.cbo.memcloud.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.data.repository.NotebooksRepository
import com.cbo.memcloud.core.logger.MemLogger
import com.cbo.memcloud.core.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val notebooksRepository: NotebooksRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(NotesViewState())
    val viewState: StateFlow<NotesViewState> = _viewState

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _notesViewType = MutableStateFlow(NotesViewType.ALL)
    private val _sortOption = MutableStateFlow(SortOption.UPDATED_DESC)
    private val _selectedNotebookId = MutableStateFlow<String?>(null)

    val notesUiState = combine(
        _notesViewType,
        _searchQuery,
        _isSearchActive,
        _sortOption,
        _selectedNotebookId
    ) { viewType, searchQuery, isSearchActive, sortOption, selectedNotebookId ->
        val notesFlow = when {
            isSearchActive && searchQuery.isNotEmpty() -> notesRepository.searchNotes(searchQuery)
            viewType == NotesViewType.FAVORITES -> notesRepository.getFavoriteNotes()
            viewType == NotesViewType.ARCHIVED -> notesRepository.getArchivedNotes()
            viewType == NotesViewType.TRASH -> notesRepository.getDeletedNotes()
            else -> notesRepository.getAllNotes()
        }
        
        val notes = notesFlow.map { notesList ->
            // Filter by notebook if applicable
            val filteredNotes = if (selectedNotebookId != null && viewType == NotesViewType.ALL) {
                notesList.filter { it.notebookId == selectedNotebookId }
            } else {
                notesList
            }
            
            // Apply sorting
            when (sortOption) {
                SortOption.UPDATED_DESC -> filteredNotes.sortedByDescending { it.updatedAt }
                SortOption.UPDATED_ASC -> filteredNotes.sortedBy { it.updatedAt }
                SortOption.CREATED_DESC -> filteredNotes.sortedByDescending { it.createdAt }
                SortOption.CREATED_ASC -> filteredNotes.sortedBy { it.createdAt }
                SortOption.TITLE_ASC -> filteredNotes.sortedBy { it.title.lowercase() }
                SortOption.TITLE_DESC -> filteredNotes.sortedByDescending { it.title.lowercase() }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        NotesUiState(
            notes = notes,
            searchQuery = searchQuery,
            isSearchActive = isSearchActive,
            viewType = viewType,
            sortOption = sortOption,
            selectedNotebookId = selectedNotebookId
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

    fun setSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun setSelectedNotebook(notebookId: String?) {
        _selectedNotebookId.value = notebookId
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

    fun restoreNote(noteId: String) {
        viewModelScope.launch {
            MemLogger.i("restoreNote")
            notesRepository.restoreNote(noteId)
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
    val viewType: NotesViewType = NotesViewType.ALL,
    val sortOption: SortOption = SortOption.UPDATED_DESC,
    val selectedNotebookId: String? = null
)

enum class NotesViewType {
    ALL,
    FAVORITES,
    ARCHIVED,
    TRASH
}

enum class SortOption {
    UPDATED_DESC,  // Most recently updated first (default)
    UPDATED_ASC,   // Oldest updated first
    CREATED_DESC,  // Most recently created first
    CREATED_ASC,   // Oldest created first
    TITLE_ASC,     // Alphabetical A-Z
    TITLE_DESC     // Alphabetical Z-A
} 