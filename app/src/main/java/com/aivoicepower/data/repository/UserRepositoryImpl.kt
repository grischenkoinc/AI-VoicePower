package com.aivoicepower.data.repository

import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.UserGoal
import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of UserRepository
 *
 * TODO Phase 1-2: Implement with UserProfileDao and UserProgressDao
 */
@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val _userProfile = MutableStateFlow(getMockUserProfile())
    private val _userProgress = MutableStateFlow(getMockUserProgress())

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
    }

    override fun getInitialDiagnostic(): Flow<DiagnosticResult?> {
        // Stub implementation - return mock initial diagnostic
        return flowOf(
            DiagnosticResult(
                id = "initial_diagnostic_1",
                userId = "default_user",
                timestamp = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // 30 days ago
                diction = 42,
                tempo = 48,
                intonation = 38,
                volume = 52,
                structure = 35,
                confidence = 40,
                fillerWords = 45,
                recordingIds = emptyList(),
                recommendations = listOf(
                    "Попрацюйте над структурою мовлення",
                    "Розвивайте інтонаційну виразність",
                    "Покращуйте впевненість у голосі"
                ),
                isInitial = true
            )
        )
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

    private fun getMockUserProgress(): UserProgress {
        return UserProgress(
            userId = "default_user",
            currentStreak = 3,
            longestStreak = 7,
            totalMinutes = 120,
            totalExercises = 25,
            totalRecordings = 15,
            lastActivityDate = System.currentTimeMillis(),
            skillLevels = mapOf(
                SkillType.DICTION to 45,
                SkillType.TEMPO to 50,
                SkillType.INTONATION to 40,
                SkillType.VOLUME to 55,
                SkillType.STRUCTURE to 35,
                SkillType.CONFIDENCE to 42,
                SkillType.FILLER_WORDS to 48
            ),
            achievements = emptyList()
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
