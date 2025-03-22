package com.cbo.memcloud.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cbo.memcloud.core.model.Note
import java.time.Instant

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean,
    val tags: String, // Stored as comma-separated values
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val isDeleted: Boolean
) {
    companion object {
        fun fromNote(note: Note): NoteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            isSynced = note.isSynced,
            tags = note.tags.joinToString(","),
            isFavorite = note.isFavorite,
            isArchived = note.isArchived,
            isDeleted = note.isDeleted
        )
    }

    fun toNote(): Note = Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        isFavorite = isFavorite,
        isArchived = isArchived,
        isDeleted = isDeleted
    )
} 