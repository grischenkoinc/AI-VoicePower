package com.aivoicepower.ui.screens.improvisation

sealed class PresentationEvent {
    data class TopicSelected(val topic: String) : PresentationEvent()
    object StartSimulation : PresentationEvent()
    object StartRecording : PresentationEvent()
    object StopRecording : PresentationEvent()
    object FinishPresentation : PresentationEvent()
    object AnalyzeClicked : PresentationEvent()
    object SkipClicked : PresentationEvent()
    object DismissAnalysis : PresentationEvent()
    object CountdownComplete : PresentationEvent()
}
