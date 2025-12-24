package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DebateTopicsProvider

data class DebateState(
    val selectedTopic: DebateTopicsProvider.DebateTopic? = null,
    val userPosition: DebatePosition? = null,
    val phase: DebatePhase = DebatePhase.TopicSelection,
    val currentRound: Int = 1,
    val maxRounds: Int = 5,
    val rounds: List<DebateRound> = emptyList(),
    val isRecording: Boolean = false,
    val isAiThinking: Boolean = false,
    val recordingSeconds: Int = 0,
    val maxRecordingSeconds: Int = 60,
    val error: String? = null
)

enum class DebatePosition {
    FOR,        // ЗА
    AGAINST     // ПРОТИ
}

sealed class DebatePhase {
    object TopicSelection : DebatePhase()
    object PositionSelection : DebatePhase()
    object UserArgument : DebatePhase()
    object AiResponse : DebatePhase()
    object DebateComplete : DebatePhase()
}

data class DebateRound(
    val roundNumber: Int,
    val userArgument: String,
    val aiResponse: String
)
