package com.aivoicepower.domain.service

import android.util.Log
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.SkillSnapshotEntity
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.repository.AchievementRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillUpdateService @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val skillSnapshotDao: SkillSnapshotDao,
    private val achievementRepository: AchievementRepository
) {
    companion object {
        private const val TAG = "SkillUpdateService"

        // Base α values by exercise category
        private const val ALPHA_DIAGNOSTIC = 0.25f
        private const val ALPHA_LESSON = 0.08f
        private const val ALPHA_IMPROV = 0.06f
        private const val ALPHA_TONGUE_TWISTER = 0.06f

        // Skill weights: Primary = 1.0, Secondary = 0.5
        private const val PRIMARY = 1.0f
        private const val SECONDARY = 0.5f

        // Daily caps
        private const val DAILY_CAP_EXERCISE = 5f
        private const val DAILY_CAP_DIAGNOSTIC = 15f

        // Warmup bonuses
        private const val WARMUP_INDIVIDUAL_BONUS = 0.4f
        private const val WARMUP_QUICK_BONUS = 0.6f

        // Decay settings
        private const val DECAY_GRACE_DAYS = 2
        private const val DECAY_MILD_RATE = 0.3f   // days 3-7
        private const val DECAY_STRONG_RATE = 0.5f  // after day 7
        private const val DECAY_MILD_END_DAY = 7
        private const val DECAY_FLOOR_RATIO = 0.5f  // 50% of peak
    }

    // Skill indices for mapping
    private enum class Skill { DICTION, TEMPO, INTONATION, VOLUME, CONFIDENCE, FILLER_WORDS, STRUCTURE }

    // Exercise → skill weight mapping
    private val exerciseSkillMap: Map<String, Map<Skill, Float>> = mapOf(
        "reading" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to SECONDARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to SECONDARY, Skill.FILLER_WORDS to SECONDARY
        ),
        "tongue_twister" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to PRIMARY
        ),
        "slow_motion" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to SECONDARY, Skill.VOLUME to SECONDARY
        ),
        "minimal_pairs" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to SECONDARY
        ),
        "emotion_reading" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to PRIMARY,
            Skill.CONFIDENCE to PRIMARY
        ),
        "free_speech" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "storytelling" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "debate" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "sales" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "job_interview" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "presentation" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to PRIMARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "negotiation" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "retelling" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "dialogue" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to SECONDARY
        ),
        "no_hesitation" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to SECONDARY, Skill.CONFIDENCE to PRIMARY,
            Skill.FILLER_WORDS to PRIMARY
        ),
        "metaphor_master" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to SECONDARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        ),
        "emotion_switch" to mapOf(
            Skill.DICTION to SECONDARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to PRIMARY,
            Skill.CONFIDENCE to PRIMARY
        ),
        "speed_round" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to SECONDARY, Skill.CONFIDENCE to PRIMARY,
            Skill.FILLER_WORDS to PRIMARY
        ),
        "character_voice" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to SECONDARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to PRIMARY,
            Skill.CONFIDENCE to PRIMARY
        ),
        "diagnostic" to mapOf(
            Skill.DICTION to PRIMARY, Skill.TEMPO to PRIMARY,
            Skill.INTONATION to PRIMARY, Skill.VOLUME to PRIMARY,
            Skill.CONFIDENCE to PRIMARY, Skill.FILLER_WORDS to PRIMARY,
            Skill.STRUCTURE to PRIMARY
        )
    )

    // Warmup category → affected skills
    private val warmupSkillMap: Map<String, List<Skill>> = mapOf(
        "articulation" to listOf(Skill.DICTION, Skill.TEMPO),
        "breathing" to listOf(Skill.VOLUME, Skill.CONFIDENCE, Skill.TEMPO),
        "voice" to listOf(Skill.INTONATION, Skill.VOLUME, Skill.CONFIDENCE)
    )

    // All skills affected by Quick Warmup (union of all warmup categories)
    private val quickWarmupSkills: List<Skill> = listOf(
        Skill.DICTION, Skill.TEMPO, Skill.INTONATION,
        Skill.VOLUME, Skill.CONFIDENCE
    )

    suspend fun updateFromAnalysis(result: VoiceAnalysisResult, exerciseType: String) {
        val progress = getOrCreateProgress()
        val resetProgress = resetDailyChangesIfNewDay(progress)
        val skillMap = exerciseSkillMap[exerciseType] ?: return
        val baseAlpha = getBaseAlpha(exerciseType)
        val dailyCap = if (exerciseType == "diagnostic") DAILY_CAP_DIAGNOSTIC else DAILY_CAP_EXERCISE

        val aiScores = mapOf(
            Skill.DICTION to result.diction.toFloat(),
            Skill.TEMPO to result.tempo.toFloat(),
            Skill.INTONATION to result.intonation.toFloat(),
            Skill.VOLUME to result.volume.toFloat(),
            Skill.CONFIDENCE to result.confidence.toFloat(),
            Skill.FILLER_WORDS to result.fillerWords.toFloat(),
            Skill.STRUCTURE to result.structure.toFloat()
        )

        var updated = resetProgress
        for ((skill, weight) in skillMap) {
            val aiScore = aiScores[skill] ?: continue
            val effectiveAlpha = baseAlpha * weight
            val currentLevel = getSkillLevel(updated, skill)
            val rawNew = effectiveAlpha * aiScore + (1f - effectiveAlpha) * currentLevel
            val delta = rawNew - currentLevel
            val currentDailyChange = getDailyChange(updated, skill)
            val cappedDelta = capDelta(delta, currentDailyChange, dailyCap)
            val newLevel = (currentLevel + cappedDelta).coerceIn(0f, 100f)

            updated = setSkillLevel(updated, skill, newLevel)
            updated = setDailyChange(updated, skill, currentDailyChange + cappedDelta)
            updated = updatePeakIfNeeded(updated, skill, newLevel)
        }

        // Save last levels for arrows
        updated = updated.copy(
            lastDictionLevel = resetProgress.dictionLevel,
            lastTempoLevel = resetProgress.tempoLevel,
            lastIntonationLevel = resetProgress.intonationLevel,
            lastVolumeLevel = resetProgress.volumeLevel,
            lastStructureLevel = resetProgress.structureLevel,
            lastConfidenceLevel = resetProgress.confidenceLevel,
            lastFillerWordsLevel = resetProgress.fillerWordsLevel,
            updatedAt = System.currentTimeMillis()
        )

        userProgressDao.insertOrUpdate(updated)
        saveDailySnapshotIfNeeded(updated)

        Log.d(TAG, "Updated skills from $exerciseType analysis: " +
                "D=${updated.dictionLevel.fmt()} T=${updated.tempoLevel.fmt()} " +
                "I=${updated.intonationLevel.fmt()} V=${updated.volumeLevel.fmt()} " +
                "C=${updated.confidenceLevel.fmt()} F=${updated.fillerWordsLevel.fmt()} " +
                "S=${updated.structureLevel.fmt()}")

        try { achievementRepository.checkAndUnlock() } catch (e: Exception) {
            Log.e(TAG, "Achievement check failed: ${e.message}")
        }
    }

    suspend fun updateFromWarmup(category: String) {
        val progress = getOrCreateProgress()
        val resetProgress = resetDailyChangesIfNewDay(progress)
        val skills = warmupSkillMap[category] ?: return

        var updated = resetProgress
        for (skill in skills) {
            val currentLevel = getSkillLevel(updated, skill)
            val currentDailyChange = getDailyChange(updated, skill)
            val cappedDelta = capDelta(WARMUP_INDIVIDUAL_BONUS, currentDailyChange, DAILY_CAP_EXERCISE)
            val newLevel = (currentLevel + cappedDelta).coerceIn(0f, 100f)

            updated = setSkillLevel(updated, skill, newLevel)
            updated = setDailyChange(updated, skill, currentDailyChange + cappedDelta)
            updated = updatePeakIfNeeded(updated, skill, newLevel)
        }

        updated = updated.copy(updatedAt = System.currentTimeMillis())
        userProgressDao.insertOrUpdate(updated)

        Log.d(TAG, "Updated skills from warmup ($category): " +
                "affected=${skills.joinToString { it.name }}")

        try { achievementRepository.checkAndUnlock() } catch (e: Exception) {
            Log.e(TAG, "Achievement check failed: ${e.message}")
        }
    }

    suspend fun updateFromQuickWarmup() {
        val progress = getOrCreateProgress()
        val resetProgress = resetDailyChangesIfNewDay(progress)

        var updated = resetProgress
        for (skill in quickWarmupSkills) {
            val currentLevel = getSkillLevel(updated, skill)
            val currentDailyChange = getDailyChange(updated, skill)
            val cappedDelta = capDelta(WARMUP_QUICK_BONUS, currentDailyChange, DAILY_CAP_EXERCISE)
            val newLevel = (currentLevel + cappedDelta).coerceIn(0f, 100f)

            updated = setSkillLevel(updated, skill, newLevel)
            updated = setDailyChange(updated, skill, currentDailyChange + cappedDelta)
            updated = updatePeakIfNeeded(updated, skill, newLevel)
        }

        updated = updated.copy(updatedAt = System.currentTimeMillis())
        userProgressDao.insertOrUpdate(updated)

        Log.d(TAG, "Updated skills from Quick Warmup: " +
                "affected=${quickWarmupSkills.joinToString { it.name }}")

        try { achievementRepository.checkAndUnlock() } catch (e: Exception) {
            Log.e(TAG, "Achievement check failed: ${e.message}")
        }
    }

    suspend fun applyDecayIfNeeded() {
        val progress = getOrCreateProgress()
        val lastActivity = progress.lastActivityDate ?: return
        val lastDate = LocalDate.ofEpochDay(lastActivity / (24 * 60 * 60 * 1000))
        val today = LocalDate.now()
        val daysSince = ChronoUnit.DAYS.between(lastDate, today).toInt()

        if (daysSince <= DECAY_GRACE_DAYS) return

        val decayDays = daysSince - DECAY_GRACE_DAYS
        var totalDecay = 0f
        for (day in 1..decayDays) {
            totalDecay += if (day + DECAY_GRACE_DAYS <= DECAY_MILD_END_DAY) {
                DECAY_MILD_RATE
            } else {
                DECAY_STRONG_RATE
            }
        }

        var updated = progress
        for (skill in Skill.entries) {
            val currentLevel = getSkillLevel(updated, skill)
            val peakLevel = getPeakLevel(updated, skill)
            val floor = peakLevel * DECAY_FLOOR_RATIO
            val newLevel = (currentLevel - totalDecay).coerceIn(floor.coerceAtLeast(1f), 100f)
            updated = setSkillLevel(updated, skill, newLevel)
        }

        updated = updated.copy(updatedAt = System.currentTimeMillis())
        userProgressDao.insertOrUpdate(updated)

        Log.d(TAG, "Applied decay: $daysSince days since last activity, " +
                "total decay=$totalDecay")
    }

    suspend fun saveDailySnapshotIfNeeded(progress: UserProgressEntity? = null) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = skillSnapshotDao.getByDate(today)
        val p = progress ?: getOrCreateProgress()

        val snapshot = SkillSnapshotEntity(
            id = existing?.id ?: UUID.randomUUID().toString(),
            date = today,
            diction = p.dictionLevel,
            tempo = p.tempoLevel,
            intonation = p.intonationLevel,
            volume = p.volumeLevel,
            structure = p.structureLevel,
            confidence = p.confidenceLevel,
            fillerWords = p.fillerWordsLevel
        )
        skillSnapshotDao.insert(snapshot)
    }

    // --- Private helpers ---

    private suspend fun getOrCreateProgress(): UserProgressEntity {
        return userProgressDao.getProgress() ?: UserProgressEntity().also {
            userProgressDao.insertOrUpdate(it)
        }
    }

    private fun resetDailyChangesIfNewDay(progress: UserProgressEntity): UserProgressEntity {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        if (progress.lastDailyResetDate == today) return progress

        return progress.copy(
            dailyChangeDiction = 0f,
            dailyChangeTempo = 0f,
            dailyChangeIntonation = 0f,
            dailyChangeVolume = 0f,
            dailyChangeStructure = 0f,
            dailyChangeConfidence = 0f,
            dailyChangeFillerWords = 0f,
            lastDailyResetDate = today
        )
    }

    private fun getBaseAlpha(exerciseType: String): Float = when (exerciseType) {
        "diagnostic" -> ALPHA_DIAGNOSTIC
        "tongue_twister" -> ALPHA_TONGUE_TWISTER
        "reading", "slow_motion", "minimal_pairs", "emotion_reading" -> ALPHA_LESSON
        else -> ALPHA_IMPROV
    }

    private fun capDelta(delta: Float, currentDailyChange: Float, cap: Float): Float {
        // Positive delta
        if (delta > 0) {
            val room = (cap - currentDailyChange).coerceAtLeast(0f)
            return delta.coerceAtMost(room)
        }
        // Negative delta
        if (delta < 0) {
            val room = (-cap - currentDailyChange).coerceAtMost(0f)
            return delta.coerceAtLeast(room)
        }
        return 0f
    }

    private fun getSkillLevel(p: UserProgressEntity, skill: Skill): Float = when (skill) {
        Skill.DICTION -> p.dictionLevel
        Skill.TEMPO -> p.tempoLevel
        Skill.INTONATION -> p.intonationLevel
        Skill.VOLUME -> p.volumeLevel
        Skill.CONFIDENCE -> p.confidenceLevel
        Skill.FILLER_WORDS -> p.fillerWordsLevel
        Skill.STRUCTURE -> p.structureLevel
    }

    private fun setSkillLevel(p: UserProgressEntity, skill: Skill, value: Float): UserProgressEntity = when (skill) {
        Skill.DICTION -> p.copy(dictionLevel = value)
        Skill.TEMPO -> p.copy(tempoLevel = value)
        Skill.INTONATION -> p.copy(intonationLevel = value)
        Skill.VOLUME -> p.copy(volumeLevel = value)
        Skill.CONFIDENCE -> p.copy(confidenceLevel = value)
        Skill.FILLER_WORDS -> p.copy(fillerWordsLevel = value)
        Skill.STRUCTURE -> p.copy(structureLevel = value)
    }

    private fun getDailyChange(p: UserProgressEntity, skill: Skill): Float = when (skill) {
        Skill.DICTION -> p.dailyChangeDiction
        Skill.TEMPO -> p.dailyChangeTempo
        Skill.INTONATION -> p.dailyChangeIntonation
        Skill.VOLUME -> p.dailyChangeVolume
        Skill.CONFIDENCE -> p.dailyChangeConfidence
        Skill.FILLER_WORDS -> p.dailyChangeFillerWords
        Skill.STRUCTURE -> p.dailyChangeStructure
    }

    private fun setDailyChange(p: UserProgressEntity, skill: Skill, value: Float): UserProgressEntity = when (skill) {
        Skill.DICTION -> p.copy(dailyChangeDiction = value)
        Skill.TEMPO -> p.copy(dailyChangeTempo = value)
        Skill.INTONATION -> p.copy(dailyChangeIntonation = value)
        Skill.VOLUME -> p.copy(dailyChangeVolume = value)
        Skill.CONFIDENCE -> p.copy(dailyChangeConfidence = value)
        Skill.FILLER_WORDS -> p.copy(dailyChangeFillerWords = value)
        Skill.STRUCTURE -> p.copy(dailyChangeStructure = value)
    }

    private fun getPeakLevel(p: UserProgressEntity, skill: Skill): Float = when (skill) {
        Skill.DICTION -> p.peakDictionLevel
        Skill.TEMPO -> p.peakTempoLevel
        Skill.INTONATION -> p.peakIntonationLevel
        Skill.VOLUME -> p.peakVolumeLevel
        Skill.CONFIDENCE -> p.peakConfidenceLevel
        Skill.FILLER_WORDS -> p.peakFillerWordsLevel
        Skill.STRUCTURE -> p.peakStructureLevel
    }

    private fun updatePeakIfNeeded(p: UserProgressEntity, skill: Skill, newLevel: Float): UserProgressEntity {
        val currentPeak = getPeakLevel(p, skill)
        if (newLevel <= currentPeak) return p
        return when (skill) {
            Skill.DICTION -> p.copy(peakDictionLevel = newLevel)
            Skill.TEMPO -> p.copy(peakTempoLevel = newLevel)
            Skill.INTONATION -> p.copy(peakIntonationLevel = newLevel)
            Skill.VOLUME -> p.copy(peakVolumeLevel = newLevel)
            Skill.CONFIDENCE -> p.copy(peakConfidenceLevel = newLevel)
            Skill.FILLER_WORDS -> p.copy(peakFillerWordsLevel = newLevel)
            Skill.STRUCTURE -> p.copy(peakStructureLevel = newLevel)
        }
    }

    private fun Float.fmt(): String = "%.1f".format(this)
}
