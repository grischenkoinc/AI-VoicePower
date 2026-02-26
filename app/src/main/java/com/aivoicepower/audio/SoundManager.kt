package com.aivoicepower.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSounds = mutableMapOf<SoundEffect, Int>()
    private var loadedCount = 0
    private val totalSounds = SoundEffect.entries.size

    // Volume settings (0.0f to 1.0f)
    private var masterEnabled = true
    private var uiVolume = 0.8f
    private var feedbackVolume = 0.9f
    private var celebrationVolume = 1.0f

    init {
        preloadSounds()
        observeSettings()
    }

    private fun preloadSounds() {
        SoundEffect.entries.forEach { effect ->
            val soundId = soundPool.load(context, effect.resId, 1)
            loadedSounds[effect] = soundId
        }
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loadedCount++
        }
    }

    private fun observeSettings() {
        scope.launch {
            userPreferencesDataStore.soundSettingsFlow.collectLatest { settings ->
                masterEnabled = settings.isSoundEnabled
                uiVolume = settings.uiVolume
                feedbackVolume = settings.feedbackVolume
                celebrationVolume = settings.celebrationVolume
            }
        }
    }

    private val activeStreams = mutableListOf<Int>()

    fun play(effect: SoundEffect) {
        if (!masterEnabled) return
        val soundId = loadedSounds[effect] ?: return

        val volume = when (effect.category) {
            SoundCategory.UI -> uiVolume
            SoundCategory.FEEDBACK -> feedbackVolume
            SoundCategory.CELEBRATION -> celebrationVolume
        } * effect.volumeMultiplier

        if (volume <= 0f) return

        val streamId = soundPool.play(
            soundId,
            volume,  // left volume
            volume,  // right volume
            1,       // priority
            0,       // loop (0 = no loop)
            1.0f     // playback rate
        )
        if (streamId > 0) {
            activeStreams.add(streamId)
            if (activeStreams.size > 10) activeStreams.removeAt(0)
        }
    }

    fun stopAll() {
        activeStreams.forEach { soundPool.stop(it) }
        activeStreams.clear()
    }

    fun play(effect: SoundEffect, pitchRate: Float): Int {
        if (!masterEnabled) return 0
        val soundId = loadedSounds[effect] ?: return 0

        val volume = when (effect.category) {
            SoundCategory.UI -> uiVolume
            SoundCategory.FEEDBACK -> feedbackVolume
            SoundCategory.CELEBRATION -> celebrationVolume
        } * effect.volumeMultiplier

        if (volume <= 0f) return 0

        return soundPool.play(
            soundId,
            volume,
            volume,
            1,
            0,
            pitchRate.coerceIn(0.5f, 2.0f)
        )
    }

    fun stop(streamId: Int) {
        if (streamId > 0) {
            soundPool.stop(streamId)
        }
    }

    fun release() {
        soundPool.release()
    }
}
