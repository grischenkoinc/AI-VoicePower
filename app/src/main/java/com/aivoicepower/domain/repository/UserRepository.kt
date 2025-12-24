package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user profile and progress data
 *
 * TODO: Implement with real data source in Phase 1-2
 */
interface UserRepository {
    /**
     * Get user profile
     */
    fun getUserProfile(): Flow<UserProfile?>

    /**
     * Get user progress
     */
    fun getUserProgress(): Flow<UserProgress?>

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(profile: UserProfile)

    /**
     * Update user progress
     */
    suspend fun updateUserProgress(progress: UserProgress)

    /**
     * Get initial diagnostic result
     */
    fun getInitialDiagnostic(): Flow<DiagnosticResult?>
}
