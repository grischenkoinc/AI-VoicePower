package com.aivoicepower.ui.screens.improvisation

sealed class RandomTopicEvent {
    object GenerateNewTopic : RandomTopicEvent()
    object StartPreparation : RandomTopicEvent()
    object StartRecording : RandomTopicEvent()
    object StopRecording : RandomTopicEvent()
    object CompleteTask : RandomTopicEvent()
    // Analysis limits
    object DismissAnalysisLimitSheet : RandomTopicEvent()
    object WatchAdForAnalysis : RandomTopicEvent()
    object ContinueWithoutAnalysis : RandomTopicEvent()
}
