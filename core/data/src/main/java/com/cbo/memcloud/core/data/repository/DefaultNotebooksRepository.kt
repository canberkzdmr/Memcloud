package com.cbo.memcloud.core.data.repository

import com.cbo.memcloud.core.database.dao.NotebookDao
import com.cbo.memcloud.core.database.model.NotebookEntity
import com.cbo.memcloud.core.model.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotebooksRepository @Inject constructor(
    private val notebookDao: NotebookDao
) : NotebooksRepository {

    override fun getAllNotebooks(): Flow<List<Notebook>> {
        return notebookDao.getAllNotebooks().map { entities ->
            entities.map { it.toNotebook() }
        }
    }

    override suspend fun getNotebookById(id: String): Notebook? {
        return notebookDao.getNotebookById(id)?.toNotebook()
    }

    override suspend fun getDefaultNotebook(): Notebook {
        val defaultNotebook = notebookDao.getDefaultNotebook()
        return defaultNotebook?.toNotebook() ?: Notebook.createDefault().also {
            saveNotebook(it)
        }
    }

    override suspend fun saveNotebook(notebook: Notebook): String {
        val id = if (notebook.id.isBlank()) UUID.randomUUID().toString() else notebook.id
        val updatedNotebook = if (notebook.id.isBlank()) {
            notebook.copy(
                id = id,
                createdAt = Instant.now().toEpochMilli(),
                updatedAt = Instant.now().toEpochMilli()
            )
        } else {
            notebook.copy(updatedAt = Instant.now().toEpochMilli())
        }
        
        notebookDao.insertNotebook(NotebookEntity.fromNotebook(updatedNotebook))
        return id
    }

    override suspend fun updateNotebook(notebook: Notebook) {
        val updatedNotebook = notebook.copy(updatedAt = Instant.now().toEpochMilli())
        notebookDao.updateNotebook(NotebookEntity.fromNotebook(updatedNotebook))
    }

    override suspend fun deleteNotebook(id: String): Boolean {
        val defaultNotebook = getDefaultNotebook()
        
        // Count notes in the notebook
        val notesCount = notebookDao.getNotesCountInNotebook(id)
        
        // If there are notes in the notebook, move them to the default notebook
        if (notesCount > 0) {
            notebookDao.moveNotesToAnotherNotebook(id, defaultNotebook.id)
        }
        
        // Delete the notebook (will fail if it's the default notebook)
        val deletedCount = notebookDao.deleteNotebook(id)
        
        return deletedCount > 0
    }

    override suspend fun getNotesCountInNotebook(notebookId: String): Int {
        return notebookDao.getNotesCountInNotebook(notebookId)
    }
} 