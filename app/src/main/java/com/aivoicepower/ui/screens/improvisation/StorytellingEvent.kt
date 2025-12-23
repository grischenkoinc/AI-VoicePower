package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.exercise.StoryFormat

sealed class StorytellingEvent {
    data class SelectFormat(val format: StoryFormat) : StorytellingEvent()
    object GenerateNewPrompt : StorytellingEvent()
    object StartPreparation : StorytellingEvent()
    object StartRecording : StorytellingEvent()
    object StopRecording : StorytellingEvent()
    object CompleteTask : StorytellingEvent()
    object ResetToFormatSelection : StorytellingEvent()
}
