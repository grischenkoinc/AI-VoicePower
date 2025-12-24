package com.aivoicepower.data.repository

import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementType
import com.aivoicepower.domain.repository.AchievementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of AchievementRepository
 *
 * TODO Phase 7: Implement with AchievementDao
 */
@Singleton
class AchievementRepositoryImpl @Inject constructor() : AchievementRepository {

    private val _unlockedAchievementIds = MutableStateFlow(setOf("first_steps", "week_warrior"))

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return MutableStateFlow(getMockAchievements())
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        val unlocked = getMockAchievements().filter {
            _unlockedAchievementIds.value.contains(it.id)
        }
        return MutableStateFlow(unlocked)
    }

    override suspend fun unlockAchievement(achievementId: String) {
        _unlockedAchievementIds.value = _unlockedAchievementIds.value + achievementId
    }

    private fun getMockAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_steps",
                type = AchievementType.FIRST_COURSE,
                title = "Перші кроки",
                description = "Завершіть першу вправу",
                iconRes = 0,
                unlockedAt = System.currentTimeMillis(),
                progress = null,
                target = null,
                icon = "🎯",
                tier = "bronze"
            ),
            Achievement(
                id = "week_warrior",
                type = AchievementType.STREAK_7,
                title = "Тиждень підряд",
                description = "Займайтеся 7 днів поспіль",
                iconRes = 0,
                unlockedAt = System.currentTimeMillis(),
                progress = 7,
                target = 7,
                icon = "🔥",
                tier = "silver"
            ),
            Achievement(
                id = "pro_speaker",
                type = AchievementType.IMPROVISER_50,
                title = "Професіонал",
                description = "Виконайте 100 вправ",
                iconRes = 0,
                unlockedAt = null,
                progress = 65,
                target = 100,
                icon = "🏆",
                tier = "gold"
            )
        )
    }
}

/**
 * Hilt module for AchievementRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AchievementRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository
}
