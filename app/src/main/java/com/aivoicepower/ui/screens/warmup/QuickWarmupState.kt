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
    val showCompletionOverlay: Boolean = false
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
    val articulationExercises = ArticulationState().exercises
    val breathingExercises = BreathingState().exercises
    val voiceExercises = VoiceWarmupState().exercises

    // Цільовий час: ~5 хвилин (300 секунд)
    // Розподіл: приблизно порівну з кожної категорії
    val result = mutableListOf<QuickWarmupExercise>()

    // Вибираємо по 3-4 вправи з кожної категорії
    val articulationCount = 4
    val breathingCount = 3
    val voiceCount = 3

    // Вибираємо рандомні вправи з артикуляції
    val selectedArticulation = articulationExercises.shuffled().take(articulationCount).map { exercise ->
        QuickWarmupExercise(
            id = result.size + 1,
            title = exercise.title,
            category = WarmupCategoryType.ARTICULATION,
            durationSeconds = exercise.durationSeconds,
            instruction = exercise.instruction,
            articulationExercise = exercise
        )
    }

    // Вибираємо рандомні вправи з дихання
    val selectedBreathing = breathingExercises.shuffled().take(breathingCount).map { exercise ->
        QuickWarmupExercise(
            id = result.size + selectedArticulation.size + 1,
            title = exercise.title,
            category = WarmupCategoryType.BREATHING,
            durationSeconds = exercise.durationSeconds,
            instruction = exercise.description,
            breathingExercise = exercise
        )
    }

    // Вибираємо рандомні вправи з голосу
    val selectedVoice = voiceExercises.shuffled().take(voiceCount).map { exercise ->
        QuickWarmupExercise(
            id = result.size + selectedArticulation.size + selectedBreathing.size + 1,
            title = exercise.title,
            category = WarmupCategoryType.VOICE,
            durationSeconds = exercise.durationSeconds,
            instruction = exercise.instruction,
            voiceExercise = exercise
        )
    }

    // Об'єднуємо всі вправи та перемішуємо
    result.addAll(selectedArticulation)
    result.addAll(selectedBreathing)
    result.addAll(selectedVoice)

    // Перемішуємо щоб вправи різних категорій чергувались
    return result.shuffled()
}
