package com.aivoicepower.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording(): File? {
        return try {
            audioFile = File(context.cacheDir, "voice_recording_${System.currentTimeMillis()}.m4a")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)

                prepare()
                start()
            }

            audioFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            audioFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            audioFile?.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
            audioFile = null
        }
    }
}
