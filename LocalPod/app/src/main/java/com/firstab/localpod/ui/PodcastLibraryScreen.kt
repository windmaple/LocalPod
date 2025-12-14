package com.firstab.localpod.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firstab.localpod.PodcastEpisode
import com.firstab.localpod.SharedViewModel
import com.firstab.localpod.ui.theme.LocalPodTheme
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastLibraryScreen(navController: NavController, viewModel: SharedViewModel) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
        }
    )

    var episodes by remember { mutableStateOf(emptyList<PodcastEpisode>()) }

    fun scanForEpisodes() {
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Podcasts")
        if (folder.exists()) {
            episodes = folder.listFiles { _, name -> name.endsWith(".mp3") }?.map {
                PodcastEpisode(it.nameWithoutExtension, "Unknown Artist", it.absolutePath)
            } ?: emptyList()
            viewModel.setEpisodes(episodes)
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            scanForEpisodes()
        }
    }

    val currentEpisode by viewModel.currentEpisode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Episodes") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Search titles or artists...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                if (!hasPermission) {
                    item {
                        Card(modifier = Modifier.padding(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("File Access Required", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Allow storage access to find and play local audio files on your device.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { launcher.launch(Manifest.permission.READ_MEDIA_AUDIO) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Allow Access")
                                }
                            }
                        }
                    }
                }
                items(episodes) { episode ->
                    ListItem(
                        headlineContent = { Text(episode.title) },
                        supportingContent = { Text(episode.artist) },
                        leadingContent = {
                            Icon(Icons.Filled.MusicNote, contentDescription = "Podcast")
                        },
                        trailingContent = {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More")
                        },
                        modifier = Modifier.clickable { 
                            viewModel.playEpisode(episode)
                            navController.navigate("now_playing") 
                        }
                    )
                }
            }
        },
        bottomBar = {
            Column {
                currentEpisode?.let {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.MusicNote, contentDescription = "Now Playing")
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(it.title, style = MaterialTheme.typography.bodyMedium)
                            Text(it.artist, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { viewModel.togglePlayback() }) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause"
                            )
                        }
                    }
                }
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Podcasts, contentDescription = "Episodes") },
                        label = { Text("Episodes") },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = false,
                        onClick = { navController.navigate("settings") }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PodcastLibraryScreenPreview() {
    LocalPodTheme {
        PodcastLibraryScreen(rememberNavController(), viewModel())
    }
}