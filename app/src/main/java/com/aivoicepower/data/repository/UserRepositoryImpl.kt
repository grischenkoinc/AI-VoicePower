package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.UserGoal
import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.repository.UserRepository
import com.aivoicepower.domain.service.SkillUpdateService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of UserRepository
 *
 * TODO Phase 1-2: Implement with UserProfileDao and UserProgressDao
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val recordingDao: com.aivoicepower.data.local.database.dao.RecordingDao,
    private val userProgressDao: UserProgressDao,
    private val skillUpdateService: SkillUpdateService
) : UserRepository {

    private val _userProfile = MutableStateFlow(getMockUserProfile())
    private val _userProgress = MutableStateFlow<UserProgress?>(null)

    init {
        Log.d("UserRepository", "UserRepositoryImpl initialized")
        CoroutineScope(Dispatchers.IO).launch {
            skillUpdateService.applyDecayIfNeeded()
            // Initial load
            _userProgress.value = calculateRealUserProgress()
            Log.d("UserRepository", "Initial progress loaded")
        }
        // Reactive: update whenever skills change in DB
        CoroutineScope(Dispatchers.IO).launch {
            userProgressDao.getProgressFlow().collect {
                _userProgress.value = calculateRealUserProgress()
            }
        }
    }

    override fun getUserProfile(): Flow<UserProfile?> {
        return _userProfile
    }

    override fun getUserProgress(): Flow<UserProgress?> {
        return _userProgress
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    override suspend fun updateUserProgress(progress: UserProgress) {
        _userProgress.value = progress
        // Recalculate with real data after update
        _userProgress.value = calculateRealUserProgress()
    }

    /**
     * Calculates real user progress from recordings database
     */
    private suspend fun calculateRealUserProgress(): UserProgress {
        val allRecordings = recordingDao.getAllRecordings().first()
        Log.d("UserRepository", "calculateRealUserProgress: Found ${allRecordings.size} total recordings")

        // Calculate total statistics
        val totalRecordings = allRecordings.size
        val totalMinutes = (allRecordings.sumOf { it.durationMs } / 60000).toInt()
        Log.d("UserRepository", "calculateRealUserProgress: totalRecordings=$totalRecordings, totalMinutes=$totalMinutes")

        // Count unique exercises (by exerciseId)
        val totalExercises = allRecordings.mapNotNull { it.exerciseId }.distinct().size

        // Calculate streaks
        val (currentStreak, longestStreak) = calculateStreaks(allRecordings)

        // Get last activity date
        val lastActivityDate = allRecordings.maxOfOrNull { it.createdAt } ?: System.currentTimeMillis()

        // Get real skill levels from DB
        val dbProgress = userProgressDao.getProgress()
        val skillLevels = if (dbProgress != null) {
            mapOf(
                SkillType.DICTION to dbProgress.dictionLevel.toInt(),
                SkillType.TEMPO to dbProgress.tempoLevel.toInt(),
                SkillType.INTONATION to dbProgress.intonationLevel.toInt(),
                SkillType.VOLUME to dbProgress.volumeLevel.toInt(),
                SkillType.STRUCTURE to dbProgress.structureLevel.toInt(),
                SkillType.CONFIDENCE to dbProgress.confidenceLevel.toInt(),
                SkillType.FILLER_WORDS to dbProgress.fillerWordsLevel.toInt()
            )
        } else {
            mapOf(
                SkillType.DICTION to 1,
                SkillType.TEMPO to 1,
                SkillType.INTONATION to 1,
                SkillType.VOLUME to 1,
                SkillType.STRUCTURE to 1,
                SkillType.CONFIDENCE to 1,
                SkillType.FILLER_WORDS to 1
            )
        }

        return UserProgress(
            userId = "default_user",
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalMinutes = totalMinutes,
            totalExercises = totalExercises,
            totalRecordings = totalRecordings,
            lastActivityDate = lastActivityDate,
            skillLevels = skillLevels,
            achievements = emptyList()
        )
    }

    /**
     * Calculates current and longest streak from recordings
     */
    private fun calculateStreaks(recordings: List<com.aivoicepower.data.local.database.entity.RecordingEntity>): Pair<Int, Int> {
        if (recordings.isEmpty()) return Pair(0, 0)

        // Group recordings by date
        val datesWithActivity = recordings.map { recording ->
            val date = java.util.Date(recording.createdAt)
            val calendar = java.util.Calendar.getInstance().apply {
                time = date
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            java.time.LocalDate.of(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }.distinct().sorted()

        // Calculate current streak
        var currentStreak = 0
        val today = java.time.LocalDate.now()
        var checkDate = today

        while (datesWithActivity.contains(checkDate)) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        }

        // If no activity today, check if there was activity yesterday
        if (currentStreak == 0 && datesWithActivity.contains(today.minusDays(1))) {
            currentStreak = 1
            checkDate = today.minusDays(2)
            while (datesWithActivity.contains(checkDate)) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            }
        }

        // Calculate longest streak
        var longestStreak = 0
        var tempStreak = 1

        for (i in 1 until datesWithActivity.size) {
            val prevDate = datesWithActivity[i - 1]
            val currDate = datesWithActivity[i]

            if (currDate == prevDate.plusDays(1)) {
                tempStreak++
            } else {
                longestStreak = maxOf(longestStreak, tempStreak)
                tempStreak = 1
            }
        }
        longestStreak = maxOf(longestStreak, tempStreak)

        return Pair(currentStreak, longestStreak)
    }

    override fun getInitialDiagnostic(): Flow<DiagnosticResult?> {
        return flowOf(null)
    }

    override suspend fun getWeeklyActivity(): List<com.aivoicepower.ui.screens.progress.DailyProgress> {
        val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        val recordings = recordingDao.getRecordingsSince(sevenDaysAgo)

        Log.d("UserRepository", "getWeeklyActivity: Found ${recordings.size} recordings in last 7 days")

        // Group recordings by date
        val recordingsByDate = recordings.groupBy { recording ->
            val date = java.util.Date(recording.createdAt)
            val calendar = java.util.Calendar.getInstance().apply {
                time = date
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            java.time.LocalDate.of(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }

        // Generate list for all 7 days (including days with no activity)
        val result = (0..6).map { daysAgo ->
            val date = java.time.LocalDate.now().minusDays(daysAgo.toLong())
            val dayRecordings = recordingsByDate[date] ?: emptyList()
            val totalDurationMs = dayRecordings.sumOf { it.durationMs }
            val minutes = (totalDurationMs / 60000).toInt()

            Log.d("UserRepository", "Day $date: ${dayRecordings.size} recordings, ${totalDurationMs}ms total, $minutes minutes")

            com.aivoicepower.ui.screens.progress.DailyProgress(
                date = date.toString(),
                exercises = dayRecordings.size,
                minutes = minutes
            )
        }.reversed()

        Log.d("UserRepository", "getWeeklyActivity: Returning ${result.size} days, total minutes: ${result.sumOf { it.minutes }}")

        return result
    }

    private fun getMockUserProfile(): UserProfile {
        return UserProfile(
            id = "default_user",
            name = "Користувач",
            goal = UserGoal.PUBLIC_SPEAKING,
            dailyMinutes = 15,
            isPremium = false,
            createdAt = System.currentTimeMillis()
        )
    }

}

/**
 * Hilt module for UserRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
