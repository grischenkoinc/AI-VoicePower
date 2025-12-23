package com.aivoicepower.data.content

import com.aivoicepower.domain.model.Difficulty
import com.aivoicepower.domain.model.ImprovisationTopic

object ImprovisationTopicsProvider {

    fun getAllTopics(): List<ImprovisationTopic> {
        return listOf(
            // BEGINNER (15 topics)
            ImprovisationTopic(
                id = "topic_1",
                title = "Чому подорожі змінюють людину",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Власний досвід подорожей",
                    "Нові перспективи та світогляд",
                    "Культурний обмін"
                )
            ),
            ImprovisationTopic(
                id = "topic_2",
                title = "Як технології впливають на наше життя",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Позитивні зміни",
                    "Виклики та проблеми",
                    "Майбутнє технологій"
                )
            ),
            ImprovisationTopic(
                id = "topic_3",
                title = "Чому важливо вчитися протягом життя",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Розвиток особистості",
                    "Адаптація до змін",
                    "Нові можливості"
                )
            ),
            ImprovisationTopic(
                id = "topic_4",
                title = "Що робить людину щасливою",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Стосунки з людьми",
                    "Самореалізація",
                    "Баланс життя"
                )
            ),
            ImprovisationTopic(
                id = "topic_5",
                title = "Чому спорт важливий для здоров'я",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Фізичне здоров'я",
                    "Ментальне благополуччя",
                    "Соціальний аспект"
                )
            ),
            ImprovisationTopic(
                id = "topic_6",
                title = "Роль книг у сучасному світі",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Розширення кругозору",
                    "Розвиток уяви",
                    "Джерело знань"
                )
            ),
            ImprovisationTopic(
                id = "topic_7",
                title = "Чому важлива командна робота",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Синергія",
                    "Різні перспективи",
                    "Спільна мета"
                )
            ),
            ImprovisationTopic(
                id = "topic_8",
                title = "Вплив соціальних мереж на суспільство",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Комунікація",
                    "Інформаційний простір",
                    "Приватність"
                )
            ),
            ImprovisationTopic(
                id = "topic_9",
                title = "Значення творчості у житті",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Самовираження",
                    "Інновації",
                    "Емоційне здоров'я"
                )
            ),
            ImprovisationTopic(
                id = "topic_10",
                title = "Екологія та особиста відповідальність",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Повсякденні звички",
                    "Вплив на планету",
                    "Майбутні покоління"
                )
            ),

            // INTERMEDIATE (10 topics)
            ImprovisationTopic(
                id = "topic_11",
                title = "Штучний інтелект: загроза чи можливість",
                difficulty = Difficulty.INTERMEDIATE,
                hints = listOf(
                    "Автоматизація",
                    "Етичні питання",
                    "Нові професії"
                )
            ),
            ImprovisationTopic(
                id = "topic_12",
                title = "Баланс між роботою та особистим життям",
                difficulty = Difficulty.INTERMEDIATE,
                hints = listOf(
                    "Пріоритети",
                    "Ефективність",
                    "Якість життя"
                )
            ),
            ImprovisationTopic(
                id = "topic_13",
                title = "Роль освіти в сучасному суспільстві",
                difficulty = Difficulty.INTERMEDIATE,
                hints = listOf(
                    "Традиційна vs онлайн освіта",
                    "Навички майбутнього",
                    "Доступність знань"
                )
            ),
            ImprovisationTopic(
                id = "topic_14",
                title = "Глобалізація: плюси і мінуси",
                difficulty = Difficulty.INTERMEDIATE,
                hints = listOf(
                    "Економічна інтеграція",
                    "Культурний обмін",
                    "Локальна ідентичність"
                )
            ),
            ImprovisationTopic(
                id = "topic_15",
                title = "Чому важливо вміти помилятися",
                difficulty = Difficulty.INTERMEDIATE,
                hints = listOf(
                    "Досвід та навчання",
                    "Інновації",
                    "Особистісний ріст"
                )
            ),

            // ADVANCED (5 topics)
            ImprovisationTopic(
                id = "topic_16",
                title = "Майбутнє демократії в цифрову еру",
                difficulty = Difficulty.ADVANCED,
                hints = listOf(
                    "Цифрова участь",
                    "Дезінформація",
                    "Нові форми управління"
                )
            ),
            ImprovisationTopic(
                id = "topic_17",
                title = "Етика генної інженерії",
                difficulty = Difficulty.ADVANCED,
                hints = listOf(
                    "Медичні можливості",
                    "Моральні межі",
                    "Соціальні наслідки"
                )
            ),
            ImprovisationTopic(
                id = "topic_18",
                title = "Філософія свободи волі",
                difficulty = Difficulty.ADVANCED,
                hints = listOf(
                    "Детермінізм",
                    "Відповідальність",
                    "Нейронаука"
                )
            ),
            ImprovisationTopic(
                id = "topic_19",
                title = "Космічна експансія людства",
                difficulty = Difficulty.ADVANCED,
                hints = listOf(
                    "Технологічні виклики",
                    "Етичні питання",
                    "Майбутнє цивілізації"
                )
            ),
            ImprovisationTopic(
                id = "topic_20",
                title = "Роль мистецтва в постмодерному світі",
                difficulty = Difficulty.ADVANCED,
                hints = listOf(
                    "Зміна парадигм",
                    "Комерціалізація",
                    "Соціальна функція"
                )
            ),

            // Additional BEGINNER topics (5 more for variety)
            ImprovisationTopic(
                id = "topic_21",
                title = "Важливість сну для продуктивності",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Відновлення організму",
                    "Когнітивні функції",
                    "Емоційний баланс"
                )
            ),
            ImprovisationTopic(
                id = "topic_22",
                title = "Музика та її вплив на настрій",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Емоційний відгук",
                    "Концентрація",
                    "Спогади"
                )
            ),
            ImprovisationTopic(
                id = "topic_23",
                title = "Переваги здорового харчування",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Енергія",
                    "Профілактика хвороб",
                    "Якість життя"
                )
            ),
            ImprovisationTopic(
                id = "topic_24",
                title = "Домашні тварини та їхня роль",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Емоційна підтримка",
                    "Відповідальність",
                    "Активний спосіб життя"
                )
            ),
            ImprovisationTopic(
                id = "topic_25",
                title = "Хобі та особистий розвиток",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Релаксація",
                    "Нові навички",
                    "Самовираження"
                )
            )
        )
    }

    fun getTopicById(id: String): ImprovisationTopic? {
        return getAllTopics().find { it.id == id }
    }
}
