package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.ImprovisationTopic

data class RandomTopicState(
    val currentTopic: ImprovisationTopic? = null,
    val preparationTimeLeft: Int = 15,
    val isPreparationPhase: Boolean = true,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingPath: String? = null,
    val recordingId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
