package com.aivoicepower.utils.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Audio Player Utility using MediaPlayer
 * Provides audio playback functionality for recordings
 */
class AudioPlayerUtil(private val context: Context) {

    companion object {
        private const val TAG = "AudioPlayer"
    }

    data class PlaybackState(
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0L,
        val duration: Long = 0L
    )

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Play audio from file path with optional completion callback
     */
    fun play(filePath: String, onComplete: (() -> Unit)? = null) {
        Log.d(TAG, "Attempting to play: $filePath")

        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "FILE NOT FOUND: $filePath")
            return
        }
        Log.d(TAG, "File exists, size: ${file.length()} bytes")

        try {
            // Release previous player if exists
            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()

                val durationMs = duration.toLong()
                Log.d(TAG, "MediaPlayer prepared, duration: ${durationMs}ms")

                _playbackState.value = PlaybackState(
                    isPlaying = true,
                    currentPosition = 0L,
                    duration = durationMs
                )

                setOnCompletionListener {
                    Log.d(TAG, "Playback completed")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPosition = durationMs
                    )
                    onComplete?.invoke()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = PlaybackState()
                    true
                }

                start()
                Log.d(TAG, "Playback started")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Playback error: ${e.message}", e)
            _playbackState.value = PlaybackState()
        }
    }

    /**
     * Pause current playback
     */
    fun pause() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPosition = player.currentPosition.toLong()
                    )
                    Log.d(TAG, "Playback paused at ${player.currentPosition}ms")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pause error: ${e.message}", e)
        }
    }

    /**
     * Resume paused playback
     */
    fun resume() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    _playbackState.value = _playbackState.value.copy(isPlaying = true)
                    Log.d(TAG, "Playback resumed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resume error: ${e.message}", e)
        }
    }

    /**
     * Stop playback and reset
     */
    fun stop() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                Log.d(TAG, "Playback stopped")
            }
            _playbackState.value = PlaybackState()
        } catch (e: Exception) {
            Log.e(TAG, "Stop error: ${e.message}", e)
        }
    }

    /**
     * Release resources
     */
    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            _playbackState.value = PlaybackState()
            Log.d(TAG, "MediaPlayer released")
        } catch (e: Exception) {
            Log.e(TAG, "Release error: ${e.message}", e)
        }
    }

    /**
     * Seek to position in milliseconds
     */
    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
            _playbackState.value = _playbackState.value.copy(currentPosition = positionMs)
            Log.d(TAG, "Seeked to ${positionMs}ms")
        } catch (e: Exception) {
            Log.e(TAG, "Seek error: ${e.message}", e)
        }
    }

    /**
     * Get duration of audio file in milliseconds
     */
    fun getDuration(filePath: String): Long {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "getDuration: File not found: $filePath")
            return 0L
        }

        return try {
            val player = MediaPlayer()
            player.setDataSource(filePath)
            player.prepare()
            val duration = player.duration.toLong()
            player.release()
            Log.d(TAG, "getDuration: $duration ms for $filePath")
            duration
        } catch (e: Exception) {
            Log.e(TAG, "getDuration error: ${e.message}", e)
            0L
        }
    }
}
