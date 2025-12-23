package com.aivoicepower.ui.screens.aicoach

import com.aivoicepower.domain.model.chat.Message

data class AiCoachState(
    val messages: List<Message> = emptyList(),
    val quickActions: List<String> = emptyList(),
    val templates: List<ConversationTemplate> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isListening: Boolean = false,
    val isUploadingAudio: Boolean = false,
    val activeSimulation: SimulationScenario? = null,
    val simulationStep: Int = 0,
    val showScenarioDialog: Boolean = false,
    val error: String? = null,
    val messagesRemaining: Int = 10,
    val isPremium: Boolean = false,
    val canSendMessage: Boolean = true
)

data class ConversationTemplate(
    val id: String,
    val title: String,
    val emoji: String,
    val initialMessage: String
)

data class SimulationScenario(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<SimulationStep>
)

data class SimulationStep(
    val stepNumber: Int,
    val aiPrompt: String,
    val userHint: String
)
