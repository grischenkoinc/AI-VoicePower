package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.exercise.StoryFormat

data class StorytellingState(
    val selectedFormat: StoryFormat? = null,
    val storyPrompt: String = "",
    val isPreparationPhase: Boolean = false,
    val preparationTimeLeft: Int = 30, // 30 секунд підготовки
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingId: String? = null,
    val error: String? = null
)
