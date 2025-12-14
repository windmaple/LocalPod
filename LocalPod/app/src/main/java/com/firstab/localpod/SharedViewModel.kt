package com.firstab.localpod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.media.MediaPlayer

class SharedViewModel : ViewModel() {
    private val _mediaPlayer = MediaPlayer()
    val mediaPlayer: MediaPlayer
        get() = _mediaPlayer

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition = _currentPosition.asStateFlow()

    private val _currentEpisode = MutableStateFlow<PodcastEpisode?>(null)
    val currentEpisode = _currentEpisode.asStateFlow()

    fun playEpisode(episode: PodcastEpisode) {
        _currentEpisode.value = episode
        _mediaPlayer.reset()
        _mediaPlayer.setDataSource(episode.path)
        _mediaPlayer.prepare()
        _mediaPlayer.start()
        _isPlaying.value = true
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
