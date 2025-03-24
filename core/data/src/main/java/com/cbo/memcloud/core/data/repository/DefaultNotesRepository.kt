package com.cbo.memcloud.core.data.repository

import com.cbo.memcloud.core.database.dao.NoteDao
import com.cbo.memcloud.core.database.model.NoteEntity
import com.cbo.memcloud.core.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotesRepository @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override fun getFavoriteNotes(): Flow<List<Note>> {
        return noteDao.getFavoriteNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getArchivedNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override fun getDeletedNotes(): Flow<List<Note>> {
        return noteDao.getDeletedNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toNote()
    }

    override suspend fun saveNote(note: Note): String {
        val id = if (note.id.isBlank()) UUID.randomUUID().toString() else note.id
        val updatedNote = if (note.id.isBlank()) {
            note.copy(
                id = id,
                createdAt = Instant.now().toEpochMilli(),
                updatedAt = Instant.now().toEpochMilli()
            )
        } else {
            note.copy(updatedAt = Instant.now().toEpochMilli())
        }
        
        noteDao.insertNote(NoteEntity.fromNote(updatedNote))
        return id
    }

    override suspend fun updateNote(note: Note) {
        val updatedNote = note.copy(updatedAt = Instant.now().toEpochMilli(), isSynced = false)
        noteDao.updateNote(NoteEntity.fromNote(updatedNote))
    }

    override suspend fun moveNoteToTrash(id: String) {
        noteDao.moveNoteToTrash(id)
    }

    override suspend fun archiveNote(id: String) {
        noteDao.archiveNote(id)
    }

    override suspend fun unarchiveNote(id: String) {
        noteDao.unarchiveNote(id)
    }

    override suspend fun toggleNoteFavorite(id: String, isFavorite: Boolean) {
        noteDao.updateNoteFavoriteStatus(id, isFavorite)
    }

    override suspend fun deleteNotePermanently(id: String) {
        noteDao.deleteNotePermanently(id)
    }

    override suspend fun restoreNote(id: String) {
        noteDao.restoreNote(id)
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override suspend fun getUnsyncedNotes(): List<Note> {
        return noteDao.getUnsyncedNotes().map { it.toNote() }
    }

    override suspend fun markNoteSynced(id: String, isSynced: Boolean) {
        noteDao.updateNoteSyncStatus(id, isSynced)
    }
} 