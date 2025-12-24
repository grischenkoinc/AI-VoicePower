package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DebateTopicsProvider

sealed class DebateEvent {
    data class TopicSelected(val topic: DebateTopicsProvider.DebateTopic) : DebateEvent()
    data class PositionSelected(val position: DebatePosition) : DebateEvent()
    object StartRecordingClicked : DebateEvent()
    object StopRecordingClicked : DebateEvent()
    object NextRoundClicked : DebateEvent()
    object FinishDebateClicked : DebateEvent()
}
