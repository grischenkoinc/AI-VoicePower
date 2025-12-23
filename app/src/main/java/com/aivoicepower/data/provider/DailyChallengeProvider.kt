package com.aivoicepower.data.provider

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DailyChallengeProvider @Inject constructor() {

    data class DailyChallenge(
        val id: String,
        val type: ChallengeType,
        val title: String,
        val description: String,
        val timeLimit: Int, // секунди
        val difficulty: String
    )

    enum class ChallengeType {
        OPPOSITE_DAY,        // День навпаки
        METAPHOR_MASTER,     // Майстер метафор
        NO_HESITATION,       // Без зупинок
        EMOTION_SWITCH,      // Зміна емоцій
        SPEED_ROUND,         // Швидке коло
        CHARACTER_VOICE      // Голос персонажа
    }

    // Challenge templates for each type
    private val oppositeDayChallenges = listOf(
        "Переконай, що зима краща за літо",
        "Доведи, що неуспіх корисніший за успіх",
        "Аргументуй, чому мовчання красномовніше слів",
        "Поясни, чому темрява краща за світло",
        "Переконай, що хаос кращий за порядок"
    )

    private val metaphorMasterChallenges = listOf(
        "Опиши свій день використовуючи тільки кулінарні метафори",
        "Розкажи про технології через призму природи",
        "Поясни емоції через архітектурні метафори",
        "Опиши відносини використовуючи музичні терміни",
        "Розкажи про навчання через спортивні аналогії"
    )

    private val noHesitationTopics = listOf(
        "Ідеальний вихідний день",
        "Що таке справжня дружба",
        "Мистецтво спілкування",
        "Цінність часу",
        "Як знайти натхнення"
    )

    private val emotionSwitchScenarios = listOf(
        "Розкажи історію про втрачений ключ, змінюючи емоцію кожні 20 секунд: радість → гнів → сум → здивування",
        "Опиши свій ранок, змінюючи настрій: ентузіазм → розчарування → гумор → спокій",
        "Розкажи про подорож, переходячи між: страх → захоплення → ностальгія → надія",
        "Опиши зустріч з другом через: сором → радість → тривога → задоволення",
        "Розкажи про мрію, змінюючи: впевненість → сумнів → натхнення → рішучість"
    )

    private val speedRoundTopics = listOf(
        listOf("кава", "ранок", "енергія", "усмішка", "початок"),
        listOf("книга", "знання", "уява", "пригода", "мудрість"),
        listOf("музика", "ритм", "настрій", "спогади", "гармонія"),
        listOf("дощ", "свіжість", "спокій", "роздуми", "очищення"),
        listOf("місто", "рух", "люди", "можливості", "життя")
    )

    private val characterVoiceChallenges = listOf(
        "Розкажи про сучасні технології голосом 80-річного діда",
        "Опиши ранкову пробіжку як спортивний коментатор",
        "Розкажи про приготування сніданку як шеф-кухар на ТВ-шоу",
        "Опиши свій день як поет-романтик XIX століття",
        "Розкажи про проблему голосом дитини-детектива"
    )

    fun getChallengeForDate(date: LocalDate): DailyChallenge {
        // Детермінований Random на основі дати
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val seed = dateString.hashCode().toLong()
        val random = Random(seed)

        // Вибираємо тип виклику детерміновано
        val challengeType = ChallengeType.values()[random.nextInt(ChallengeType.values().size)]

        return when (challengeType) {
            ChallengeType.OPPOSITE_DAY -> {
                val topic = oppositeDayChallenges[random.nextInt(oppositeDayChallenges.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "🔄 День навпаки",
                    description = topic,
                    timeLimit = 120, // 2 хвилини
                    difficulty = "INTERMEDIATE"
                )
            }
            ChallengeType.METAPHOR_MASTER -> {
                val topic = metaphorMasterChallenges[random.nextInt(metaphorMasterChallenges.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "🎭 Майстер метафор",
                    description = topic,
                    timeLimit = 90, // 1.5 хвилини
                    difficulty = "ADVANCED"
                )
            }
            ChallengeType.NO_HESITATION -> {
                val topic = noHesitationTopics[random.nextInt(noHesitationTopics.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "⚡ Без зупинок",
                    description = "Говори про '$topic' без пауз та слів-паразитів",
                    timeLimit = 60, // 1 хвилина
                    difficulty = "INTERMEDIATE"
                )
            }
            ChallengeType.EMOTION_SWITCH -> {
                val scenario = emotionSwitchScenarios[random.nextInt(emotionSwitchScenarios.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "🎭 Зміна емоцій",
                    description = scenario,
                    timeLimit = 80, // ~1:20
                    difficulty = "ADVANCED"
                )
            }
            ChallengeType.SPEED_ROUND -> {
                val topics = speedRoundTopics[random.nextInt(speedRoundTopics.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "🏃 Швидке коло",
                    description = "По 10 секунд на кожну тему: ${topics.joinToString(", ")}",
                    timeLimit = 50, // 5 тем × 10 сек
                    difficulty = "BEGINNER"
                )
            }
            ChallengeType.CHARACTER_VOICE -> {
                val challenge = characterVoiceChallenges[random.nextInt(characterVoiceChallenges.size)]
                DailyChallenge(
                    id = "challenge_$dateString",
                    type = challengeType,
                    title = "🎤 Голос персонажа",
                    description = challenge,
                    timeLimit = 90, // 1.5 хвилини
                    difficulty = "INTERMEDIATE"
                )
            }
        }
    }

    fun getTodayChallenge(): DailyChallenge {
        return getChallengeForDate(LocalDate.now())
    }
}
