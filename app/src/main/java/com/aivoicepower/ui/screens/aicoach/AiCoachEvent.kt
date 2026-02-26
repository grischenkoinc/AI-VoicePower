package com.aivoicepower.ui.screens.aicoach

import android.net.Uri

sealed class AiCoachEvent {
    data class InputChanged(val text: String) : AiCoachEvent()
    object SendMessageClicked : AiCoachEvent()
    data class QuickActionClicked(val action: String) : AiCoachEvent()
    object ClearConversationClicked : AiCoachEvent()
    object ErrorDismissed : AiCoachEvent()

    // Voice Input
    object StartVoiceInput : AiCoachEvent()
    object StopVoiceInput : AiCoachEvent()
    data class VoiceInputTranscribed(val text: String) : AiCoachEvent()

    // Audio Upload
    object UploadAudioClicked : AiCoachEvent()
    data class AudioFileSelected(val uri: Uri) : AiCoachEvent()

    // Simulations
    object ShowScenarioDialog : AiCoachEvent()
    object HideScenarioDialog : AiCoachEvent()
    data class StartSimulation(val scenario: SimulationScenario) : AiCoachEvent()
    object NextSimulationStep : AiCoachEvent()
    object ExitSimulation : AiCoachEvent()

    // Export
    object ExportConversation : AiCoachEvent()

    // Templates
    data class ApplyTemplate(val template: ConversationTemplate) : AiCoachEvent()
}
