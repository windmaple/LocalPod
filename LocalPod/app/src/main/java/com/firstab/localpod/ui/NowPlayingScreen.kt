package com.firstab.localpod.ui

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firstab.localpod.PreferencesManager
import com.firstab.localpod.SharedViewModel
import com.firstab.localpod.ui.theme.LocalPodTheme
import kotlinx.coroutines.delay
import java.io.IOException

@Composable
fun ImageAsset(assetPath: String, contentDescription: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val assetManager = context.assets
    val assets = assetManager.list("")
    Log.d("ImageAsset", "Assets: ${assets?.joinToString()}")
    val inputStream = try {
        assetManager.open(assetPath)
    } catch (e: IOException) {
        Log.e("ImageAsset", "Error opening asset: $assetPath", e)
        return
    }
    val bitmap = BitmapFactory.decodeStream(inputStream)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(navController: NavController, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val currentEpisode by viewModel.currentEpisode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val image = remember { listOf("lake.png", "mountain.png", "ocean.png", "rain.png", "trail.png").random() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.MoreHoriz, contentDescription = "More")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(it).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ImageAsset(
                    assetPath = image,
                    contentDescription = "Podcast Cover",
                    modifier = Modifier.size(300.dp).padding(16.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    currentEpisode?.let {
                        Text(it.title, style = MaterialTheme.typography.headlineMedium)
                        Text(it.artist, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { viewModel.mediaPlayer.seekTo(it.toInt()) },
                    valueRange = 0f..viewModel.mediaPlayer.duration.toFloat(),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.mediaPlayer.seekTo(viewModel.mediaPlayer.currentPosition - preferencesManager.seekDuration * 1000) }) {
                        Icon(Icons.Outlined.Replay10, contentDescription = "Replay 10 seconds", modifier = Modifier.size(48.dp))
                    }
                    IconButton(onClick = { viewModel.togglePlayback() }) {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(72.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.mediaPlayer.seekTo(viewModel.mediaPlayer.currentPosition + preferencesManager.seekDuration * 1000) }) {
                        Icon(Icons.Outlined.Forward10, contentDescription = "Forward 10 seconds", modifier = Modifier.size(48.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var playbackSpeed by remember { mutableStateOf(1f) }
                    IconButton(onClick = { 
                        playbackSpeed = when (playbackSpeed) {
                            1f -> 1.25f
                            1.25f -> 1.5f
                            1.5f -> 1.75f
                            1.75f -> 2f
                            else -> 1f
                        }
                        viewModel.mediaPlayer.playbackParams = viewModel.mediaPlayer.playbackParams.setSpeed(playbackSpeed)
                    }) {
                        Icon(Icons.Outlined.Speed, contentDescription = "Playback speed")
                    }
                    Text("${playbackSpeed}x")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NowPlayingScreenPreview() {
    LocalPodTheme {
        NowPlayingScreen(rememberNavController(), viewModel())
    }
}