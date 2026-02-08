package com.aivoicepower.domain.service

import android.util.Log
import com.aivoicepower.data.local.database.dao.AchievementDao
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.AchievementEntity
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementDefinitions
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementChecker @Inject constructor(
    private val achievementDao: AchievementDao,
    private val userProgressDao: UserProgressDao,
    private val recordingDao: RecordingDao,
    private val diagnosticResultDao: DiagnosticResultDao,
    private val skillSnapshotDao: SkillSnapshotDao,
    private val courseProgressDao: CourseProgressDao
) {
    companion object {
        private const val TAG = "AchievementChecker"
    }

    suspend fun seedAchievements() {
        val entities = AchievementDefinitions.all.map { AchievementEntity(id = it.id) }
        achievementDao.insertAll(entities)
    }

    suspend fun checkAll(): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        for (def in AchievementDefinitions.all) {
            val existing = achievementDao.getById(def.id)
            if (existing?.unlockedAt != null) continue

            val isConditionMet = checkCondition(def.id)
            if (isConditionMet) {
                achievementDao.unlock(def.id)
                val updated = achievementDao.getById(def.id)
                newlyUnlocked.add(
                    Achievement(
                        id = def.id,
                        category = def.category,
                        title = def.title,
                        description = def.description,
                        icon = def.icon,
                        unlockedAt = updated?.unlockedAt,
                        progress = def.target,
                        target = def.target
                    )
                )
                Log.d(TAG, "Achievement unlocked: ${def.title}")
            }
        }

        return newlyUnlocked
    }

    private suspend fun checkCondition(id: String): Boolean {
        return try {
            when (id) {
                // Streaks
                "streak_3" -> checkStreak(3)
                "streak_7" -> checkStreak(7)
                "streak_14" -> checkStreak(14)
                "streak_30" -> checkStreak(30)
                "streak_60" -> checkStreak(60)
                "streak_100" -> checkStreak(100)

                // Practice time
                "time_1h" -> checkPracticeMinutes(60)
                "time_5h" -> checkPracticeMinutes(300)
                "time_10h" -> checkPracticeMinutes(600)
                "time_25h" -> checkPracticeMinutes(1500)
                "time_50h" -> checkPracticeMinutes(3000)

                // Recordings count
                "rec_1" -> checkRecordingCount(1)
                "rec_10" -> checkRecordingCount(10)
                "rec_25" -> checkRecordingCount(25)
                "rec_50" -> checkRecordingCount(50)
                "rec_100" -> checkRecordingCount(100)

                // Special
                "first_diagnostic" -> checkDiagnostic()
                "all_skills_40" -> checkAllSkillsAbove(40)
                "all_skills_60" -> checkAllSkillsAbove(60)
                "breakthrough" -> checkBreakthrough()
                "early_bird" -> checkEarlyBird()
                "night_owl" -> checkNightOwl()
                "marathon" -> checkMarathon()
                "polyglot" -> checkPolyglot()
                "productive_day" -> checkProductiveDay()
                "summit" -> checkAnySkillAbove(90)

                // Course completion
                "course_1_done" -> checkCourseCompletion("course_1")
                "course_2_done" -> checkCourseCompletion("course_2")
                "course_3_done" -> checkCourseCompletion("course_3")
                "course_4_done" -> checkCourseCompletion("course_4")
                "course_5_done" -> checkCourseCompletion("course_5")
                "course_6_done" -> checkCourseCompletion("course_6")
                "course_7_done" -> checkCourseCompletion("course_7")

                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking condition for $id: ${e.message}")
            false
        }
    }

    private suspend fun checkStreak(required: Int): Boolean {
        val progress = userProgressDao.getProgress() ?: return false
        return progress.currentStreak >= required || progress.longestStreak >= required
    }

    /**
     * Returns only valid recordings: analyzed + minimum 2 seconds duration.
     * Excludes empty/silent recordings and failed analyses.
     */
    private suspend fun getValidRecordings(): List<com.aivoicepower.data.local.database.entity.RecordingEntity> {
        return recordingDao.getAllRecordings().first()
            .filter { it.durationMs >= 2000 && it.isAnalyzed }
    }

    private suspend fun checkPracticeMinutes(required: Int): Boolean {
        val recordings = getValidRecordings()
        val totalMinutes = (recordings.sumOf { it.durationMs } / 60000).toInt()
        return totalMinutes >= required
    }

    private suspend fun checkRecordingCount(required: Int): Boolean {
        val recordings = getValidRecordings()
        return recordings.size >= required
    }

    private suspend fun checkDiagnostic(): Boolean {
        val diagnostic = diagnosticResultDao.getInitialDiagnostic().first()
        return diagnostic != null
    }

    private suspend fun checkAllSkillsAbove(threshold: Int): Boolean {
        val progress = userProgressDao.getProgress() ?: return false
        return progress.dictionLevel > threshold &&
                progress.tempoLevel > threshold &&
                progress.intonationLevel > threshold &&
                progress.volumeLevel > threshold &&
                progress.structureLevel > threshold &&
                progress.confidenceLevel > threshold &&
                progress.fillerWordsLevel > threshold
    }

    private suspend fun checkAnySkillAbove(threshold: Int): Boolean {
        val progress = userProgressDao.getProgress() ?: return false
        return progress.dictionLevel >= threshold ||
                progress.tempoLevel >= threshold ||
                progress.intonationLevel >= threshold ||
                progress.volumeLevel >= threshold ||
                progress.structureLevel >= threshold ||
                progress.confidenceLevel >= threshold ||
                progress.fillerWordsLevel >= threshold
    }

    private suspend fun checkBreakthrough(): Boolean {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(7)
        val snapshots = skillSnapshotDao.getByDateRange(
            weekAgo.format(DateTimeFormatter.ISO_LOCAL_DATE),
            today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        ).first()

        if (snapshots.size < 2) return false

        val oldest = snapshots.first()
        val newest = snapshots.last()

        return (newest.diction - oldest.diction >= 20f) ||
                (newest.tempo - oldest.tempo >= 20f) ||
                (newest.intonation - oldest.intonation >= 20f) ||
                (newest.volume - oldest.volume >= 20f) ||
                (newest.structure - oldest.structure >= 20f) ||
                (newest.confidence - oldest.confidence >= 20f) ||
                (newest.fillerWords - oldest.fillerWords >= 20f)
    }

    private suspend fun checkEarlyBird(): Boolean {
        val recordings = getValidRecordings()
        return recordings.any { recording ->
            val cal = Calendar.getInstance().apply { timeInMillis = recording.createdAt }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            val totalMinutes = hour * 60 + minute
            totalMinutes in 270..479 // 4:30 (270) to 7:59 (479)
        }
    }

    private suspend fun checkNightOwl(): Boolean {
        val recordings = getValidRecordings()
        return recordings.any { recording ->
            val cal = Calendar.getInstance().apply { timeInMillis = recording.createdAt }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            val totalMinutes = hour * 60 + minute
            totalMinutes >= 1320 || totalMinutes < 270 // 22:00 (1320) to 4:29 (269)
        }
    }

    private suspend fun checkMarathon(): Boolean {
        val recordings = getValidRecordings()
        return recordings.any { it.durationMs >= 5 * 60 * 1000 } // 5 minutes
    }

    private suspend fun checkPolyglot(): Boolean {
        val recordings = getValidRecordings()
        val uniqueTypes = recordings.map { it.type }.distinct().size
        val uniqueContexts = recordings.map { it.contextId }.distinct().size
        return (uniqueTypes + uniqueContexts) >= 8 || uniqueContexts >= 8
    }

    private suspend fun checkProductiveDay(): Boolean {
        val recordings = getValidRecordings()
        val countsByDate = recordings.groupBy { recording ->
            val cal = Calendar.getInstance().apply { timeInMillis = recording.createdAt }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        }
        return countsByDate.any { it.value.size >= 5 }
    }

    private suspend fun checkCourseCompletion(courseId: String): Boolean {
        val completedCourseIds = courseProgressDao.getFullyCompletedCourseIds().first()
        return courseId in completedCourseIds
    }

    // Helper to get current progress for a specific achievement (for progress bars)
    suspend fun getProgress(achievementId: String): Int? {
        return try {
            when {
                achievementId.startsWith("streak_") -> {
                    val progress = userProgressDao.getProgress()
                    maxOf(progress?.currentStreak ?: 0, progress?.longestStreak ?: 0)
                }
                achievementId.startsWith("time_") -> {
                    val recordings = getValidRecordings()
                    (recordings.sumOf { it.durationMs } / 60000).toInt()
                }
                achievementId.startsWith("rec_") -> {
                    val recordings = getValidRecordings()
                    recordings.size
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
