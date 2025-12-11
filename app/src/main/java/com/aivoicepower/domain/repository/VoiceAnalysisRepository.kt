package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.VoiceAnalysis
import java.io.File

interface VoiceAnalysisRepository {
    suspend fun analyzeVoice(audioFile: File, context: String): Result<VoiceAnalysis>
}
