package com.cbo.memcloud.feature.notes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cbo.memcloud.core.model.Note
import com.cbo.memcloud.core.ui.cards.CardSwipeType
import com.cbo.memcloud.core.ui.cards.DefaultSwipeBackground
import com.cbo.memcloud.core.ui.cards.SwipeableCard
import com.cbo.memcloud.feature.notes.NotesViewType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NoteCard(
    note: Note,
    viewType: NotesViewType,
    onNoteClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    onArchiveClick: (String) -> Unit,
    onUnarchiveClick: (String) -> Unit,
    onTrashClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onRestoreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    swipeType: CardSwipeType = CardSwipeType.NONE, // Optional swipe feature, enabled by default for notes
) {
    var showMenu by remember { mutableStateOf(false) }

    SwipeableCard(
        modifier = modifier.fillMaxWidth(),
        swipeType = swipeType,
        onSwipeLeft = {
            if (viewType == NotesViewType.TRASH) {
                onDeleteClick(note.id)
            } else {
                onTrashClick(note.id)
            }
        },
        onSwipeRight = {
            if (viewType == NotesViewType.TRASH) {
                onRestoreClick(note.id)
            } else if (viewType == NotesViewType.ARCHIVED) {
                onUnarchiveClick(note.id)
            }
        },
        swipeBackground = { left, right -> DefaultSwipeBackground(swipeType, left, right) },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onNoteClick(note.id) }
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${if (note.createdAt != note.updatedAt) "* " else ""}${note.title.ifEmpty { "Untitled Note" }}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (viewType == NotesViewType.TRASH) {
                    IconButton(
                        onClick = { onRestoreClick(note.id) },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Restore note",
                        )
                    }
                }

                IconButton(
                    onClick = { onFavoriteClick(note.id, !note.isFavorite) },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (note.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (note.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        when (viewType) {
                            NotesViewType.ARCHIVED -> {
                                DropdownMenuItem(
                                    text = { Text("Unarchive") },
                                    onClick = {
                                        onUnarchiveClick(note.id)
                                        showMenu = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        onTrashClick(note.id)
                                        showMenu = false
                                    },
                                )
                            }
                            NotesViewType.TRASH -> {
                                DropdownMenuItem(
                                    text = { Text("Delete permanently") },
                                    onClick = {
                                        onDeleteClick(note.id)
                                        showMenu = false
                                    },
                                )
                            }
                            else -> {
                                DropdownMenuItem(
                                    text = { Text("Archive") },
                                    onClick = {
                                        onArchiveClick(note.id)
                                        showMenu = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        onTrashClick(note.id)
                                        showMenu = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val lastModified =
                    if (note.updatedAt > 0) {
                        formatDateTime(note.updatedAt)
                    } else {
                        "Unknown date"
                    }

                Text(
                    text = lastModified,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (note.tags.isNotEmpty()) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = note.tags.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun formatDateTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")
    return dateTime.format(formatter)
}
