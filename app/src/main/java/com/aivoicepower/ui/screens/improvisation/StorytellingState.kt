package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.exercise.StoryFormat

data class StorytellingState(
    val selectedFormat: StoryFormat? = null,
    val storyPrompt: String = "",
    val isPreparationPhase: Boolean = false,
    val preparationTimeLeft: Int = 15,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingId: String? = null,
    val isAnalyzing: Boolean = false,
    val analysisResult: VoiceAnalysisResult? = null,
    val error: String? = null,
    // Analysis limits
    val showAnalysisLimitSheet: Boolean = false,
    val isPremium: Boolean = true,
    val remainingImprovAnalyses: Int = Int.MAX_VALUE,
    val remainingAdImprovAnalyses: Int = 0,
    val isAdLoaded: Boolean = false
)
