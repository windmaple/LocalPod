package com.firstab.localpod

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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



    private val _episodes = MutableStateFlow<List<PodcastEpisode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    fun setEpisodes(episodes: List<PodcastEpisode>) {
        _episodes.value = episodes
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
            val currentIndex = _episodes.value.indexOf(currentEpisode.value)
            if (currentIndex != -1 && currentIndex < _episodes.value.size - 1) {
                playEpisode(_episodes.value[currentIndex + 1])
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
