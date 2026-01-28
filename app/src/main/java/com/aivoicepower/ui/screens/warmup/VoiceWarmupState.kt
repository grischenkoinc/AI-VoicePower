package com.aivoicepower.ui.screens.warmup

data class VoiceWarmupState(
    val exercises: List<VoiceExercise> = getVoiceExercises(),
    val completedToday: Set<Int> = emptySet(),
    val selectedExercise: VoiceExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val showCompletionOverlay: Boolean = false
)

data class VoiceExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val instruction: String,
    val audioExampleUrl: String? = null // Placeholder for future
)

private fun getVoiceExercises(): List<VoiceExercise> {
    return listOf(
        VoiceExercise(
            id = 1,
            title = "Гумкання",
            durationSeconds = 30,
            instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах. Спробуйте на різних нотах.",
            audioExampleUrl = null // TODO: Add audio in Phase 8
        ),
        VoiceExercise(
            id = 2,
            title = "Сирена",
            durationSeconds = 20,
            instruction = "Ведіть голос від найнижчої ноти до найвищої, як сирена. Плавно без стрибків. Використовуйте звук \"У-у-у\".",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 3,
            title = "Губні трелі",
            durationSeconds = 20,
            instruction = "Робіть звук \"Брррр\" губами, як мотор. Спробуйте на різних висотах. Це розслабляє голосові зв'язки.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 4,
            title = "Розспівка \"Ма-ме-мі-мо-му\"",
            durationSeconds = 30,
            instruction = "Співайте склади \"Ма-ме-мі-мо-му\" на одній ноті, потім підвищуйте. Чітко артикулюйте кожен склад.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 5,
            title = "Співання на одній ноті",
            durationSeconds = 25,
            instruction = "Виберіть комфортну ноту і співайте \"А-а-а\" якомога довше. Тримайте звук рівним і стабільним.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 6,
            title = "Глісандо",
            durationSeconds = 20,
            instruction = "Плавно ведіть голос вгору і вниз, як ковзанка. Звук \"О-о-о\". Без різких переходів.",
            audioExampleUrl = null
        )
    )
}
