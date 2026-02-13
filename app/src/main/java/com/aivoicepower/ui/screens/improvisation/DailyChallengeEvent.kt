package com.aivoicepower.ui.screens.improvisation

sealed class DailyChallengeEvent {
    object LoadChallenge : DailyChallengeEvent()
    object StartPreparation : DailyChallengeEvent()
    object SkipPreparation : DailyChallengeEvent()
    object StartRecording : DailyChallengeEvent()
    object StopRecording : DailyChallengeEvent()
    object CompleteChallenge : DailyChallengeEvent()
    object DismissAnalysis : DailyChallengeEvent()
    object DismissAnalysisLimitSheet : DailyChallengeEvent()
    object WatchAdForAnalysis : DailyChallengeEvent()
    object ContinueWithoutAnalysis : DailyChallengeEvent()
}
