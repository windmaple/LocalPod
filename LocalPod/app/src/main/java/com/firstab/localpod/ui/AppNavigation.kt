package com.firstab.localpod.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.firstab.localpod.SharedViewModel

@Composable
fun AppNavigation(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "podcast_library") {
        composable("podcast_library") {
            PodcastLibraryScreen(navController, viewModel)
        }
        composable(
            "now_playing?episodePath={episodePath}",
            arguments = listOf(navArgument("episodePath") { nullable = true })
        ) { backStackEntry ->
            NowPlayingScreen(navController, viewModel)
        }
        composable("settings") {
            SettingsScreen(navController)
        }

    }
}