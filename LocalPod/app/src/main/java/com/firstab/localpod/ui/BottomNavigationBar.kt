package com.firstab.localpod.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Podcasts, contentDescription = "Episodes") },
            label = { Text("Episodes") },
            selected = currentRoute == "podcast_library",
            onClick = { navController.navigate("podcast_library") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.CloudDownload, contentDescription = "YouTube") },
            label = { Text("YouTube") },
            selected = currentRoute == "youtube_downloader",
            onClick = { navController.navigate("youtube_downloader") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = { navController.navigate("settings") }
        )
    }
}
