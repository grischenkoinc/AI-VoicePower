package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.provider.DailyChallengeProvider
import com.aivoicepower.domain.model.VoiceAnalysisResult

data class DailyChallengeState(
    val challenge: DailyChallengeProvider.DailyChallenge? = null,
    val isLoading: Boolean = true,
    val isPreparationPhase: Boolean = false,
    val preparationTimeLeft: Int = 15,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingId: String? = null,
    val isAnalyzing: Boolean = false,
    val analysisResult: VoiceAnalysisResult? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val error: String? = null,
    // Analysis limits
    val showAnalysisLimitSheet: Boolean = false,
    val isPremium: Boolean = true,
    val remainingImprovAnalyses: Int = Int.MAX_VALUE,
    val remainingAdImprovAnalyses: Int = 0,
    val isAdLoaded: Boolean = false
)
