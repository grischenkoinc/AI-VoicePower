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

    // AI Coach simulations (moved from Phase 6)
    data class JobInterview(
        val steps: List<SimulationStep>,
        val difficulty: Difficulty = Difficulty.INTERMEDIATE
    ) : ImprovisationTask()

    data class Presentation(
        val steps: List<SimulationStep>,
        val difficulty: Difficulty = Difficulty.INTERMEDIATE
    ) : ImprovisationTask()

    data class Negotiation(
        val steps: List<SimulationStep>,
        val difficulty: Difficulty = Difficulty.ADVANCED
    ) : ImprovisationTask()
}

/**
 * Step in a multi-step simulation
 */
data class SimulationStep(
    val stepNumber: Int,
    val question: String,
    val hint: String
)
