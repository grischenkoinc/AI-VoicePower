package com.aivoicepower.domain.model.content

import com.aivoicepower.domain.model.course.Difficulty

sealed class ImprovisationTask {
    data class RandomTopic(
        val topic: String,
        val difficulty: Difficulty,
        val preparationSeconds: Int,
        val speakingSeconds: Int,
        val hints: List<String>
    ) : ImprovisationTask()

    data class Storytelling(
        val format: StoryFormat,
        val elements: StoryElements?
    ) : ImprovisationTask()

    data class Debate(
        val topic: String,
        val userPosition: DebatePosition,
        val rounds: Int
    ) : ImprovisationTask()

    data class SalesPitch(
        val product: Product,
        val customer: CustomerProfile,
        val pitchSeconds: Int
    ) : ImprovisationTask()
}
