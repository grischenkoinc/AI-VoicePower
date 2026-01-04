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

    data class ArticulationExercise(
        val hasTimer: Boolean = true,
        val durationSeconds: Int = 60,
        val repetitions: Int? = null,
        val imageUrl: String? = null  // Для майбутньої ілюстрації
    ) : ExerciseContent()

    // Схожі слова (бик/бік, сила/шила)
    data class MinimalPairs(
        val pairs: List<Pair<String, String>>,  // [("бик", "бік"), ("сила", "шила")]
        val targetSounds: List<String>
    ) : ExerciseContent()

    // Чергування звуків
    data class ContrastSounds(
        val sequence: String,  // "ша-са-ша-са-ша-са"
        val targetSounds: List<String>,
        val repetitions: Int = 5
    ) : ExerciseContent()

    // 3 скоромовки поспіль
    data class TongueTwisterBattle(
        val twisters: List<TongueTwister>,  // 3 скоромовки
        val allowMistakes: Int = 0
    ) : ExerciseContent()

    // Повільна скоромовка
    data class SlowMotion(
        val text: String,
        val targetSounds: List<String>,
        val minDurationSeconds: Int = 30
    ) : ExerciseContent()
}
