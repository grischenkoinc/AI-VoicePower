package com.aivoicepower.domain.model.exercise

sealed class ExerciseContent {
    data class TongueTwister(
        val text: String,
        val difficulty: Int,       // 1-5
        val targetSounds: List<String>
    ) : ExerciseContent()

    data class ReadingText(
        val text: String,
        val emotion: Emotion? = null
    ) : ExerciseContent()

    data class FreeSpeechTopic(
        val topic: String,
        val hints: List<String>
    ) : ExerciseContent()

    data class Dialogue(
        val lines: List<DialogueLine>
    ) : ExerciseContent()

    data class Retelling(
        val sourceText: String,
        val keyPoints: List<String>
    ) : ExerciseContent()

    data class Pitch(
        val scenario: String,
        val targetAudience: String,
        val keyMessages: List<String>
    ) : ExerciseContent()
}
