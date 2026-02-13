package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.ImprovisationTopic
import com.aivoicepower.domain.model.VoiceAnalysisResult

data class RandomTopicState(
    val currentTopic: ImprovisationTopic? = null,
    val preparationTimeLeft: Int = 15,
    val isPreparationPhase: Boolean = true,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingPath: String? = null,
    val recordingId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Analysis
    val isAnalyzing: Boolean = false,
    val analysisResult: VoiceAnalysisResult? = null,
    // Analysis limits
    val showAnalysisLimitSheet: Boolean = false,
    val isPremium: Boolean = true,
    val remainingImprovAnalyses: Int = Int.MAX_VALUE,
    val remainingAdImprovAnalyses: Int = 0,
    val isAdLoaded: Boolean = false
)
