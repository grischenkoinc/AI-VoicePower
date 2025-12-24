package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.Achievement
import kotlinx.coroutines.flow.Flow

/**
 * Repository for achievements data
 *
 * TODO: Implement with real data source in Phase 7
 */
interface AchievementRepository {
    /**
     * Get all achievements
     */
    fun getAllAchievements(): Flow<List<Achievement>>

    /**
     * Get unlocked achievements
     */
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    /**
     * Unlock achievement
     */
    suspend fun unlockAchievement(achievementId: String)
}
