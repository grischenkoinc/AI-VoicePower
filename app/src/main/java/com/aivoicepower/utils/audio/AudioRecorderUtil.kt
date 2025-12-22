package com.aivoicepower.utils.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRecorderUtil(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String? = null
    private var startTime: Long = 0

    suspend fun startRecording(outputPath: String) = withContext(Dispatchers.IO) {
        outputFile = outputPath
        startTime = System.currentTimeMillis()

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outputPath)

            prepare()
            start()
        }
    }

    suspend fun stopRecording(): RecordingResult? = withContext(Dispatchers.IO) {
        val recorder = mediaRecorder ?: return@withContext null
        val file = outputFile ?: return@withContext null

        try {
            recorder.stop()
            recorder.release()
            mediaRecorder = null

            val durationMs = System.currentTimeMillis() - startTime

            RecordingResult(
                filePath = file,
                durationMs = durationMs
            )
        } catch (e: Exception) {
            null
        }
    }

    fun release() {
        try {
            mediaRecorder?.stop()
        } catch (_: Exception) {
            // Recorder might not be started
        }
        mediaRecorder?.release()
        mediaRecorder = null
    }
}

data class RecordingResult(
    val filePath: String,
    val durationMs: Long
)
