package com.cbo.memcloud.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cbo.memcloud.core.database.model.NotebookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebooks ORDER BY name ASC")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>
    
    @Query("SELECT * FROM notebooks WHERE id = :id")
    suspend fun getNotebookById(id: String): NotebookEntity?
    
    @Query("SELECT * FROM notebooks WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultNotebook(): NotebookEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity)
    
    @Update
    suspend fun updateNotebook(notebook: NotebookEntity)
    
    @Query("DELETE FROM notebooks WHERE id = :id AND isDefault = 0")
    suspend fun deleteNotebook(id: String): Int
    
    @Query("UPDATE notes SET notebookId = :newNotebookId WHERE notebookId = :oldNotebookId")
    suspend fun moveNotesToAnotherNotebook(oldNotebookId: String, newNotebookId: String)
    
    @Query("SELECT COUNT(*) FROM notes WHERE notebookId = :notebookId")
    suspend fun getNotesCountInNotebook(notebookId: String): Int
} 