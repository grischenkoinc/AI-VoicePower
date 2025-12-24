package com.aivoicepower.utils.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

/**
 * Result of a recording operation
 */
data class RecordingResult(
    val filePath: String,
    val durationMs: Long
)

/**
 * Utility for recording audio
 */
class AudioRecorderUtil(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var outputFilePath: String? = null
    private var recordingStartTime: Long = 0L

    /**
     * Start recording to the specified output file path
     */
    fun startRecording(outputPath: String) {
        try {
            // Ensure parent directory exists
            File(outputPath).parentFile?.mkdirs()

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputPath)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)

                prepare()
                start()
            }

            outputFilePath = outputPath
            recordingStartTime = System.currentTimeMillis()
            isRecording = true
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Stop recording and return the recording result with file path and duration
     */
    fun stopRecording(): RecordingResult? {
        return try {
            mediaRecorder?.apply {
                stop()
                reset()
            }
            isRecording = false
            val path = outputFilePath
            val duration = System.currentTimeMillis() - recordingStartTime

            if (path != null) {
                RecordingResult(filePath = path, durationMs = duration)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = isRecording

    /**
     * Release resources
     */
    fun release() {
        try {
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            outputFilePath = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
