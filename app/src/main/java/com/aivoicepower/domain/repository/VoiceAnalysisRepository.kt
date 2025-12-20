package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.analysis.VoiceAnalysis
import java.io.File

// Using old VoiceAnalysis structure for compatibility - will migrate to NewVoiceAnalysis in future phases
interface VoiceAnalysisRepository {
    suspend fun analyzeVoice(audioFile: File, context: String): Result<VoiceAnalysis>
}
