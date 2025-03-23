package com.cbo.memcloud.core.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Notebook(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    val isDefault: Boolean = false
) {
    companion object {
        fun createDefault(): Notebook = Notebook(
            id = "default",
            name = "Default",
            description = "Default notebook",
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli(),
            isDefault = true
        )
        
        fun createEmpty(): Notebook = Notebook(
            id = "",
            name = "",
            description = "",
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli()
        )
    }
} 