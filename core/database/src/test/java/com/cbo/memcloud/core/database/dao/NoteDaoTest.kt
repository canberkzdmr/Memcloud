package com.cbo.memcloud.core.database.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cbo.memcloud.core.database.MemcloudDatabase
import com.cbo.memcloud.core.database.model.NoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class NoteDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDao: NoteDao
    private lateinit var db: MemcloudDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MemcloudDatabase::class.java
        ).allowMainThreadQueries().build()
        noteDao = db.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetNote() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        val retrievedNote = noteDao.getNoteById("1")
        
        // Then
        assertNotNull(retrievedNote)
        assertEquals("Test Note", retrievedNote?.title)
        assertEquals("Test Content", retrievedNote?.content)
    }

    @Test
    fun insertAndGetAllNotes() = runTest {
        // Given
        val note1 = NoteEntity(
            id = "1",
            title = "Note 1",
            content = "Content 1",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val note2 = NoteEntity(
            id = "2",
            title = "Note 2",
            content = "Content 2",
            createdAt = 1500L,
            updatedAt = 2500L
        )
        
        // When
        noteDao.insertOrUpdateNote(note1)
        noteDao.insertOrUpdateNote(note2)
        val allNotes = noteDao.getAllNotes().first()
        
        // Then
        assertEquals(2, allNotes.size)
    }

    @Test
    fun updateNote() = runTest {
        // Given
        val originalNote = NoteEntity(
            id = "1",
            title = "Original Title",
            content = "Original Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        // When
        noteDao.insertOrUpdateNote(originalNote)
        
        val updatedNote = originalNote.copy(
            title = "Updated Title",
            content = "Updated Content",
            updatedAt = 3000L
        )
        noteDao.insertOrUpdateNote(updatedNote)
        
        val retrievedNote = noteDao.getNoteById("1")
        
        // Then
        assertEquals("Updated Title", retrievedNote?.title)
        assertEquals("Updated Content", retrievedNote?.content)
        assertEquals(3000L, retrievedNote?.updatedAt)
    }

    @Test
    fun deleteNote() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Test Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        noteDao.deleteNoteById("1")
        val retrievedNote = noteDao.getNoteById("1")
        
        // Then
        assertNull(retrievedNote)
    }

    @Test
    fun getFavoriteNotes() = runTest {
        // Given
        val favoriteNote = NoteEntity(
            id = "1",
            title = "Favorite Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isFavorite = true
        )
        val regularNote = NoteEntity(
            id = "2",
            title = "Regular Note",
            content = "Content",
            createdAt = 1500L,
            updatedAt = 2500L,
            isFavorite = false
        )
        
        // When
        noteDao.insertOrUpdateNote(favoriteNote)
        noteDao.insertOrUpdateNote(regularNote)
        val favoriteNotes = noteDao.getFavoriteNotes().first()
        
        // Then
        assertEquals(1, favoriteNotes.size)
        assertEquals("Favorite Note", favoriteNotes[0].title)
        assertTrue(favoriteNotes[0].isFavorite)
    }

    @Test
    fun getArchivedNotes() = runTest {
        // Given
        val archivedNote = NoteEntity(
            id = "1",
            title = "Archived Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = true
        )
        val regularNote = NoteEntity(
            id = "2",
            title = "Regular Note",
            content = "Content",
            createdAt = 1500L,
            updatedAt = 2500L,
            isArchived = false
        )
        
        // When
        noteDao.insertOrUpdateNote(archivedNote)
        noteDao.insertOrUpdateNote(regularNote)
        val archivedNotes = noteDao.getArchivedNotes().first()
        
        // Then
        assertEquals(1, archivedNotes.size)
        assertEquals("Archived Note", archivedNotes[0].title)
        assertTrue(archivedNotes[0].isArchived)
    }

    @Test
    fun getDeletedNotes() = runTest {
        // Given
        val deletedNote = NoteEntity(
            id = "1",
            title = "Deleted Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isDeleted = true
        )
        val regularNote = NoteEntity(
            id = "2",
            title = "Regular Note",
            content = "Content",
            createdAt = 1500L,
            updatedAt = 2500L,
            isDeleted = false
        )
        
        // When
        noteDao.insertOrUpdateNote(deletedNote)
        noteDao.insertOrUpdateNote(regularNote)
        val deletedNotes = noteDao.getDeletedNotes().first()
        
        // Then
        assertEquals(1, deletedNotes.size)
        assertEquals("Deleted Note", deletedNotes[0].title)
        assertTrue(deletedNotes[0].isDeleted)
    }

    @Test
    fun searchNotes() = runTest {
        // Given
        val note1 = NoteEntity(
            id = "1",
            title = "Apple Pie Recipe",
            content = "Ingredients...",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val note2 = NoteEntity(
            id = "2",
            title = "Shopping List",
            content = "Buy apples",
            createdAt = 1500L,
            updatedAt = 2500L
        )
        val note3 = NoteEntity(
            id = "3",
            title = "Meeting Notes",
            content = "Discuss project timeline",
            createdAt = 1600L,
            updatedAt = 2600L
        )
        
        // When
        noteDao.insertOrUpdateNote(note1)
        noteDao.insertOrUpdateNote(note2)
        noteDao.insertOrUpdateNote(note3)
        val searchResults = noteDao.searchNotes("%apple%").first()
        
        // Then
        assertEquals(2, searchResults.size) // Should find "Apple Pie Recipe" and "Buy apples"
    }

    @Test
    fun updateNoteFavoriteStatus() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isFavorite = false
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        noteDao.updateNoteFavoriteStatus("1", true)
        val updatedNote = noteDao.getNoteById("1")
        
        // Then
        assertTrue(updatedNote?.isFavorite == true)
    }

    @Test
    fun updateNoteArchivedStatus() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = false
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        noteDao.updateNoteArchivedStatus("1", true)
        val updatedNote = noteDao.getNoteById("1")
        
        // Then
        assertTrue(updatedNote?.isArchived == true)
    }

    @Test
    fun updateNoteDeletedStatus() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isDeleted = false
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        noteDao.updateNoteDeletedStatus("1", true)
        val updatedNote = noteDao.getNoteById("1")
        
        // Then
        assertTrue(updatedNote?.isDeleted == true)
    }

    @Test
    fun deleteAllTrashedNotes() = runTest {
        // Given
        val trashedNote = NoteEntity(
            id = "1",
            title = "Trashed Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isDeleted = true
        )
        val regularNote = NoteEntity(
            id = "2",
            title = "Regular Note",
            content = "Content",
            createdAt = 1500L,
            updatedAt = 2500L,
            isDeleted = false
        )
        
        // When
        noteDao.insertOrUpdateNote(trashedNote)
        noteDao.insertOrUpdateNote(regularNote)
        noteDao.deleteAllTrashedNotes()
        
        val allNotes = noteDao.getAllNotes().first()
        val trashedNoteAfterDeletion = noteDao.getNoteById("1")
        
        // Then
        assertEquals(1, allNotes.size)
        assertNull(trashedNoteAfterDeletion)
    }

    @Test
    fun getUnsyncedNotes() = runTest {
        // Given
        val syncedNote = NoteEntity(
            id = "1",
            title = "Synced Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isSynced = true
        )
        val unsyncedNote = NoteEntity(
            id = "2",
            title = "Unsynced Note",
            content = "Content",
            createdAt = 1500L,
            updatedAt = 2500L,
            isSynced = false
        )
        
        // When
        noteDao.insertOrUpdateNote(syncedNote)
        noteDao.insertOrUpdateNote(unsyncedNote)
        val unsyncedNotes = noteDao.getUnsyncedNotes().first()
        
        // Then
        assertEquals(1, unsyncedNotes.size)
        assertEquals("Unsynced Note", unsyncedNotes[0].title)
        assertFalse(unsyncedNotes[0].isSynced)
    }

    @Test
    fun updateNoteSyncStatus() = runTest {
        // Given
        val note = NoteEntity(
            id = "1",
            title = "Test Note",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L,
            isSynced = false
        )
        
        // When
        noteDao.insertOrUpdateNote(note)
        noteDao.updateNoteSyncStatus("1", true)
        val updatedNote = noteDao.getNoteById("1")
        
        // Then
        assertTrue(updatedNote?.isSynced == true)
    }
} 