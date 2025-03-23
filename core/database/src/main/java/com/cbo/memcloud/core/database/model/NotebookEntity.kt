package com.cbo.memcloud.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cbo.memcloud.core.model.Notebook
import java.time.Instant

@Entity(tableName = "notebooks")
data class NotebookEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDefault: Boolean
) {
    companion object {
        fun fromNotebook(notebook: Notebook): NotebookEntity = NotebookEntity(
            id = notebook.id,
            name = notebook.name,
            description = notebook.description,
            createdAt = notebook.createdAt,
            updatedAt = notebook.updatedAt,
            isDefault = notebook.isDefault
        )
    }

    fun toNotebook(): Notebook = Notebook(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDefault = isDefault
    )
} 