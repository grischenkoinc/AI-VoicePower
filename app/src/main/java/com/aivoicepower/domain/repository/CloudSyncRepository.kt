package com.aivoicepower.domain.repository

import kotlinx.coroutines.flow.Flow

sealed class SyncState {
    object Idle : SyncState()
    data class Syncing(val progress: Float) : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}

interface CloudSyncRepository {
    val syncState: Flow<SyncState>

    suspend fun syncUserProfile(): Result<Unit>
    suspend fun syncUserProgress(): Result<Unit>
    suspend fun syncRecordings(): Result<Unit>
    suspend fun syncCourseProgress(): Result<Unit>
    suspend fun syncAchievements(): Result<Unit>
    suspend fun fullSync(): Result<Unit>
    suspend fun uploadRecording(localPath: String, recordingId: String): Result<String>
    suspend fun downloadRecording(cloudUrl: String, localPath: String): Result<Unit>
}
