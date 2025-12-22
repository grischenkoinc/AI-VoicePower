package com.aivoicepower.utils.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioPlayerUtil(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var onCompletionCallback: (() -> Unit)? = null

    suspend fun play(filePath: String, onCompletion: (() -> Unit)? = null) = withContext(Dispatchers.IO) {
        stop()

        onCompletionCallback = onCompletion

        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()

            setOnCompletionListener {
                onCompletionCallback?.invoke()
            }

            start()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
            // Player might not be started
        }
        mediaPlayer?.release()
        mediaPlayer = null
        onCompletionCallback = null
    }

    fun release() {
        stop()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
