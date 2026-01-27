package com.aivoicepower.ui.screens.warmup

data class QuickWarmupState(
    val exercises: List<QuickWarmupExercise> = getQuickWarmupExercises(),
    val currentExerciseIndex: Int = 0,
    val completedExercises: Set<Int> = emptySet(),
    val isExerciseDialogOpen: Boolean = false,
    val totalElapsedSeconds: Int = 0,
    val isCompleted: Boolean = false
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

private fun getQuickWarmupExercises(): List<QuickWarmupExercise> {
    return listOf(
        // 1. Артикуляція: Усмішка-хоботок
        QuickWarmupExercise(
            id = 1,
            title = "Усмішка-хоботок",
            category = WarmupCategoryType.ARTICULATION,
            durationSeconds = 30,
            instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення.",
            articulationExercise = ArticulationExercise(
                id = 1,
                title = "Усмішка-хоботок",
                durationSeconds = 30,
                instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення."
            )
        ),

        // 2. Артикуляція: Язик вліво-вправо
        QuickWarmupExercise(
            id = 2,
            title = "Язик вліво-вправо",
            category = WarmupCategoryType.ARTICULATION,
            durationSeconds = 20,
            instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно.",
            articulationExercise = ArticulationExercise(
                id = 2,
                title = "Язик вліво-вправо",
                durationSeconds = 20,
                instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно."
            )
        ),

        // 3. Дихання: Діафрагмальне
        QuickWarmupExercise(
            id = 3,
            title = "Діафрагмальне дихання",
            category = WarmupCategoryType.BREATHING,
            durationSeconds = 60,
            instruction = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху.",
            breathingExercise = BreathingExercise(
                id = 1,
                title = "Діафрагмальне дихання",
                durationSeconds = 60,
                pattern = BreathingPattern(
                    inhaleSeconds = 4,
                    exhaleSeconds = 4
                ),
                description = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху.",
                speechBenefit = "Розвиває правильну опору дихання для довгих фраз. Зміцнює діафрагму - основний м'яз для сильного голосу."
            )
        ),

        // 4. Голос: Гумкання
        QuickWarmupExercise(
            id = 4,
            title = "Гумкання",
            category = WarmupCategoryType.VOICE,
            durationSeconds = 30,
            instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах.",
            voiceExercise = VoiceExercise(
                id = 1,
                title = "Гумкання",
                durationSeconds = 30,
                instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах.",
                audioExampleUrl = null
            )
        )
    )
}
