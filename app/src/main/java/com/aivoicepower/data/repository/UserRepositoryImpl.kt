package com.aivoicepower.data.repository

import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    // In-memory storage (will be replaced with DataStore/Room in production)
    private val userProfileFlow = MutableStateFlow<UserProfile?>(null)
    private val userProgressFlow = MutableStateFlow<UserProgress?>(createDefaultProgress())
    private val diagnosticResults = MutableStateFlow<List<DiagnosticResult>>(emptyList())

    override fun getUserProfile(): Flow<UserProfile?> = userProfileFlow

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileFlow.value = profile
    }

    override fun getUserProgress(): Flow<UserProgress?> = userProgressFlow

    override suspend fun updateUserProgress(progress: UserProgress) {
        userProgressFlow.value = progress
    }

    override fun getDiagnosticResults(): Flow<List<DiagnosticResult>> = diagnosticResults

    override suspend fun saveDiagnosticResult(result: DiagnosticResult) {
        val current = diagnosticResults.value.toMutableList()
        current.add(result)
        diagnosticResults.value = current
    }

    override fun getInitialDiagnostic(): Flow<DiagnosticResult?> {
        return diagnosticResults.map { list ->
            list.firstOrNull { it.isInitial }
        }
    }

    override fun getLatestDiagnostic(): Flow<DiagnosticResult?> {
        return diagnosticResults.map { list ->
            list.maxByOrNull { it.timestamp }
        }
    }

    private fun createDefaultProgress(): UserProgress {
        return UserProgress(
            userId = "default",
            currentStreak = 0,
            longestStreak = 0,
            totalMinutes = 0,
            totalExercises = 0,
            totalRecordings = 0,
            lastActivityDate = null,
            skillLevels = SkillType.entries.associateWith { 0 },
            achievements = emptyList()
        )
    }
}
