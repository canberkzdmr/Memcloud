package com.cbo.memcloud.feature.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cbo.memcloud.feature.notes.components.NoteCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNavigateToEditor: (String?) -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val viewState by viewModel.viewState.collectAsState()
    val notesUiState by viewModel.notesUiState.collectAsState()
    val notes by notesUiState.notes.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 300.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Memcloud",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                NavigationDrawerItem(
                    label = { Text("Notes") },
                    selected = notesUiState.viewType == NotesViewType.ALL,
                    onClick = {
                        viewModel.setViewType(NotesViewType.ALL)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Outlined.Notes, contentDescription = null) },
                )

                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = notesUiState.viewType == NotesViewType.FAVORITES,
                    onClick = {
                        viewModel.setViewType(NotesViewType.FAVORITES)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                )

                NavigationDrawerItem(
                    label = { Text("Archive") },
                    selected = notesUiState.viewType == NotesViewType.ARCHIVED,
                    onClick = {
                        viewModel.setViewType(NotesViewType.ARCHIVED)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                )

                NavigationDrawerItem(
                    label = { Text("Trash") },
                    selected = notesUiState.viewType == NotesViewType.TRASH,
                    onClick = {
                        viewModel.setViewType(NotesViewType.TRASH)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                )
            }
        },
    ) {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text =
                                    when (notesUiState.viewType) {
                                        NotesViewType.ALL -> "Notes"
                                        NotesViewType.FAVORITES -> "Favorites"
                                        NotesViewType.ARCHIVED -> "Archive"
                                        NotesViewType.TRASH -> "Trash"
                                    },
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Outlined.Notes,
                                    contentDescription = "Menu",
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.setSearchActive(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )

                    if (notesUiState.isSearchActive) {
                        SearchBar(
                            query = notesUiState.searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            onSearch = { },
                            active = true,
                            onActiveChange = { viewModel.setSearchActive(it) },
                            placeholder = { Text("Search notes") },
                            modifier = Modifier.fillMaxWidth(),
                        ) {}
                    }
                }
            },
            floatingActionButton = {
                if (notesUiState.viewType != NotesViewType.TRASH) {
                    FloatingActionButton(
                        onClick = { onNavigateToEditor(null) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Note",
                        )
                    }
                }
            },
        ) { paddingValues ->
            if (viewState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (notes.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text =
                            when (notesUiState.viewType) {
                                NotesViewType.ALL -> "No notes yet. Create one!"
                                NotesViewType.FAVORITES -> "No favorite notes yet."
                                NotesViewType.ARCHIVED -> "No archived notes."
                                NotesViewType.TRASH -> "Trash is empty."
                            },
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp,
                ) {
                    items(
                        items = notes,
                        key = { it.id },
                    ) { note ->
                        NoteCard(
                            note = note,
                            viewType = notesUiState.viewType,
                            onNoteClick = { onNavigateToEditor(it) },
                            onFavoriteClick = { id, isFavorite ->
                                viewModel.toggleNoteFavorite(id, isFavorite)
                            },
                            onArchiveClick = { viewModel.archiveNote(it) },
                            onUnarchiveClick = { viewModel.unarchiveNote(it) },
                            onTrashClick = { viewModel.moveNoteToTrash(it) },
                            onDeleteClick = { viewModel.deleteNotePermanently(it) },
                        )
                    }
                }
            }
        }
    }
} 
