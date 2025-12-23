package com.aivoicepower.ui.screens.warmup

data class ArticulationState(
    val exercises: List<ArticulationExercise> = getArticulationExercises(),
    val completedToday: Set<Int> = emptySet(), // Індекси виконаних вправ
    val selectedExercise: ArticulationExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false
)

data class ArticulationExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val instruction: String
)

private fun getArticulationExercises(): List<ArticulationExercise> {
    return listOf(
        ArticulationExercise(
            id = 1,
            title = "Усмішка-хоботок",
            durationSeconds = 30,
            instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення."
        ),
        ArticulationExercise(
            id = 2,
            title = "Язик вліво-вправо",
            durationSeconds = 20,
            instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно."
        ),
        ArticulationExercise(
            id = 3,
            title = "Язик вгору-вниз",
            durationSeconds = 20,
            instruction = "Рухай язиком вгору-вниз, торкаючись верхньої та нижньої губи. Виконуй повільно та контрольовано."
        ),
        ArticulationExercise(
            id = 4,
            title = "Коло язиком",
            durationSeconds = 30,
            instruction = "Проводь кінчиком язика по зовнішньому боці зубів, роблячи коло. Спочатку за годинниковою, потім проти."
        ),
        ArticulationExercise(
            id = 5,
            title = "Клацання язиком",
            durationSeconds = 15,
            instruction = "Клацай язиком, як коник цокає копитами. Робіт чіткі, голосні звуки."
        ),
        ArticulationExercise(
            id = 6,
            title = "Масаж щік",
            durationSeconds = 20,
            instruction = "Надувай щоки, затримуй повітря на 2-3 секунди, потім розслабляй. Повтори кілька разів."
        ),
        ArticulationExercise(
            id = 7,
            title = "Губи-трубочка",
            durationSeconds = 20,
            instruction = "Витягни губи вперед трубочкою і затримай на кілька секунд. Розслабся і повтори."
        ),
        ArticulationExercise(
            id = 8,
            title = "Широкий язик",
            durationSeconds = 15,
            instruction = "Розслаб язик і поклади його плоско на нижню губу. Утримуй позицію."
        ),
        ArticulationExercise(
            id = 9,
            title = "Гострий язик",
            durationSeconds = 15,
            instruction = "Напружи язик і зроби його вузьким та гострим. Витягни вперед."
        ),
        ArticulationExercise(
            id = 10,
            title = "Чашечка",
            durationSeconds = 20,
            instruction = "Підніми боки язика вгору, формуючи чашечку. Утримуй позицію."
        ),
        ArticulationExercise(
            id = 11,
            title = "Гойдалка",
            durationSeconds = 25,
            instruction = "Рухай язиком вверх (до носа) і вниз (до підборіддя), як на гойдалці."
        ),
        ArticulationExercise(
            id = 12,
            title = "Годинник",
            durationSeconds = 30,
            instruction = "Рухай язиком по колу, ніби стрілки годинника. Спочатку повільно, потім трохи швидше."
        )
    )
}
