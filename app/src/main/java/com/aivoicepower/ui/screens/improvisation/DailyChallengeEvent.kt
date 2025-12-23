package com.aivoicepower.ui.screens.improvisation

sealed class DailyChallengeEvent {
    object LoadChallenge : DailyChallengeEvent()
    object StartPreparation : DailyChallengeEvent()
    object StartRecording : DailyChallengeEvent()
    object StopRecording : DailyChallengeEvent()
    object CompleteChallenge : DailyChallengeEvent()
}
