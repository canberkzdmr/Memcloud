package com.cbo.memcloud.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.cbo.memcloud.feature.notes.navigation.navigateToNoteEditor
import com.cbo.memcloud.feature.notes.navigation.notesGraph
import com.cbo.memcloud.feature.notes.navigation.notesRoute

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = notesRoute
    ) {
        notesGraph(
            onNavigateToNoteEditor = { noteId ->
                navController.navigateToNoteEditor(noteId)
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
} 