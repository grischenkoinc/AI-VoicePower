package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.VoiceAnalysis
import com.aivoicepower.domain.model.VoiceAnalysisResult
import java.io.File

interface VoiceAnalysisRepository {
    suspend fun analyzeVoice(audioFile: File, context: String): Result<VoiceAnalysis>

    /**
     * Аналізує аудіо запис та повертає детальні метрики
     */
    suspend fun analyzeRecording(
        audioFilePath: String,
        expectedText: String? = null,
        exerciseType: String,
        context: String? = null
    ): Result<VoiceAnalysisResult>
}
