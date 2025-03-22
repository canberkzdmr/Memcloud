package com.cbo.memcloud.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cbo.memcloud.core.database.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?
    
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)
    
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :id")
    suspend fun moveNoteToTrash(id: String)
    
    @Query("UPDATE notes SET isArchived = 1 WHERE id = :id")
    suspend fun archiveNote(id: String)
    
    @Query("UPDATE notes SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveNote(id: String)
    
    @Query("UPDATE notes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateNoteFavoriteStatus(id: String, isFavorite: Boolean)
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNotePermanently(id: String)
    
    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyTrash()
    
    @Query("UPDATE notes SET isSynced = :isSynced WHERE id = :id")
    suspend fun updateNoteSyncStatus(id: String, isSynced: Boolean)
    
    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>
} 