package com.aivoicepower.ui.screens.improvisation

sealed class JobInterviewEvent {
    object StartSimulation : JobInterviewEvent()
    object StartRecording : JobInterviewEvent()
    object StopRecording : JobInterviewEvent()
    object NextStep : JobInterviewEvent()
}
