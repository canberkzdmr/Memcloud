package com.cbo.memcloud.core.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false
) {
    companion object {
        fun createEmpty(): Note = Note(
            id = "",
            title = "",
            content = "",
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli()
        )
    }
} 