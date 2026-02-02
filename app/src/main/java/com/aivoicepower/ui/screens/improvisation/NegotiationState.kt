package com.aivoicepower.ui.screens.improvisation

data class NegotiationState(
    val isStarted: Boolean = false,
    val currentStepIndex: Int = 0,
    val steps: List<NegotiationStep> = emptyList(),
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingIds: List<String> = emptyList()
)

data class NegotiationStep(
    val stepNumber: Int,
    val question: String,
    val hint: String
)
