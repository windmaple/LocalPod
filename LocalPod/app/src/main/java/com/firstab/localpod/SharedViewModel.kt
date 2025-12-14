package com.firstab.localpod

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.util.SparseArray
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val _mediaPlayer = MediaPlayer()
    val mediaPlayer: MediaPlayer
        get() = _mediaPlayer

    private val preferencesManager = PreferencesManager(application)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition = _currentPosition.asStateFlow()

    private val _currentEpisode = MutableStateFlow<PodcastEpisode?>(null)
    val currentEpisode = _currentEpisode.asStateFlow()

    private val _downloading = MutableStateFlow(false)
    val downloading = _downloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress = _downloadProgress.asStateFlow()

    private var downloadId: Long = 0

    private var episodes: List<PodcastEpisode> = emptyList()

    fun setEpisodes(episodes: List<PodcastEpisode>) {
        this.episodes = episodes
    }

    fun playEpisode(episode: PodcastEpisode) {
        _currentEpisode.value = episode
        _mediaPlayer.reset()
        _mediaPlayer.setDataSource(episode.path)
        _mediaPlayer.prepare()
        _mediaPlayer.start()
        _mediaPlayer.isLooping = false
        _mediaPlayer.setOnCompletionListener { playNextEpisode() }
        _isPlaying.value = true
    }

    fun playNextEpisode() {
        if (preferencesManager.autoplay) {
            val currentIndex = episodes.indexOf(currentEpisode.value)
            if (currentIndex != -1 && currentIndex < episodes.size - 1) {
                playEpisode(episodes[currentIndex + 1])
            }
        }
    }

    fun togglePlayback() {
        if (_mediaPlayer.isPlaying) {
            _mediaPlayer.pause()
            _isPlaying.value = false
        } else {
            _mediaPlayer.start()
            _isPlaying.value = true
        }
    }

    fun downloadYouTubeAudio(url: String) {
        viewModelScope.launch {
            try {
                val extractor = object : YouTubeExtractor(getApplication()) {
                    override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
                        Log.d("YouTubeExtractor", "onExtractionComplete")
                        if (ytFiles != null) {
                            val audioFile = ytFiles.get(251) // 251 is for webm audio
                            if (audioFile != null) {
                                val downloadManager = getApplication<Application>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                val request = DownloadManager.Request(Uri.parse(audioFile.url))
                                request.setTitle(videoMeta?.title)
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Podcasts/${videoMeta?.title}.webm")
                                downloadId = downloadManager.enqueue(request)
                                _downloading.value = true
                                Log.d("SharedViewModel", "Downloading started")

                                viewModelScope.launch {
                                    var downloading = true
                                    while (downloading) {
                                        val query = DownloadManager.Query()
                                        query.setFilterById(downloadId)
                                        val cursor = downloadManager.query(query)
                                        if (cursor.moveToFirst()) {
                                            val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                            val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                            if (bytesTotal > 0) {
                                                _downloadProgress.value = (bytesDownloaded.toFloat() / bytesTotal.toFloat())
                                                Log.d("SharedViewModel", "Download progress: ${_downloadProgress.value}")
                                            }
                                            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                                            if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                                                downloading = false
                                                _downloading.value = false
                                                Log.d("SharedViewModel", "Downloading finished")
                                            }
                                        }
                                        cursor.close()
                                        delay(1000)
                                    }
                                }
                            }
                        }
                    }
                }
                extractor.extract(url, true, true)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error downloading YouTube audio", e)
            }
        }
    }

    init {
        viewModelScope.launch {
            while (true) {
                if (_mediaPlayer.isPlaying) {
                    _currentPosition.value = _mediaPlayer.currentPosition
                }
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _mediaPlayer.release()
    }
}
