package com.aivoicepower.ui.screens.improvisation

sealed class ImprovisationEvent {
    object RandomTopicClicked : ImprovisationEvent()
    object StorytellingClicked : ImprovisationEvent()
    object DailyChallengeClicked : ImprovisationEvent()
    object DebateClicked : ImprovisationEvent()
    object SalesPitchClicked : ImprovisationEvent()

    // AI Coach simulations moved to Improvisation
    object JobInterviewClicked : ImprovisationEvent()
    object PresentationClicked : ImprovisationEvent()
    object NegotiationClicked : ImprovisationEvent()
}
