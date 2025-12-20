package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.model.user.DiagnosticResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)

    fun getUserProgress(): Flow<UserProgress?>
    suspend fun updateUserProgress(progress: UserProgress)

    fun getDiagnosticResults(): Flow<List<DiagnosticResult>>
    suspend fun saveDiagnosticResult(result: DiagnosticResult)
    fun getInitialDiagnostic(): Flow<DiagnosticResult?>
    fun getLatestDiagnostic(): Flow<DiagnosticResult?>
}
