package com.aivoicepower.ui.screens.diagnostic

data class DiagnosticState(
    val tasks: List<DiagnosticTask> = getDefaultTasks(),
    val currentTaskIndex: Int? = null,
    val selectedTask: DiagnosticTask? = null,
    val showInstructionDialog: Boolean = false,
    val isRecording: Boolean = false,
    val recordingSeconds: Int = 0,
    val currentRecordingPath: String? = null,
    val showRecordingPreview: Boolean = false,
    val completedTasksCount: Int = 0,
    val isAnalyzing: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null
)

data class DiagnosticTask(
    val id: String,
    val title: String,
    val emoji: String,
    val instruction: String,
    val contentText: String? = null,
    val durationSeconds: Int,
    val tips: List<String>,
    val status: TaskStatus = TaskStatus.PENDING,
    val recordingPath: String? = null
)

enum class TaskStatus {
    PENDING,      // Ще не почато
    IN_PROGRESS,  // Діалог інструкцій відкритий
    RECORDED,     // Запис зроблено
    COMPLETED     // Аналізовано (Phase 1.4)
}

fun getDefaultTasks(): List<DiagnosticTask> {
    return listOf(
        DiagnosticTask(
            id = "task_1_reading",
            title = "Читання тексту",
            emoji = "📖",
            instruction = "Прочитай наступний текст чітко та виразно",
            contentText = """
                Голосові комунікації — це мистецтво, яке вимагає практики та уваги до деталей.
                Чітка дикція, правильний темп мовлення та вміння робити паузи в потрібних місцях —
                це ключові навички для ефективного спілкування. Кожне слово має значення,
                і від того, як ми його вимовляємо, залежить, наскільки добре нас зрозуміють.
            """.trimIndent(),
            durationSeconds = 90,
            tips = listOf(
                "Читай повільно та чітко",
                "Робі паузи між реченнями",
                "Не поспішай"
            )
        ),
        DiagnosticTask(
            id = "task_2_spontaneous",
            title = "Спонтанне мовлення",
            emoji = "🗣️",
            instruction = "Розкажи про свій звичайний день, улюблене хобі або те, що тобі цікаво",
            contentText = null,
            durationSeconds = 60,
            tips = listOf(
                "Говори природно",
                "Не хвилюйся про помилки",
                "Будь собою"
            )
        ),
        DiagnosticTask(
            id = "task_3_emotional",
            title = "Емоційне читання",
            emoji = "🎭",
            instruction = "Прочитай цей текст з відповідними емоціями",
            contentText = """
                Це був найкращий день у моєму житті! Сонце яскраво світило, птахи співали,
                і я відчував себе абсолютно щасливим. Кожна мить була сповнена радості та енергії.
                Я посміхався всім, кого зустрічав, і світ здавався таким прекрасним місцем!
            """.trimIndent(),
            durationSeconds = 60,
            tips = listOf(
                "Передай емоції в голосі",
                "Варіюй інтонацію",
                "Використовуй виразність"
            )
        ),
        DiagnosticTask(
            id = "task_4_persuasive",
            title = "Переконлива промова",
            emoji = "💼",
            instruction = "Переконай уявну аудиторію в важливості навчання та особистого розвитку",
            contentText = null,
            durationSeconds = 60,
            tips = listOf(
                "Говори впевнено",
                "Використовуй аргументи",
                "Будь переконливим"
            )
        )
    )
}
