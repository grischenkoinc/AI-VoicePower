package com.aivoicepower.domain.model

data class VoiceAnalysis(
    val transcription: String,
    val overallScore: Float, // 0-100
    val metrics: VoiceMetrics,
    val feedback: String,
    val recommendations: List<String>,
    val strengths: List<String>
)

data class VoiceMetrics(
    val clarity: Float, // 0-100
    val pace: Float, // words per minute
    val volume: Float, // 0-100
    val pronunciation: Float, // 0-100
    val pauseQuality: Float, // 0-100
    val fillerWordsCount: Int
)
