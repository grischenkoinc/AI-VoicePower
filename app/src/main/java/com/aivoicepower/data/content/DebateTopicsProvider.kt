package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.DebateTopic
import com.aivoicepower.domain.model.content.DebatePosition
import com.aivoicepower.domain.model.content.ImprovisationTask

/**
 * Provider for debate topics
 * 25+ topics for practicing argumentation skills
 */
object DebateTopicsProvider {

    fun getAllDebateTopics(): List<DebateTopic> {
        return getTechnologyTopics() + getEducationTopics() + getSocietyTopics() +
               getLifestyleTopics() + getEthicsTopics()
    }

    fun getDebateTopicsByCategory(category: String): List<DebateTopic> {
        return getAllDebateTopics().filter { it.category == category }
    }

    fun getDebateTopicsByDifficulty(difficulty: Int): List<DebateTopic> {
        return getAllDebateTopics().filter { it.difficulty == difficulty }
    }

    fun getRandomDebateTopic(): DebateTopic {
        return getAllDebateTopics().random()
    }

    fun getDebateTasks(): List<ImprovisationTask.Debate> {
        return getAllDebateTopics().map { topic ->
            ImprovisationTask.Debate(
                topic = topic.topic,
                userPosition = listOf(DebatePosition.FOR, DebatePosition.AGAINST).random(),
                rounds = when (topic.difficulty) {
                    1, 2 -> 2
                    3 -> 3
                    else -> 4
                }
            )
        }
    }

    // ========== Technology Topics (5) ==========
    private fun getTechnologyTopics(): List<DebateTopic> {
        return listOf(
            DebateTopic(
                id = "debate_tech_001",
                topic = "Штучний інтелект: загроза чи можливість для людства?",
                description = "Обговорення впливу ШІ на робочі місця, креативність та майбутнє людства. Аргументи за: ефективність, нові можливості. Аргументи проти: втрата робочих місць, етичні питання.",
                difficulty = 3,
                category = "Технології"
            ),
            DebateTopic(
                id = "debate_tech_002",
                topic = "Соціальні мережі роблять нас більш самотніми",
                description = "Дискусія про вплив соціальних мереж на реальні стосунки та психічне здоров'я. Аргументи за: поверхневе спілкування, залежність. Аргументи проти: підтримка зв'язків, спільноти за інтересами.",
                difficulty = 2,
                category = "Технології"
            ),
            DebateTopic(
                id = "debate_tech_003",
                topic = "Віддалена робота ефективніша за офісну",
                description = "Порівняння продуктивності та якості життя при різних форматах роботи. Аргументи за: гнучкість, економія часу. Аргументи проти: комунікація, корпоративна культура.",
                difficulty = 2,
                category = "Технології"
            ),
            DebateTopic(
                id = "debate_tech_004",
                topic = "Електронні книги повністю замінять паперові",
                description = "Майбутнє читання: цифрове чи традиційне? Аргументи за: зручність, екологія. Аргументи проти: тактильний досвід, колекціонування.",
                difficulty = 1,
                category = "Технології"
            ),
            DebateTopic(
                id = "debate_tech_005",
                topic = "Відеоігри розвивають більше корисних навичок, ніж шкодять",
                description = "Вплив ігор на когнітивні здібності та поведінку. Аргументи за: стратегічне мислення, командна робота. Аргументи проти: залежність, насильство.",
                difficulty = 2,
                category = "Технології"
            )
        )
    }

    // ========== Education Topics (5) ==========
    private fun getEducationTopics(): List<DebateTopic> {
        return listOf(
            DebateTopic(
                id = "debate_edu_001",
                topic = "Домашні завдання потрібно скасувати",
                description = "Дискусія про роль домашніх завдань у навчальному процесі. Аргументи за: стрес, вільний час для розвитку. Аргументи проти: закріплення матеріалу, самодисципліна.",
                difficulty = 2,
                category = "Освіта"
            ),
            DebateTopic(
                id = "debate_edu_002",
                topic = "Онлайн-освіта краща за традиційну",
                description = "Порівняння форматів навчання в епоху цифровізації. Аргументи за: доступність, гнучкість. Аргументи проти: соціалізація, практичні навички.",
                difficulty = 2,
                category = "Освіта"
            ),
            DebateTopic(
                id = "debate_edu_003",
                topic = "Оцінки мотивують учнів до навчання",
                description = "Роль системи оцінювання у мотивації та розвитку. Аргументи за: чітка зворотна відь, змагання. Аргументи проти: стрес, зовнішня мотивація.",
                difficulty = 2,
                category = "Освіта"
            ),
            DebateTopic(
                id = "debate_edu_004",
                topic = "Вивчення іноземних мов має бути обов'язковим для всіх",
                description = "Необхідність мовної освіти у глобалізованому світі. Аргументи за: міжкультурна комунікація, когнітивний розвиток. Аргументи проти: індивідуальний вибір, час на інші предмети.",
                difficulty = 2,
                category = "Освіта"
            ),
            DebateTopic(
                id = "debate_edu_005",
                topic = "Креативність важливіша за академічні знання",
                description = "Пріоритети сучасної освіти: творчість vs факти. Аргументи за: інновації, адаптивність. Аргументи проти: базові знання, структура.",
                difficulty = 3,
                category = "Освіта"
            )
        )
    }

