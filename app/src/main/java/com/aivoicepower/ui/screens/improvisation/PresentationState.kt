package com.aivoicepower.ui.screens.improvisation

data class PresentationState(
    val isStarted: Boolean = false,
    val currentStepIndex: Int = 0,
    val steps: List<PresentationStep> = emptyList(),
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingIds: List<String> = emptyList()
)

data class PresentationStep(
    val stepNumber: Int,
    val question: String,
    val hint: String
)
