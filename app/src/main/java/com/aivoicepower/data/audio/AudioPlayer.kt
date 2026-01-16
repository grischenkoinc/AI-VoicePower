package com.aivoicepower.data.audio

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(filePath: String, onComplete: () -> Unit = {}) {
        stop() // Stop any current playback

        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            setOnCompletionListener {
                _isPlaying.value = false
                onComplete()
            }
            start()
        }
        _isPlaying.value = true
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }

    fun getDuration(filePath: String): Int {
        return try {
            val mp = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
            }
            val duration = mp.duration / 1000 // Convert to seconds
            mp.release()
            duration
        } catch (e: Exception) {
            0
        }
    }
}
