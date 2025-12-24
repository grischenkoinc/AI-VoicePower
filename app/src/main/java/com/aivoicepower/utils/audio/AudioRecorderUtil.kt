package com.aivoicepower.utils.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

/**
 * Utility for recording audio
 */
class AudioRecorderUtil(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var outputFilePath: String? = null

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
            isRecording = true
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Stop recording and return the output file path
     */
    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                reset()
            }
            isRecording = false
            outputFilePath
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
