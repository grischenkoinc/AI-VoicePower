package com.aivoicepower.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import com.aivoicepower.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudTtsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsTracker: AnalyticsTracker
) {
    companion object {
        private const val TAG = "CloudTtsManager"
        private const val TTS_URL = "https://texttospeech.googleapis.com/v1/text:synthesize"
        private const val VOICE_NAME = "uk-UA-Chirp3-HD-Umbriel"
        private const val LANGUAGE_CODE = "uk-UA"
        private const val MAX_TEXT_BYTES = 4500
        private const val MAX_CACHE_SIZE = 30
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentJob: Job? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    // LRU cache: sentence text hashCode → MP3 bytes
    private val cache = object : LinkedHashMap<Int, ByteArray>(32, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, ByteArray>?): Boolean {
            return size > MAX_CACHE_SIZE
        }
    }

    private val apiKey: String
        get() = BuildConfig.GOOGLE_CLOUD_TTS_API_KEY

    // ===== Public API =====

    fun speak(text: String) {
        if (text.isBlank()) return
        if (apiKey.isBlank()) {
            Log.e(TAG, "Google Cloud TTS API key not set")
            return
        }

        stop()

        currentJob = scope.launch {
            try {
                _isSpeaking.value = true

                val sentences = splitIntoSentences(text)
                var prefetchedAudio: ByteArray? = null

                for (i in sentences.indices) {
                    if (!isActive) break

                    val nextDeferred = if (i + 1 < sentences.size) {
                        async { synthesizeWithCache(sentences[i + 1]) }
                    } else null

                    val audio = if (i == 0) {
                        synthesizeWithCache(sentences[i])
                    } else {
                        prefetchedAudio ?: synthesizeWithCache(sentences[i])
                    }

                    if (!isActive) break
                    audio?.let { playAudioBytes(it) }

                    prefetchedAudio = nextDeferred?.await()
                }
            } catch (e: CancellationException) {
                // Normal cancellation
            } catch (e: Exception) {
                Log.e(TAG, "TTS error: ${e.message}", e)
            } finally {
                _isSpeaking.value = false
            }
        }
    }

    fun stop() {
        currentJob?.cancel()
        currentJob = null
        releaseMediaPlayer()
        _isSpeaking.value = false
    }

    fun release() {
        stop()
        scope.cancel()
        cache.clear()
        cleanupTempFiles()
    }

    fun warmUp() {
        if (apiKey.isBlank()) return
        scope.launch {
            try {
                // Minimal request to establish HTTP keep-alive connection
                synthesize("Привіт")
                Log.d(TAG, "Connection warmed up")
            } catch (e: Exception) {
                Log.w(TAG, "Warm-up failed: ${e.message}")
            }
        }
    }

    // ===== SSML Generation =====

    private fun sentenceToSsml(sentence: String): String {
        var escaped = sentence
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")

        // Breaks at punctuation
        escaped = escaped.replace(", ", ",<break time=\"1ms\"/> ")
        escaped = escaped.replace(". ", ".<break time=\"75ms\"/> ")
        escaped = escaped.replace("! ", "!<break time=\"75ms\"/> ")
        escaped = escaped.replace("? ", "?<break time=\"75ms\"/> ")
        escaped = escaped.replace(": ", ":<break time=\"50ms\"/> ")
        escaped = escaped.replace("; ", ";<break time=\"50ms\"/> ")
        escaped = escaped.replace("— ", "—<break time=\"50ms\"/> ")
        escaped = escaped.replace(" — ", " —<break time=\"50ms\"/> ")

        val rate = detectRate(sentence)
        val contour = detectPitchContour(sentence)

        return "<speak><prosody rate=\"$rate\" $contour>$escaped</prosody></speak>"
    }

    private fun detectRate(text: String): String {
        return when {
            // Short energetic phrases
            text.length < 30 -> "130%"
            // Greetings / encouragement
            text.contains(Regex("(?i)(привіт|вітаю|молодець|чудово|супер|браво|відмінно|так тримати|гарна робота)")) -> "124%"
            // Long explanatory sentences
            text.length > 120 -> "120%"
            // Medium explanations
            text.length > 80 -> "123%"
            // Default lively pace
            else -> "125%"
        }
    }

    private fun detectPitchContour(text: String): String {
        val trimmed = text.trim()

        return when {
            // Questions — flat then rising at end
            trimmed.endsWith("?") ->
                "contour=\"(0%,+0.3st)(60%,+0st)(85%,+1.5st)(100%,+2.5st)\""

            // Exclamations / motivation — energetic, higher overall
            trimmed.endsWith("!") ->
                "contour=\"(0%,+1.5st)(30%,+1st)(70%,+0.5st)(100%,+1.2st)\""

            // Encouragement keywords — warm, uplifting contour
            trimmed.contains(Regex("(?i)(молодець|чудово|супер|браво|відмінно|так тримати|гарна робота|вітаю|продовжуй)")) ->
                "contour=\"(0%,+0.8st)(40%,+1.2st)(70%,+0.8st)(100%,+0.5st)\""

            // Lists / instructions (contains colon or numbered items)
            trimmed.contains(":") || trimmed.matches(Regex("^\\d+[.)].*")) ->
                "contour=\"(0%,+0.5st)(50%,+0.2st)(100%,-0.2st)\""

            // Regular statements — natural rise-fall
            else ->
                "contour=\"(0%,+0.5st)(25%,+0.3st)(75%,-0.2st)(100%,-0.5st)\""
        }
    }

    // ===== Network =====

    private fun synthesizeWithCache(text: String): ByteArray? {
        val key = text.hashCode()
        synchronized(cache) {
            cache[key]?.let { return it }
        }
        val audio = synthesize(text) ?: return null
        synchronized(cache) {
            cache[key] = audio
        }
        return audio
    }

    /** Поточний контекст TTS для аналітики (coach, debate, exercise тощо) */
    private var currentTtsContext: String = "unknown"

    fun setTtsContext(context: String) {
        currentTtsContext = context
    }

    private fun synthesize(text: String): ByteArray? {
        // Strip markdown formatting
        val cleanText = text
            .replace(Regex("\\*\\*(.+?)\\*\\*")) { it.groupValues[1] }
            .replace(Regex("__(.+?)__")) { it.groupValues[1] }
            .replace(Regex("\\*(.+?)\\*")) { it.groupValues[1] }

        // Track TTS char count
        val charCount = cleanText.length
        analyticsTracker.logTtsSynthesized(
            charCount = charCount,
            context = currentTtsContext,
            isPremium = false // встановлюється через User Property
        )

        val ssml = sentenceToSsml(cleanText)

        val jsonBody = JSONObject().apply {
            put("input", JSONObject().put("ssml", ssml))
            put("voice", JSONObject().apply {
                put("languageCode", LANGUAGE_CODE)
                put("name", VOICE_NAME)
            })
            put("audioConfig", JSONObject().apply {
                put("audioEncoding", "MP3")
                put("sampleRateHertz", 24000)
                put("effectsProfileId", org.json.JSONArray().put("handset-class-device"))
            })
        }

        val request = Request.Builder()
            .url("$TTS_URL?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            Log.e(TAG, "TTS API error ${response.code}: $errorBody")
            return null
        }

        val responseBody = response.body?.string() ?: return null
        val audioContent = JSONObject(responseBody).getString("audioContent")
        return Base64.decode(audioContent, Base64.DEFAULT)
    }

    // ===== Playback =====

    private suspend fun playAudioBytes(audioBytes: ByteArray) {
        val tempFile = File(context.cacheDir, "tts_${System.currentTimeMillis()}.mp3")
        FileOutputStream(tempFile).use { it.write(audioBytes) }

        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                val player = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    setOnCompletionListener { mp ->
                        mp.release()
                        tempFile.delete()
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.success(Unit))
                        }
                    }
                    setOnErrorListener { mp, what, extra ->
                        Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                        mp.release()
                        tempFile.delete()
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.success(Unit))
                        }
                        true
                    }
                }

                mediaPlayer = player

                continuation.invokeOnCancellation {
                    player.release()
                    tempFile.delete()
                    mediaPlayer = null
                }

                player.start()
            }
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    // ===== Text Splitting =====

    private fun splitIntoSentences(text: String): List<String> {
        // Split by sentence-ending punctuation followed by space
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (sentences.isEmpty()) return listOf(text)

        // Merge sentences that would be too small (< 10 chars) with previous
        val merged = mutableListOf<String>()
        for (sentence in sentences) {
            if (merged.isNotEmpty() && sentence.length < 10) {
                merged[merged.lastIndex] = "${merged.last()} $sentence"
            } else {
                merged.add(sentence)
            }
        }

        // Split any sentence that exceeds byte limit
        val result = mutableListOf<String>()
        for (sentence in merged) {
            if (sentence.toByteArray(Charsets.UTF_8).size <= MAX_TEXT_BYTES) {
                result.add(sentence)
            } else {
                // Split by comma clauses
                val parts = sentence.split(Regex("(?<=,)\\s+"))
                val chunk = StringBuilder()
                for (part in parts) {
                    val withPart = if (chunk.isEmpty()) part else "$chunk $part"
                    if (withPart.toByteArray(Charsets.UTF_8).size > MAX_TEXT_BYTES) {
                        if (chunk.isNotEmpty()) result.add(chunk.toString())
                        chunk.clear()
                        chunk.append(part)
                    } else {
                        chunk.clear()
                        chunk.append(withPart)
                    }
                }
                if (chunk.isNotEmpty()) result.add(chunk.toString())
            }
        }

        return result
    }

    private fun cleanupTempFiles() {
        context.cacheDir.listFiles()?.filter {
            it.name.startsWith("tts_") && it.name.endsWith(".mp3")
        }?.forEach { it.delete() }
    }
}
