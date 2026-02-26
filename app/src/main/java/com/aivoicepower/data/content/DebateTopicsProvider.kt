package com.aivoicepower.data.content

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebateTopicsProvider @Inject constructor() {

    data class DebateTopic(
        val id: String,
        val topic: String,
        val description: String,
        val difficulty: String
    )

    private val topics = listOf(
        DebateTopic(
            id = "debate_001",
            topic = "Штучний інтелект приносить більше користі, ніж шкоди",
            description = "Вплив AI на майбутнє суспільства",
            difficulty = "Середня"
        ),
        DebateTopic(
            id = "debate_002",
            topic = "Людство повинно колонізувати Марс",
            description = "Міжпланетна колонізація як пріоритет",
            difficulty = "Середня"
        ),
        DebateTopic(
            id = "debate_003",
            topic = "Безумовний базовий дохід необхідний для суспільства",
            description = "Економічні системи майбутнього",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_004",
            topic = "Соціальні мережі роблять нас більш самотніми",
            description = "Вплив соцмереж на психічне здоров'я",
            difficulty = "Легка"
        ),
        DebateTopic(
            id = "debate_005",
            topic = "Онлайн-освіта краща за традиційну",
            description = "Майбутнє освітньої системи",
            difficulty = "Легка"
        ),
        DebateTopic(
            id = "debate_006",
            topic = "Роботи зі штучним інтелектом повинні мати права",
            description = "Етика штучного інтелекту",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_007",
            topic = "Генетична модифікація людей етично виправдана",
            description = "Межі біотехнологій",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_008",
            topic = "4-денний робочий тиждень підвищує продуктивність",
            description = "Work-life balance та ефективність",
            difficulty = "Легка"
        )
    )

    fun getAllTopics(): List<DebateTopic> = topics

    fun getRandomTopic(): DebateTopic = topics.random()

    fun getTopicById(id: String): DebateTopic? = topics.find { it.id == id }
}
