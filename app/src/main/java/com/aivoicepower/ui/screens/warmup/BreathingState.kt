package com.aivoicepower.ui.screens.warmup

data class BreathingState(
    val exercises: List<BreathingExercise> = getBreathingExercises(),
    val completedToday: Set<Int> = emptySet(),
    val selectedExercise: BreathingExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val showInstructions: Boolean = false,
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
    val description: String,
    val speechBenefit: String
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

// Helper function to calculate actual duration based on desired duration and cycle
private fun calculateActualDuration(desiredSeconds: Int, cycleDuration: Int): Int {
    val cycles = desiredSeconds / cycleDuration
    return cycles * cycleDuration
}

private fun getBreathingExercises(): List<BreathingExercise> {
    return listOf(
        BreathingExercise(
            id = 1,
            title = "Діафрагмальне дихання",
            durationSeconds = calculateActualDuration(60, 8), // 4+4=8, 7 циклів = 56 сек
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 4
            ),
            description = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху.",
            speechBenefit = "Розвиває правильну опору дихання для довгих фраз. Зміцнює діафрагму - основний м'яз для сильного голосу."
        ),
        BreathingExercise(
            id = 2,
            title = "Квадратне дихання",
            durationSeconds = calculateActualDuration(60, 16), // 4+4+4+4=16, 3 цикли = 48 сек
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 4,
                exhaleSeconds = 4,
                exhaleHoldSeconds = 4
            ),
            description = "Рівні інтервали для кожної фази. Допомагає зосередитися та заспокоїтися.",
            speechBenefit = "Покращує контроль видиху та паузацію. Допомагає чітко вимовляти слова без задишки."
        ),
        BreathingExercise(
            id = 3,
            title = "4-7-8 дихання",
            durationSeconds = calculateActualDuration(60, 19), // 4+7+8=19, 3 цикли = 57 сек
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 7,
                exhaleSeconds = 8
            ),
            description = "Техніка для швидкого заспокоєння. Довгий видих активує парасимпатичну нервову систему.",
            speechBenefit = "Знижує хвилювання перед виступом. Довгий видих допомагає говорити впевнено та спокійно."
        ),
        BreathingExercise(
            id = 4,
            title = "Спокійне дихання",
            durationSeconds = calculateActualDuration(48, 8), // 3+5=8, 6 циклів = 48 сек
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                exhaleSeconds = 5
            ),
            description = "Довший видих допомагає розслабитися. Ідеально перед сном.",
            speechBenefit = "Розслабляє голосові зв'язки. Усуває напругу в горлі для м'якого звучання голосу."
        ),
        BreathingExercise(
            id = 5,
            title = "Енергійне дихання",
            durationSeconds = calculateActualDuration(30, 4), // 2+2=4, 7 циклів = 28 сек
            pattern = BreathingPattern(
                inhaleSeconds = 2,
                exhaleSeconds = 2
            ),
            description = "Швидке ритмічне дихання для підвищення енергії. Будьте обережні, не перестарайтеся.",
            speechBenefit = "Активізує дихальну систему перед довгим мовленням. Додає енергії та динаміки голосу."
        ),
        BreathingExercise(
            id = 6,
            title = "Глибоке дихання",
            durationSeconds = calculateActualDuration(60, 12), // 6+6=12, 5 циклів = 60 сек
            pattern = BreathingPattern(
                inhaleSeconds = 6,
                exhaleSeconds = 6
            ),
            description = "Повільне глибоке дихання насичує організм киснем. Дихайте через ніс.",
            speechBenefit = "Максимально наповнює легені повітрям. Забезпечує потужну підтримку голосу для гучного мовлення."
        ),
        BreathingExercise(
            id = 7,
            title = "Розслаблююче дихання",
            durationSeconds = calculateActualDuration(60, 12), // 4+8=12, 5 циклів = 60 сек
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 8
            ),
            description = "Подвійна тривалість видиху максимально розслабляє. Відчуйте напругу що йде.",
            speechBenefit = "Тренує контроль видиху на довгих фразах. Допомагає економно витрачати повітря під час мовлення."
        ),
        BreathingExercise(
            id = 8,
            title = "Ритмічне дихання",
            durationSeconds = calculateActualDuration(48, 12), // 3+3+3+3=12, 4 цикли = 48 сек
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                inhaleHoldSeconds = 3,
                exhaleSeconds = 3,
                exhaleHoldSeconds = 3
            ),
            description = "Рівний ритм створює медитативний стан. Зосередьтеся на рахунку.",
            speechBenefit = "Вирівнює темп мовлення. Допомагає підтримувати стабільний ритм під час довгих промов."
        )
    )
}
