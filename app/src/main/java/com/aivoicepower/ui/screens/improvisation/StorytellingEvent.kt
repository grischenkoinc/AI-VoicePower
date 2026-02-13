package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.exercise.StoryFormat

sealed class StorytellingEvent {
    data class SelectFormat(val format: StoryFormat) : StorytellingEvent()
    object GenerateNewPrompt : StorytellingEvent()
    object StartPreparation : StorytellingEvent()
    object SkipPreparation : StorytellingEvent()
    object StartRecording : StorytellingEvent()
    object StopRecording : StorytellingEvent()
    object CompleteTask : StorytellingEvent()
    object DismissAnalysis : StorytellingEvent()
    object ResetToFormatSelection : StorytellingEvent()
    object DismissAnalysisLimitSheet : StorytellingEvent()
    object WatchAdForAnalysis : StorytellingEvent()
    object ContinueWithoutAnalysis : StorytellingEvent()
}
