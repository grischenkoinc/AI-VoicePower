package com.aivoicepower.ui.screens.improvisation

data class JobInterviewState(
    val isStarted: Boolean = false,
    val currentStepIndex: Int = 0,
    val steps: List<InterviewStep> = emptyList(),
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingIds: List<String> = emptyList()
)

data class InterviewStep(
    val stepNumber: Int,
    val question: String,
    val hint: String
)
