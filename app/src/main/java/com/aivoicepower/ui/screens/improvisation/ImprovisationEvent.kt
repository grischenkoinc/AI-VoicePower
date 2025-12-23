package com.aivoicepower.ui.screens.improvisation

sealed class ImprovisationEvent {
    object RandomTopicClicked : ImprovisationEvent()
    object StorytellingClicked : ImprovisationEvent()
    object DailyChallengeClicked : ImprovisationEvent()
    object DebateClicked : ImprovisationEvent()
    object SalesPitchClicked : ImprovisationEvent()
}
