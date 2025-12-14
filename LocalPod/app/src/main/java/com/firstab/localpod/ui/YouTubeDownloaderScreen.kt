package com.firstab.localpod.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.firstab.localpod.SharedViewModel

@Composable
fun YouTubeDownloaderScreen(navController: NavController, viewModel: SharedViewModel) {
    var youtubeUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val downloading by viewModel.downloading.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it },
            label = { Text("YouTube URL") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !downloading
        )
        Button(
            onClick = { 
                viewModel.downloadYouTubeAudio(youtubeUrl)
                Toast.makeText(context, "Starting download...", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = !downloading
        ) {
            Text("Download MP3")
        }
        if (downloading) {
            LinearProgressIndicator(
                progress = downloadProgress,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}
