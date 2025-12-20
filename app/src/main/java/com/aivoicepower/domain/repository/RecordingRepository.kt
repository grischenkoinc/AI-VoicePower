package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.analysis.VoiceAnalysis
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    suspend fun saveRecording(
        filePath: String,
        durationMs: Long,
        type: String,
        contextId: String?,
        exerciseId: String?
    ): String // returns recordingId

    suspend fun saveAnalysis(recordingId: String, analysis: VoiceAnalysis)
    fun getAnalysis(recordingId: String): Flow<VoiceAnalysis?>
    fun getAllRecordings(): Flow<List<VoiceAnalysis>>
}
