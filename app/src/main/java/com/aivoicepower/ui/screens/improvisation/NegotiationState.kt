package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.ui.screens.improvisation.components.OrbState

data class NegotiationState(
    val isStarted: Boolean = false,
    val currentRound: Int = 1,
    val maxRounds: Int = 3,
    val rounds: List<NegotiationRound> = emptyList(),
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

data class NegotiationRound(
    val roundNumber: Int,
    val userProposal: String,
    val aiResponse: String
)
