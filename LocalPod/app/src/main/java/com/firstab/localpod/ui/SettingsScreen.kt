package com.firstab.localpod.ui

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firstab.localpod.ui.theme.LocalPodTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
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
                    val checkedState = remember { mutableStateOf(true) }
                    ListItem(
                        headlineContent = { Text("Auto-play next episode") },
                        trailingContent = {
                            Switch(checked = checkedState.value, onCheckedChange = { checkedState.value = it })
                        }
                    )
                }
                item {
                    val checkedState = remember { mutableStateOf(true) }
                    ListItem(
                        headlineContent = { Text("Skip silence") },
                        trailingContent = {
                            Switch(checked = checkedState.value, onCheckedChange = { checkedState.value = it })
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("Seek Duration") },
                        trailingContent = {
                            Text("30s / 15s")
                        }
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
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    LocalPodTheme {
        SettingsScreen(rememberNavController())
    }
}