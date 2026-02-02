package com.aivoicepower.ui.screens.improvisation

sealed class PresentationEvent {
    object StartSimulation : PresentationEvent()
    object StartRecording : PresentationEvent()
    object StopRecording : PresentationEvent()
    object NextStep : PresentationEvent()
}
