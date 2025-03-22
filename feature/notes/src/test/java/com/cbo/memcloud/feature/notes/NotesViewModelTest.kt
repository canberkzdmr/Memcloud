package com.cbo.memcloud.feature.notes

import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NotesRepository
    private lateinit var viewModel: NotesViewModel
    
    // Create flows for each note type
    private val allNotesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val favoriteNotesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val archivedNotesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val deletedNotesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val searchNotesFlow = MutableStateFlow<List<Note>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Create mock repository that returns our flows
        repository = mock {
            on { getAllNotes() } doReturn allNotesFlow
            on { getFavoriteNotes() } doReturn favoriteNotesFlow
            on { getArchivedNotes() } doReturn archivedNotesFlow
            on { getDeletedNotes() } doReturn deletedNotesFlow
        }
        
        // Setup the search function to return our search flow
        whenever(repository.searchNotes(org.mockito.kotlin.any())).thenReturn(searchNotesFlow)
        
        viewModel = NotesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setSearchQuery should update searchQuery state`() = runTest {
        // Collect the UI state in a background coroutine
        var searchQuery = ""
        val job = launch {
            viewModel.notesUiState.collect {
                searchQuery = it.searchQuery
            }
        }
        
        // Initial advancement to collect initial value
        advanceUntilIdle()
        
        // When
        viewModel.setSearchQuery("test query")
        advanceUntilIdle()
        
        // Then
        assertEquals("test query", searchQuery)
        
        job.cancel()
    }

    @Test
    fun `setSearchActive should update isSearchActive state`() = runTest {
        // Collect the UI state in a background coroutine
        var isSearchActive = false
        val job = launch {
            viewModel.notesUiState.collect {
                isSearchActive = it.isSearchActive
            }
        }
        
        // Initial advancement to collect initial value
        advanceUntilIdle()
        
        // When
        viewModel.setSearchActive(true)
        advanceUntilIdle()
        
        // Then
        assertTrue(isSearchActive)
        
        // When
        viewModel.setSearchActive(false)
        advanceUntilIdle()
        
        // Then
        assertFalse(isSearchActive)
        
        job.cancel()
    }

    @Test
    fun `setViewType should update viewType state`() = runTest {
        // Collect the UI state in a background coroutine
        var viewType = NotesViewType.ALL
        val job = launch {
            viewModel.notesUiState.collect {
                viewType = it.viewType
            }
        }
        
        // Initial advancement to collect initial value
        advanceUntilIdle()
        
        // When - test ALL (already default)
        assertEquals(NotesViewType.ALL, viewType)
        
        // When - test FAVORITES
        viewModel.setViewType(NotesViewType.FAVORITES)
        advanceUntilIdle()
        
        // Then
        assertEquals(NotesViewType.FAVORITES, viewType)
        
        // When - test ARCHIVED
        viewModel.setViewType(NotesViewType.ARCHIVED)
        advanceUntilIdle()
        
        // Then
        assertEquals(NotesViewType.ARCHIVED, viewType)
        
        // When - test TRASH
        viewModel.setViewType(NotesViewType.TRASH)
        advanceUntilIdle()
        
        // Then
        assertEquals(NotesViewType.TRASH, viewType)
        
        job.cancel()
    }

    @Test
    fun `toggleNoteFavorite should call repository method with correct parameters`() = runTest {
        // When
        viewModel.toggleNoteFavorite("note-id", true)
        advanceUntilIdle()
        
        // Then
        verify(repository).toggleNoteFavorite("note-id", true)
    }

    @Test
    fun `archiveNote should call repository method`() = runTest {
        // When
        viewModel.archiveNote("note-id")
        advanceUntilIdle()
        
        // Then
        verify(repository).archiveNote("note-id")
    }

    @Test
    fun `unarchiveNote should call repository method`() = runTest {
        // When
        viewModel.unarchiveNote("note-id")
        advanceUntilIdle()
        
        // Then
        verify(repository).unarchiveNote("note-id")
    }

    @Test
    fun `moveNoteToTrash should call repository method`() = runTest {
        // When
        viewModel.moveNoteToTrash("note-id")
        advanceUntilIdle()
        
        // Then
        verify(repository).moveNoteToTrash("note-id")
    }

    @Test
    fun `deleteNotePermanently should call repository method`() = runTest {
        // When
        viewModel.deleteNotePermanently("note-id")
        advanceUntilIdle()
        
        // Then
        verify(repository).deleteNotePermanently("note-id")
    }
    
    @Test
    fun `emptyTrash should call repository method`() = runTest {
        // When
        viewModel.emptyTrash()
        advanceUntilIdle()
        
        // Then
        verify(repository).emptyTrash()
    }
} 