package com.aivoicepower.domain.model.analysis

// New domain model from Phase 0.5 specification
// Will replace the old VoiceAnalysis in future phases
data class NewVoiceAnalysis(
    val id: String,
    val recordingId: String,
    val timestamp: Long,
    val transcription: String?,
    val metrics: AnalysisMetrics,
    val feedback: AiFeedback,
    val wordsPerMinute: Int?,
    val fillerWordsCount: Map<String, Int>?
) {
    /**
     * Загальна кількість слів-паразитів
     */
    val totalFillerWords: Int
        get() = fillerWordsCount?.values?.sum() ?: 0
}

// Old VoiceAnalysis structure for compatibility with existing code
data class VoiceAnalysis(
    val transcription: String,
    val overallScore: Float, // 0-100
    val metrics: VoiceMetrics,
    val feedback: String,
    val recommendations: List<String>,
    val strengths: List<String>
)
