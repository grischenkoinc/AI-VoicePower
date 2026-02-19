package com.aivoicepower.ui.screens.improvisation

sealed class JobInterviewEvent {
    data class ProfessionSelected(val profession: String) : JobInterviewEvent()
    object StartSimulation : JobInterviewEvent()
    object StartRecording : JobInterviewEvent()
    object StopRecording : JobInterviewEvent()
    object FinishInterview : JobInterviewEvent()
    object AnalyzeClicked : JobInterviewEvent()
    object SkipClicked : JobInterviewEvent()
    object DismissAnalysis : JobInterviewEvent()
    object CountdownComplete : JobInterviewEvent()
}
