package com.cbo.memcloud.feature.notes

import com.cbo.memcloud.core.data.repository.NotesRepository
import com.cbo.memcloud.core.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

@ExperimentalCoroutinesApi
class NoteEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NotesRepository
    private lateinit var viewModel: NoteEditorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = NoteEditorViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNote should update UI state with note when note exists`() = runTest {
        // Given
        val noteId = "test-id"
        val testNote = Note(
            id = noteId,
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        whenever(repository.getNoteById(noteId)).thenReturn(testNote)

        // When
        viewModel.loadNote(noteId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(testNote, state.note)
        assertEquals("Test Title", state.title)
        assertEquals("Test Content", state.content)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadNote should set isLoading to false when note does not exist`() = runTest {
        // Given
        val noteId = "non-existent-id"
        whenever(repository.getNoteById(noteId)).thenReturn(null)

        // When
        viewModel.loadNote(noteId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.note)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadNote should update error state when exception is thrown`() = runTest {
        // Given
        val noteId = "test-id"
        val errorMessage = "Error loading note"
        whenever(repository.getNoteById(noteId)).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.loadNote(noteId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `updateTitle should update the title in UI state`() {
        // When
        viewModel.updateTitle("New Title")

        // Then
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `updateContent should update the content in UI state`() {
        // When
        viewModel.updateContent("New Content")

        // Then
        assertEquals("New Content", viewModel.uiState.value.content)
    }

    @Test
    fun `saveNote should update existing note when note exists`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Original Title",
            content = "Original Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        // Setup the viewModel with a note
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Update title and content
        viewModel.updateTitle("Updated Title")
        viewModel.updateContent("Updated Content")
        
        // When
        viewModel.saveNote()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).saveNote(any())
    }

    @Test
    fun `saveNote should create new note when note does not exist`() = runTest {
        // Given
        viewModel.updateTitle("New Note Title")
        viewModel.updateContent("New Note Content")
        
        // When
        viewModel.saveNote()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).saveNote(any())
    }

    @Test
    fun `saveNote should not save when title and content are blank`() = runTest {
        // Given
        viewModel.updateTitle("")
        viewModel.updateContent("")
        
        // When
        viewModel.saveNote()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - repository.saveNote should not be called
        verify(repository, org.mockito.kotlin.never()).saveNote(any())
    }

    @Test
    fun `toggleFavorite should toggle favorite status of note`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isFavorite = false
        )
        
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).toggleNoteFavorite("test-id", true)
        assertTrue(viewModel.uiState.value.note?.isFavorite == true)
    }

    @Test
    fun `archiveNote should set isArchived to true`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = false
        )
        
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.archiveNote()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).archiveNote("test-id")
        assertTrue(viewModel.uiState.value.note?.isArchived == true)
    }

    @Test
    fun `unarchiveNote should set isArchived to false`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = true
        )
        
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.unarchiveNote()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).unarchiveNote("test-id")
        assertFalse(viewModel.uiState.value.note?.isArchived == true)
    }

    @Test
    fun `moveToTrash should set isDeleted to true`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isDeleted = false
        )
        
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.moveToTrash()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).moveNoteToTrash("test-id")
        assertTrue(viewModel.uiState.value.note?.isDeleted == true)
    }

    @Test
    fun `deleteNotePermanently should set note to null`() = runTest {
        // Given
        val existingNote = Note(
            id = "test-id",
            title = "Test Title",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isDeleted = true
        )
        
        whenever(repository.getNoteById("test-id")).thenReturn(existingNote)
        viewModel.loadNote("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.deleteNotePermanently()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(repository).deleteNotePermanently("test-id")
        assertNull(viewModel.uiState.value.note)
    }
} 