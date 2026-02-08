package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AchievementRepository {
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    suspend fun checkAndUnlock(): List<Achievement>
    val pendingCelebrations: SharedFlow<Achievement>
    suspend fun dismissCelebration()
}
