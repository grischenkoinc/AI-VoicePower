package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.analysis.VoiceAnalysis

interface AiCoachRepository {
    suspend fun sendMessage(message: String, conversationHistory: List<Pair<String, String>>): String
    suspend fun analyzeRecording(recordingPath: String, transcription: String): VoiceAnalysis
}
