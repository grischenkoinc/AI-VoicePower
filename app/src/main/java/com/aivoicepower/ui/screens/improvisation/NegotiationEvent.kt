package com.aivoicepower.ui.screens.improvisation

sealed class NegotiationEvent {
    object StartSimulation : NegotiationEvent()
    object StartRecording : NegotiationEvent()
    object StopRecording : NegotiationEvent()
    object FinishNegotiation : NegotiationEvent()
    object AnalyzeClicked : NegotiationEvent()
    object SkipClicked : NegotiationEvent()
    object DismissAnalysis : NegotiationEvent()
    object CountdownComplete : NegotiationEvent()
}
