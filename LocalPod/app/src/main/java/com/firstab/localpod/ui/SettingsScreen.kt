package com.firstab.localpod.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firstab.localpod.PreferencesManager
import com.firstab.localpod.ui.theme.LocalPodTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var showSeekDurationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item {
                    Text(
                        text = "Library Management",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("Local Folder") },
                        supportingContent = { Text("/storage/emulated/0/Download/Podcasts") },
                        leadingContent = {
                            Icon(Icons.Filled.Folder, contentDescription = "Folder")
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("Scan for new episodes") },
                        leadingContent = {
                            Icon(Icons.Filled.Sync, contentDescription = "Scan")
                        },
                        trailingContent = {
                            Button(onClick = { Toast.makeText(context, "Scanning for new episodes...", Toast.LENGTH_SHORT).show() }) {
                                Text("Scan")
                            }
                        }
                    )
                }
                item {
                    Text(
                        text = "Playback",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    var autoplay by remember { mutableStateOf(preferencesManager.autoplay) }
                    ListItem(
                        headlineContent = { Text("Auto-play next episode") },
                        trailingContent = {
                            Switch(checked = autoplay, onCheckedChange = {
                                autoplay = it
                                preferencesManager.autoplay = it
                            })
                        }
                    )
                }

                item {
                    ListItem(
                        headlineContent = { Text("Seek Duration") },
                        trailingContent = {
                            Text("${preferencesManager.seekDuration}s")
                        },
                        modifier = Modifier.clickable { showSeekDurationDialog = true }
                    )
                }
                item {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("App Theme") },
                        trailingContent = {
                            Text("System Default")
                        }
                    )
                }
            }
        }
    )

    if (showSeekDurationDialog) {
        AlertDialog(
            onDismissRequest = { showSeekDurationDialog = false },
            title = { Text("Seek Duration") },
            text = {
                Column {
                    Text("Forward:")
                    Slider(value = preferencesManager.seekDuration.toFloat(), onValueChange = { preferencesManager.seekDuration = it.toInt() }, valueRange = 5f..60f)
                }
            },
            confirmButton = {
                Button(onClick = { showSeekDurationDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    LocalPodTheme {
        SettingsScreen(rememberNavController())
    }
}