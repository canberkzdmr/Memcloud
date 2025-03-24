package com.cbo.memcloud.core.data.repository

import com.cbo.memcloud.core.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>

    fun getFavoriteNotes(): Flow<List<Note>>

    fun getArchivedNotes(): Flow<List<Note>>

    fun getDeletedNotes(): Flow<List<Note>>

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun getNoteById(id: String): Note?

    suspend fun saveNote(note: Note): String

    suspend fun updateNote(note: Note)

    suspend fun moveNoteToTrash(id: String)

    suspend fun archiveNote(id: String)

    suspend fun unarchiveNote(id: String)

    suspend fun toggleNoteFavorite(
        id: String,
        isFavorite: Boolean,
    )

    suspend fun deleteNotePermanently(id: String)

    suspend fun restoreNote(id: String)

    suspend fun emptyTrash()

    suspend fun getUnsyncedNotes(): List<Note>

    suspend fun markNoteSynced(
        id: String,
        isSynced: Boolean,
    )
} 
