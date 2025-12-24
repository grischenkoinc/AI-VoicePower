package com.aivoicepower.domain.model.analysis

// Compatibility class for old code - VoiceMetrics is now defined in VoiceAnalysis.kt
data class VoiceMetrics(
    val clarity: Float, // 0-100
    val pace: Float, // words per minute
    val volume: Float, // 0-100
    val pronunciation: Float, // 0-100
    val pauseQuality: Float, // 0-100
    val fillerWordsCount: Int
)
