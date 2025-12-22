package com.aivoicepower.ui.screens.results

import com.aivoicepower.domain.model.exercise.Exercise

data class ResultsState(
    val recordingId: String = "",
    val recording: RecordingInfo? = null,
    val exercise: Exercise? = null,
    val analysis: AnalysisResult? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class RecordingInfo(
    val id: String,
    val filePath: String,
    val durationMs: Long,
    val createdAt: Long,
    val exerciseTitle: String,
    val exerciseType: String
)

data class AnalysisResult(
    val isAnalyzed: Boolean,
    val overallScore: Int?,
    val feedback: FeedbackData?
)

data class FeedbackData(
    val summary: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val tip: String
)
