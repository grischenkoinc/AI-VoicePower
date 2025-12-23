package com.aivoicepower.ui.screens.warmup

data class BreathingState(
    val exercises: List<BreathingExercise> = getBreathingExercises(),
    val completedToday: Set<Int> = emptySet(),
    val selectedExercise: BreathingExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val totalSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.INHALE,
    val phaseProgress: Float = 0f, // 0.0 - 1.0
    val isRunning: Boolean = false
)

data class BreathingExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val pattern: BreathingPattern,
    val description: String
)

data class BreathingPattern(
    val inhaleSeconds: Int,
    val inhaleHoldSeconds: Int = 0,
    val exhaleSeconds: Int,
    val exhaleHoldSeconds: Int = 0
) {
    val cycleDurationSeconds: Int
        get() = inhaleSeconds + inhaleHoldSeconds + exhaleSeconds + exhaleHoldSeconds
}

enum class BreathingPhase {
    INHALE,        // Вдих
    INHALE_HOLD,   // Затримка після вдиху
    EXHALE,        // Видих
    EXHALE_HOLD    // Затримка після видиху
}

private fun getBreathingExercises(): List<BreathingExercise> {
    return listOf(
        BreathingExercise(
            id = 1,
            title = "Діафрагмальне дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 4
            ),
            description = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху."
        ),
        BreathingExercise(
            id = 2,
            title = "Квадратне дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 4,
                exhaleSeconds = 4,
                exhaleHoldSeconds = 4
            ),
            description = "Рівні інтервали для кожної фази. Допомагає зосередитися та заспокоїтися."
        ),
        BreathingExercise(
            id = 3,
            title = "4-7-8 дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 7,
                exhaleSeconds = 8
            ),
            description = "Техніка для швидкого заспокоєння. Довгий видих активує парасимпатичну нервову систему."
        ),
        BreathingExercise(
            id = 4,
            title = "Спокійне дихання",
            durationSeconds = 45,
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                exhaleSeconds = 5
            ),
            description = "Довший видих допомагає розслабитися. Ідеально перед сном."
        ),
        BreathingExercise(
            id = 5,
            title = "Енергійне дихання",
            durationSeconds = 30,
            pattern = BreathingPattern(
                inhaleSeconds = 2,
                exhaleSeconds = 2
            ),
            description = "Швидке ритмічне дихання для підвищення енергії. Будьте обережні, не перестарайтеся."
        ),
        BreathingExercise(
            id = 6,
            title = "Глибоке дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 6,
                exhaleSeconds = 6
            ),
            description = "Повільне глибоке дихання насичує організм киснем. Дихайте через ніс."
        ),
        BreathingExercise(
            id = 7,
            title = "Розслаблююче дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 8
            ),
            description = "Подвійна тривалість видиху максимально розслабляє. Відчуйте напругу що йде."
        ),
        BreathingExercise(
            id = 8,
            title = "Ритмічне дихання",
            durationSeconds = 45,
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                inhaleHoldSeconds = 3,
                exhaleSeconds = 3,
                exhaleHoldSeconds = 3
            ),
            description = "Рівний ритм створює медитативний стан. Зосередьтеся на рахунку."
        )
    )
}
