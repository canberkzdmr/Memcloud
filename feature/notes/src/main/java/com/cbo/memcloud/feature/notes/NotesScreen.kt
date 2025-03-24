package com.cbo.memcloud.feature.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cbo.memcloud.core.logger.MemLogger
import com.cbo.memcloud.core.ui.cards.CardSwipeType
import com.cbo.memcloud.feature.notes.SortOption
import com.cbo.memcloud.feature.notes.components.NoteCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNavigateToEditor: (String?) -> Unit,
    onNavigateToNotebooks: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    MemLogger.i("note screen init")
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
                            if (drawerState.isOpen) {
                                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Close drawer",
                                    )
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open drawer",
                                    )
                                }
                            }
                        },
                        actions = {
                            var showSortMenu by remember { mutableStateOf(false) }

                            Box {
                                IconButton(onClick = { showSortMenu = true }) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Sort,
                                            contentDescription = "Sort",
                                        )

                                        val arrowIcon =
                                            when {
                                                notesUiState.sortOption == SortOption.UPDATED_ASC ||
                                                    notesUiState.sortOption == SortOption.CREATED_ASC ||
                                                    notesUiState.sortOption == SortOption.TITLE_ASC -> Icons.Default.ArrowUpward
                                                else -> Icons.Default.ArrowDownward
                                            }

                                        Icon(
                                            imageVector = arrowIcon,
                                            contentDescription = "Sort Direction",
                                            modifier =
                                                Modifier
                                                    .size(12.dp)
                                                    .align(Alignment.BottomEnd)
                                                    .offset(x = (-2).dp, y = (-2).dp),
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false },
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                Text("Last updated")
                                                if (notesUiState.sortOption == SortOption.UPDATED_DESC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Descending",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                } else if (notesUiState.sortOption == SortOption.UPDATED_ASC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "Ascending",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSortOption(
                                                if (notesUiState.sortOption == SortOption.UPDATED_DESC) {
                                                    SortOption.UPDATED_ASC
                                                } else {
                                                    SortOption.UPDATED_DESC
                                                },
                                            )
                                            showSortMenu = false
                                        },
                                        colors =
                                            MenuDefaults.itemColors(
                                                textColor =
                                                    if (notesUiState.sortOption == SortOption.UPDATED_DESC ||
                                                        notesUiState.sortOption == SortOption.UPDATED_ASC
                                                    ) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                            ),
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                Text("Creation date")
                                                if (notesUiState.sortOption == SortOption.CREATED_DESC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Descending",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                } else if (notesUiState.sortOption == SortOption.CREATED_ASC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "Ascending",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSortOption(
                                                if (notesUiState.sortOption == SortOption.CREATED_DESC) {
                                                    SortOption.CREATED_ASC
                                                } else {
                                                    SortOption.CREATED_DESC
                                                },
                                            )
                                            showSortMenu = false
                                        },
                                        colors =
                                            MenuDefaults.itemColors(
                                                textColor =
                                                    if (notesUiState.sortOption == SortOption.CREATED_DESC ||
                                                        notesUiState.sortOption == SortOption.CREATED_ASC
                                                    ) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                            ),
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                Text("Title")
                                                if (notesUiState.sortOption == SortOption.TITLE_DESC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Z to A",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                } else if (notesUiState.sortOption == SortOption.TITLE_ASC) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "A to Z",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSortOption(
                                                if (notesUiState.sortOption == SortOption.TITLE_ASC) {
                                                    SortOption.TITLE_DESC
                                                } else {
                                                    SortOption.TITLE_ASC
                                                },
                                            )
                                            showSortMenu = false
                                        },
                                        colors =
                                            MenuDefaults.itemColors(
                                                textColor =
                                                    if (notesUiState.sortOption == SortOption.TITLE_DESC ||
                                                        notesUiState.sortOption == SortOption.TITLE_ASC
                                                    ) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                            ),
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.setSearchActive(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                )
                            }

                            if (notesUiState.viewType == NotesViewType.ALL) {
                                IconButton(onClick = { onNavigateToNotebooks() }) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = "Manage Notebooks",
                                    )
                                }
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

                    // Notebook filter - only show in ALL notes view
                    if (notesUiState.viewType == NotesViewType.ALL && !notesUiState.isSearchActive) {
                        val notebooksViewModel: NotebooksViewModel = hiltViewModel()
                        val notebooksUiState by notebooksViewModel.uiState.collectAsState()

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Notebooks",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        LazyRow(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            item {
                                FilterChip(
                                    selected = notesUiState.selectedNotebookId == null,
                                    onClick = { viewModel.setSelectedNotebook(null) },
                                    label = { Text("All") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Book,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    },
                                )
                            }

                            items(notebooksUiState.notebooks) { notebook ->
                                FilterChip(
                                    selected = notesUiState.selectedNotebookId == notebook.id,
                                    onClick = { viewModel.setSelectedNotebook(notebook.id) },
                                    label = { Text(notebook.name) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Book,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (notesUiState.viewType != NotesViewType.TRASH) {
                    FloatingActionButton(
                        onClick = {
                            MemLogger.i("Add Note onClick")
                            onNavigateToEditor(null)
                        },
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
                            onDeleteClick = {
                                viewModel.deleteNotePermanently(it)
                                MemLogger.i("note size: ${notes.size}")
                                if (notes.size in 1..1) {
                                    viewModel.setViewType(NotesViewType.ALL)
                                }
                                            },
                            onRestoreClick = {
                                MemLogger.i("NoteCard onRestoreClick")
                                viewModel.restoreNote(it)
                            },
                            swipeType = when (notesUiState.viewType) {
                                NotesViewType.ALL -> CardSwipeType.LEFT
                                NotesViewType.FAVORITES, NotesViewType.ARCHIVED -> CardSwipeType.NONE
                                NotesViewType.TRASH -> CardSwipeType.BOTH
                            }
                        )
                    }
                }
            }
        }
    }
}
