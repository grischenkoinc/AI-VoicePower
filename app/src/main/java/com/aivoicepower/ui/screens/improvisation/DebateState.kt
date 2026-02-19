package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DebateTopicsProvider
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.ui.screens.improvisation.components.OrbState

data class DebateState(
    val selectedTopic: DebateTopicsProvider.DebateTopic? = null,
    val userPosition: DebatePosition? = null,
    val phase: DebatePhase = DebatePhase.TopicSelection,
    val currentRound: Int = 1,
    val maxRounds: Int = 5,
    val rounds: List<DebateRound> = emptyList(),
    val isRecording: Boolean = false,
    val isListening: Boolean = false,
    val recordingSeconds: Int = 0,
    val maxRecordingSeconds: Int = 90,
    val audioLevel: Float = 0f,
    val orbState: OrbState = OrbState.IDLE,
    val aiText: String = "",
    val hint: String? = null,
    val isAiThinking: Boolean = false,
    val isTtsSpeaking: Boolean = false,
    val error: String? = null,
    val isAnalyzing: Boolean = false,
    val analysisResult: VoiceAnalysisResult? = null
)

enum class DebatePosition {
    FOR,
    AGAINST
}

sealed class DebatePhase {
    object TopicSelection : DebatePhase()
    object PositionSelection : DebatePhase()
    object Conversation : DebatePhase()
    object Complete : DebatePhase()
}

data class DebateRound(
    val roundNumber: Int,
    val userArgument: String,
    val aiResponse: String
)
