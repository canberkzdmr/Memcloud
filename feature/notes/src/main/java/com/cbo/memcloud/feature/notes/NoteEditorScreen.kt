package com.cbo.memcloud.feature.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNote(noteId)
        }
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.note?.title.isNullOrBlank()) "New Note" else uiState.note?.title ?: "New Note"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveNote()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            viewModel.saveNote() 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Note"
                        )
                    }
                    
                    if (noteId != null) {
                        IconButton(
                            onClick = { 
                                viewModel.toggleFavorite() 
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.note?.isFavorite == true) 
                                    Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = if (uiState.note?.isFavorite == true) 
                                    "Remove from Favorites" else "Add to Favorites"
                            )
                        }
                        
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (uiState.note?.isArchived == true) {
                                DropdownMenuItem(
                                    text = { Text("Unarchive") },
                                    onClick = {
                                        viewModel.unarchiveNote()
                                        showMenu = false
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Archive") },
                                    onClick = {
                                        viewModel.archiveNote()
                                        showMenu = false
                                        onNavigateBack()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Archive,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                            
                            if (uiState.note?.isDeleted == true) {
                                DropdownMenuItem(
                                    text = { Text("Delete permanently") },
                                    onClick = {
                                        viewModel.deleteNotePermanently()
                                        showMenu = false
                                        onNavigateBack()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Move to trash") },
                                    onClick = {
                                        viewModel.moveToTrash()
                                        showMenu = false
                                        onNavigateBack()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                TextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = { Text("Title") },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    placeholder = { Text("Note content") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
                
                uiState.note?.updatedAt?.let { timestamp ->
                    val date = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                    Text(
                        text = "Last modified: ${date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
} 