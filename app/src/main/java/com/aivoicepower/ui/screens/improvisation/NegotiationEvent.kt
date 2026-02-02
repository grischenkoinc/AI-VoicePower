package com.aivoicepower.ui.screens.improvisation

sealed class NegotiationEvent {
    object StartSimulation : NegotiationEvent()
    object StartRecording : NegotiationEvent()
    object StopRecording : NegotiationEvent()
    object NextStep : NegotiationEvent()
}
