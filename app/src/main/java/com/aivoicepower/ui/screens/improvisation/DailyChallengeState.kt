package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.provider.DailyChallengeProvider

data class DailyChallengeState(
    val challenge: DailyChallengeProvider.DailyChallenge? = null,
    val isLoading: Boolean = true,
    val isPreparationPhase: Boolean = false,
    val preparationTimeLeft: Int = 20, // 20 секунд підготовки
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingId: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val error: String? = null
)
