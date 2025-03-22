package com.cbo.memcloud.feature.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cbo.memcloud.feature.notes.NoteEditorScreen
import com.cbo.memcloud.feature.notes.NotesScreen

const val notesRoute = "notes"
private const val noteEditorRoute = "note_editor"
private const val noteIdArg = "noteId"

fun NavController.navigateToNotes(navOptions: NavOptions? = null) {
    this.navigate(notesRoute, navOptions)
}

fun NavController.navigateToNoteEditor(noteId: String? = null, navOptions: NavOptions? = null) {
    val route = if (noteId != null) {
        "$noteEditorRoute/$noteId"
    } else {
        noteEditorRoute
    }
    this.navigate(route, navOptions)
}

fun NavGraphBuilder.notesGraph(
    onNavigateToNoteEditor: (String?) -> Unit,
    onNavigateBack: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit = {}
) {
    composable(route = notesRoute) {
        NotesScreen(
            onNavigateToEditor = onNavigateToNoteEditor
        )
    }
    
    composable(
        route = "$noteEditorRoute/{$noteIdArg}",
        arguments = listOf(
            navArgument(noteIdArg) {
                type = NavType.StringType
            }
        )
    ) { entry ->
        val noteId = entry.arguments?.getString(noteIdArg)
        NoteEditorScreen(
            noteId = noteId,
            onNavigateBack = onNavigateBack
        )
    }
    
    composable(route = noteEditorRoute) {
        NoteEditorScreen(
            noteId = null,
            onNavigateBack = onNavigateBack
        )
    }
    
    nestedGraphs()
} 