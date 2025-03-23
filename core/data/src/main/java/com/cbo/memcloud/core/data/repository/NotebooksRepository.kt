package com.cbo.memcloud.core.data.repository

import com.cbo.memcloud.core.model.Notebook
import kotlinx.coroutines.flow.Flow

interface NotebooksRepository {
    fun getAllNotebooks(): Flow<List<Notebook>>
    
    suspend fun getNotebookById(id: String): Notebook?
    
    suspend fun getDefaultNotebook(): Notebook
    
    suspend fun saveNotebook(notebook: Notebook): String
    
    suspend fun updateNotebook(notebook: Notebook)
    
    suspend fun deleteNotebook(id: String): Boolean
    
    suspend fun getNotesCountInNotebook(notebookId: String): Int
} 