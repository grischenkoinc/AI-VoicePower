package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    suspend fun unlockAchievement(achievementId: String)
    suspend fun updateProgress(achievementId: String, progress: Int)
}