    // ========== Society Topics (5) ==========
    private fun getSocietyTopics(): List<DebateTopic> {
        return listOf(
            DebateTopic(
                id = "debate_soc_001",
                topic = "Гроші можуть купити щастя",
                description = "Зв'язок між матеріальним добробутом та задоволенням життям. Аргументи за: безпека, можливості. Аргументи проти: справжні цінності, адаптація.",
                difficulty = 2,
                category = "Суспільство"
            ),
            DebateTopic(
                id = "debate_soc_002",
                topic = "Успіх залежить більше від таланту, ніж від наполегливої праці",
                description = "Фактори успіху: природні здібності vs зусилля. Аргументи за: вроджені здібності, генетика. Аргументи проти: практика, дисципліна.",
                difficulty = 3,
                category = "Суспільство"
            ),
            DebateTopic(
                id = "debate_soc_003",
                topic = "Знаменитості мають бути прикладом для наслідування",
                description = "Відповідальність публічних осіб перед суспільством. Аргументи за: вплив, платформа. Аргументи проти: приватне життя, нереалістичні очікування.",
                difficulty = 2,
                category = "Суспільство"
            ),
            DebateTopic(
                id = "debate_soc_004",
                topic = "Волонтерство має бути обов'язковим для всіх громадян",
                description = "Громадянський обов'язок допомагати суспільству. Аргументи за: соціальна відповідальність, досвід. Аргументи проти: примус, особистий вибір.",
                difficulty = 3,
                category = "Суспільство"
            ),
            DebateTopic(
                id = "debate_soc_005",
                topic = "Соціальні мережі покращують демократію",
                description = "Роль цифрових платформ у політичному житті. Аргументи за: доступ до інформації, активізм. Аргументи проти: дезінформація, бульбашки фільтрів.",
                difficulty = 4,
                category = "Суспільство"
            )
        )
    }

    // ========== Lifestyle Topics (5) ==========
    private fun getLifestyleTopics(): List<DebateTopic> {
        return listOf(
            DebateTopic(
                id = "debate_life_001",
                topic = "Вегетаріанство корисніше за традиційне харчування",
                description = "Вплив дієти на здоров'я та довкілля. Аргументи за: екологія, етика. Аргументи проти: поживні речовини, традиції.",
                difficulty = 2,
                category = "Спосіб життя"
            ),
            DebateTopic(
                id = "debate_life_002",
                topic = "Щоденні фізичні вправи обов'язкові для щасливого життя",
                description = "Роль спорту у загальному благополуччі. Аргументи за: здоров'я, енергія. Аргументи проти: індивідуальність, час.",
                difficulty = 1,
                category = "Спосіб життя"
            ),
            DebateTopic(
                id = "debate_life_003",
                topic = "Подорожі важливіші за матеріальні речі",
                description = "Пріоритети витрат: досвід vs володіння. Аргументи за: спогади, розвиток. Аргументи проти: стабільність, комфорт.",
                difficulty = 2,
                category = "Спосіб життя"
            ),
            DebateTopic(
                id = "debate_life_004",
                topic = "Раннє пробудження робить людину більш продуктивною",
                description = "Режим дня та його вплив на ефективність. Аргументи за: тиша, дисципліна. Аргументи проти: хронотип, якість сну.",
                difficulty = 1,
                category = "Спосіб життя"
            ),
            DebateTopic(
                id = "debate_life_005",
                topic = "Домашні тварини роблять людей щасливішими",
                description = "Вплив домашніх улюбленців на психічне здоров'я. Аргументи за: компанія, відповідальність. Аргументи проти: обмеження, алергії.",
                difficulty = 1,
                category = "Спосіб життя"
            )
        )
    }

    // ========== Ethics Topics (5) ==========
    private fun getEthicsTopics(): List<DebateTopic> {
        return listOf(
            DebateTopic(
                id = "debate_eth_001",
                topic = "Приватність важливіша за безпеку",
                description = "Баланс між особистими свободами та колективною безпекою. Аргументи за: права людини, свобода. Аргументи проти: захист від загроз, порядок.",
                difficulty = 4,
                category = "Етика"
            ),
            DebateTopic(
                id = "debate_eth_002",
                topic = "Брехня іноді є морально виправданою",
                description = "Етика чесності та її межі. Аргументи за: захист почуттів, екстремальні ситуації. Аргументи проти: довіра, послідовність.",
                difficulty = 3,
                category = "Етика"
            ),
            DebateTopic(
                id = "debate_eth_003",
                topic = "Багаті люди мають моральний обов'язок допомагати бідним",
                description = "Соціальна відповідальність заможних. Аргументи за: справедливість, можливості. Аргументи проти: право власності, ефективність.",
                difficulty = 3,
                category = "Етика"
            ),
            DebateTopic(
                id = "debate_eth_004",
                topic = "Мета виправдовує засоби",
                description = "Класична етична дилема про методи досягнення цілей. Аргументи за: прагматизм, результат. Аргументи проти: принципи, наслідки.",
                difficulty = 4,
                category = "Етика"
            ),
            DebateTopic(
                id = "debate_eth_005",
                topic = "Люди за своєю природою добрі",
                description = "Філософське питання про людську природу. Аргументи за: альтруїзм, співпраця. Аргументи проти: егоїзм, історичні приклади.",
                difficulty = 4,
                category = "Етика"
            )
        )
    }
}
