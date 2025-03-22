package com.cbo.memcloud.core.data.repository

import com.cbo.memcloud.core.database.dao.NoteDao
import com.cbo.memcloud.core.database.model.NoteEntity
import com.cbo.memcloud.core.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DefaultNotesRepositoryTest {

    private lateinit var noteDao: NoteDao
    private lateinit var repository: DefaultNotesRepository

    @Before
    fun setup() {
        noteDao = mock()
        repository = DefaultNotesRepository(noteDao)
    }

    @Test
    fun `getAllNotes should return mapped notes`() = runTest {
        // Given
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Note 1",
                content = "Content 1",
                createdAt = 1000L,
                updatedAt = 2000L,
                isSynced = false,
                tags = "tag1,tag2",
                isFavorite = false,
                isArchived = false,
                isDeleted = false
            )
        )
        
        whenever(noteDao.getAllNotes()).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.getAllNotes().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Note 1", result[0].title)
        assertEquals("Content 1", result[0].content)
        assertEquals(1000L, result[0].createdAt)
        assertEquals(2000L, result[0].updatedAt)
        assertEquals(false, result[0].isSynced)
        assertEquals(listOf("tag1", "tag2"), result[0].tags)
        assertEquals(false, result[0].isFavorite)
        assertEquals(false, result[0].isArchived)
        assertEquals(false, result[0].isDeleted)
    }

    @Test
    fun `getFavoriteNotes should return only favorite notes`() = runTest {
        // Given
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Favorite Note",
                content = "Content",
                createdAt = 1000L,
                updatedAt = 2000L,
                isFavorite = true
            )
        )
        
        whenever(noteDao.getFavoriteNotes()).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.getFavoriteNotes().first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isFavorite)
    }

    @Test
    fun `getArchivedNotes should return only archived notes`() = runTest {
        // Given
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Archived Note",
                content = "Content",
                createdAt = 1000L,
                updatedAt = 2000L,
                isArchived = true
            )
        )
        
        whenever(noteDao.getArchivedNotes()).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.getArchivedNotes().first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isArchived)
    }

    @Test
    fun `getDeletedNotes should return only deleted notes`() = runTest {
        // Given
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Deleted Note",
                content = "Content",
                createdAt = 1000L,
                updatedAt = 2000L,
                isDeleted = true
            )
        )
        
        whenever(noteDao.getDeletedNotes()).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.getDeletedNotes().first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isDeleted)
    }

    @Test
    fun `searchNotes should return notes matching the query`() = runTest {
        // Given
        val query = "test"
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Test Note",
                content = "Content",
                createdAt = 1000L,
                updatedAt = 2000L
            )
        )
        
        whenever(noteDao.searchNotes("%$query%")).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.searchNotes(query).first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].title.contains(query, ignoreCase = true))
    }

    @Test
    fun `getNoteById should return the note with the specified ID`() = runTest {
        // Given
        val noteId = "note-id"
        val noteEntity = NoteEntity(
            id = noteId,
            title = "Test Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        whenever(noteDao.getNoteById(noteId)).thenReturn(noteEntity)
        
        // When
        val result = repository.getNoteById(noteId)
        
        // Then
        assertNotNull(result)
        assertEquals(noteId, result?.id)
    }

    @Test
    fun `getNoteById should return null when note does not exist`() = runTest {
        // Given
        val noteId = "non-existent-id"
        whenever(noteDao.getNoteById(noteId)).thenReturn(null)
        
        // When
        val result = repository.getNoteById(noteId)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `saveNote should insert note into DAO`() = runTest {
        // Given
        val note = Note(
            id = "new-note",
            title = "New Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        // When
        repository.saveNote(note)
        
        // Then
        val captor = argumentCaptor<NoteEntity>()
        verify(noteDao).insertOrUpdateNote(captor.capture())
        
        val capturedEntity = captor.firstValue
        assertEquals("new-note", capturedEntity.id)
        assertEquals("New Note", capturedEntity.title)
        assertEquals("Content", capturedEntity.content)
        assertEquals(1000L, capturedEntity.createdAt)
        assertEquals(2000L, capturedEntity.updatedAt)
    }

    @Test
    fun `toggleNoteFavorite should update note favorite status`() = runTest {
        // Given
        val noteId = "note-id"
        val isFavorite = true
        
        // When
        repository.toggleNoteFavorite(noteId, isFavorite)
        
        // Then
        verify(noteDao).updateNoteFavoriteStatus(noteId, isFavorite)
    }

    @Test
    fun `archiveNote should set note as archived`() = runTest {
        // Given
        val noteId = "note-id"
        
        // When
        repository.archiveNote(noteId)
        
        // Then
        verify(noteDao).updateNoteArchivedStatus(noteId, true)
    }

    @Test
    fun `unarchiveNote should set note as not archived`() = runTest {
        // Given
        val noteId = "note-id"
        
        // When
        repository.unarchiveNote(noteId)
        
        // Then
        verify(noteDao).updateNoteArchivedStatus(noteId, false)
    }

    @Test
    fun `moveNoteToTrash should set note as deleted`() = runTest {
        // Given
        val noteId = "note-id"
        
        // When
        repository.moveNoteToTrash(noteId)
        
        // Then
        verify(noteDao).updateNoteDeletedStatus(noteId, true)
    }

    @Test
    fun `deleteNotePermanently should delete note from DAO`() = runTest {
        // Given
        val noteId = "note-id"
        
        // When
        repository.deleteNotePermanently(noteId)
        
        // Then
        verify(noteDao).deleteNoteById(noteId)
    }

    @Test
    fun `emptyTrash should delete all trashed notes`() = runTest {
        // When
        repository.emptyTrash()
        
        // Then
        verify(noteDao).deleteAllTrashedNotes()
    }

    @Test
    fun `getUnsyncedNotes should return only unsynced notes`() = runTest {
        // Given
        val noteEntities = listOf(
            NoteEntity(
                id = "1",
                title = "Unsynced Note",
                content = "Content",
                createdAt = 1000L,
                updatedAt = 2000L,
                isSynced = false
            )
        )
        
        whenever(noteDao.getUnsyncedNotes()).thenReturn(flowOf(noteEntities))
        
        // When
        val result = repository.getUnsyncedNotes().first()
        
        // Then
        assertEquals(1, result.size)
        assertFalse(result[0].isSynced)
    }

    @Test
    fun `markNoteAsSynced should update note sync status`() = runTest {
        // Given
        val noteId = "note-id"
        
        // When
        repository.markNoteAsSynced(noteId)
        
        // Then
        verify(noteDao).updateNoteSyncStatus(noteId, true)
    }
} 