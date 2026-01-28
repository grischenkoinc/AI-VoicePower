package com.aivoicepower.ui.screens.warmup

data class QuickWarmupState(
    val exercises: List<QuickWarmupExercise> = generateRandomQuickWarmupExercises(),
    val currentExerciseIndex: Int = 0,
    val completedExercises: Set<Int> = emptySet(),
    val isExerciseDialogOpen: Boolean = false,
    val totalElapsedSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val showCompletionOverlay: Boolean = false,
    // Breathing exercise state
    val breathingElapsedSeconds: Int = 0,
    val breathingCurrentPhase: BreathingPhase = BreathingPhase.INHALE,
    val breathingPhaseProgress: Float = 0f,
    val breathingIsRunning: Boolean = false
)

data class QuickWarmupExercise(
    val id: Int,
    val title: String,
    val category: WarmupCategoryType,
    val durationSeconds: Int,
    val instruction: String,
    // Type-specific data
    val articulationExercise: ArticulationExercise? = null,
    val breathingExercise: BreathingExercise? = null,
    val voiceExercise: VoiceExercise? = null
)

private fun generateRandomQuickWarmupExercises(): List<QuickWarmupExercise> {
    // Отримуємо всі вправи з кожної категорії
    val articulationExercises = ArticulationState().exercises.shuffled()
    val breathingExercises = BreathingState().exercises.shuffled()
    val voiceExercises = VoiceWarmupState().exercises.shuffled()

    val result = mutableListOf<QuickWarmupExercise>()
    var articulationIndex = 0
    var voiceIndex = 0
    var breathingIndex = 0
    var idCounter = 1

    // Логіка: 1-Артикуляція → 2-Голос → 3-Дихання → повтор циклу
    val exerciseCount = 10 // ~5 хвилин

    for (i in 0 until exerciseCount) {
        val category = when (i % 3) {
            0 -> WarmupCategoryType.ARTICULATION
            1 -> WarmupCategoryType.VOICE
            else -> WarmupCategoryType.BREATHING
        }

        val exercise = when (category) {
            WarmupCategoryType.ARTICULATION -> {
                if (articulationIndex < articulationExercises.size) {
                    val ex = articulationExercises[articulationIndex++]
                    QuickWarmupExercise(
                        id = idCounter++,
                        title = ex.title,
                        category = WarmupCategoryType.ARTICULATION,
                        durationSeconds = ex.durationSeconds,
                        instruction = ex.instruction,
                        articulationExercise = ex
                    )
                } else null
            }
            WarmupCategoryType.VOICE -> {
                if (voiceIndex < voiceExercises.size) {
                    val ex = voiceExercises[voiceIndex++]
                    QuickWarmupExercise(
                        id = idCounter++,
                        title = ex.title,
                        category = WarmupCategoryType.VOICE,
                        durationSeconds = ex.durationSeconds,
                        instruction = ex.instruction,
                        voiceExercise = ex
                    )
                } else null
            }
            WarmupCategoryType.BREATHING -> {
                if (breathingIndex < breathingExercises.size) {
                    val ex = breathingExercises[breathingIndex++]
                    QuickWarmupExercise(
                        id = idCounter++,
                        title = ex.title,
                        category = WarmupCategoryType.BREATHING,
                        durationSeconds = ex.durationSeconds,
                        instruction = ex.description,
                        breathingExercise = ex
                    )
                } else null
            }
            else -> null
        }

        exercise?.let { result.add(it) }
    }

    return result
}
