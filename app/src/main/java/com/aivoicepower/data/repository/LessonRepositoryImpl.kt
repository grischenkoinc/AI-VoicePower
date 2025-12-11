package com.aivoicepower.data.repository

import com.aivoicepower.domain.model.*
import com.aivoicepower.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor() : LessonRepository {

    private val lessons = listOf(
        // УРОК 1: АРТИКУЛЯЦІЯ
        Lesson(
            id = "lesson_1",
            title = "Артикуляційна гімнастика",
            description = "Комплексна розминка для язика, губ та м'язів обличчя. Покращує чіткість вимови.",
            category = LessonCategory.DICTION,
            durationMinutes = 20,
            isPremium = false,
            order = 1,
            exercises = listOf(
                Exercise(
                    id = "ex_1_1",
                    title = "Посмішка-Трубочка",
                    instruction = "Чергуйте широку посмішку та витягування губ трубочкою",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 60,
                    repetitions = 10,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Посмішка", "Широко посміхніться, показуючи зуби. Утримуйте 5 секунд.", 5, tips = listOf("Губи розтягнуті", "Зуби видно")),
                        ExerciseStep(2, "Трубочка", "Витягніть губи вперед трубочкою. Утримуйте 5 секунд.", 5, tips = listOf("Губи округлені", "Витягнуті вперед")),
                        ExerciseStep(3, "Повтор", "Повторіть чергування 10 разів", 50)
                    )
                ),
                Exercise(
                    id = "ex_1_2",
                    title = "Годинник",
                    instruction = "Рухайте язиком вліво-вправо, як маятник годинника",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 60,
                    repetitions = 15,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Підготовка", "Відкрийте рот, висуньте язик", 5),
                        ExerciseStep(2, "Вліво", "Потягніть язик до лівого куточка губ", 3),
                        ExerciseStep(3, "Вправо", "Потягніть язик до правого куточка губ", 3),
                        ExerciseStep(4, "Повтор", "Чергуйте 15 разів в повільному темпі", 45, tips = listOf("Рух плавний", "Язик напружений"))
                    )
                ),
                Exercise(
                    id = "ex_1_3",
                    title = "Гойдалка",
                    instruction = "Рухайте язиком вгору-вниз",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 60,
                    repetitions = 15,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Вгору", "Потягніть кінчик язика до носа", 3),
                        ExerciseStep(2, "Вниз", "Потягніть кінчик язика до підборіддя", 3),
                        ExerciseStep(3, "Повтор", "Чергуйте рухи 15 разів", 50, tips = listOf("Рот відкритий", "Рухи повільні"))
                    )
                ),
                Exercise(
                    id = "ex_1_4",
                    title = "Маляр",
                    instruction = "Проведіть язиком по піднебінню, як пензликом",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 45,
                    repetitions = 8,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Початок", "Притисніть кінчик язика до верхніх зубів", 5),
                        ExerciseStep(2, "Малювання", "Повільно проведіть язиком від зубів до м'якого піднебіння", 5, tips = listOf("Язик притиснутий", "Рух плавний")),
                        ExerciseStep(3, "Повтор", "Повторіть 8 разів", 35)
                    )
                ),
                Exercise(
                    id = "ex_1_5",
                    title = "Коник",
                    instruction = "Цокайте язиком, імітуючи цокіт копит",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 45,
                    repetitions = 20,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Підготовка", "Присмокчіть язик до піднебіння", 5),
                        ExerciseStep(2, "Цокання", "Різко відривайте язик, створюючи звук", 2, tips = listOf("Звук чіткий", "Темп помірний")),
                        ExerciseStep(3, "Повтор", "Цокайте 20 разів", 38)
                    )
                ),
                Exercise(
                    id = "ex_1_6",
                    title = "Грибок",
                    instruction = "Присмокчіть язик до піднебіння",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 30,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Присмоктування", "Притисніть весь язик до піднебіння", 10, tips = listOf("Язик плоский", "Притиснутий повністю")),
                        ExerciseStep(2, "Утримання", "Утримуйте 5 секунд", 15),
                        ExerciseStep(3, "Відпочинок", "Розслабте язик", 5)
                    )
                ),
                Exercise(
                    id = "ex_1_7",
                    title = "Чашечка",
                    instruction = "Зробіть язик чашечкою",
                    type = ExerciseType.ARTICULATION,
                    targetDurationSeconds = 40,
                    repetitions = 8,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Формування", "Висуньте широкий язик", 5),
                        ExerciseStep(2, "Чашечка", "Підніміть краї язика вгору, утворюючи чашечку", 10, tips = listOf("Середина вниз", "Краї вгору", "Не торкайтесь зубів")),
                        ExerciseStep(3, "Утримання", "Утримуйте форму 5 секунд", 20),
                        ExerciseStep(4, "Відпочинок", "Розслабте", 5)
                    )
                )
            )
        ),

        // УРОК 2: ДИХАННЯ
        Lesson(
            id = "lesson_2",
            title = "Правильне дихання",
            description = "Опануйте техніки діафрагмального дихання для потужного та стабільного голосу",
            category = LessonCategory.BREATHING,
            durationMinutes = 18,
            isPremium = false,
            order = 2,
            exercises = listOf(
                Exercise(
                    id = "ex_2_1",
                    title = "Діафрагмальне дихання базове",
                    instruction = "Навчіться дихати діафрагмою для глибокого, контрольованого дихання",
                    type = ExerciseType.BREATHING,
                    targetDurationSeconds = 120,
                    repetitions = 10,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Підготовка", "Сядьте рівно, покладіть одну руку на груди, іншу на живіт", 10),
                        ExerciseStep(2, "Вдих", "Вдихніть носом на 4 рахунки, живіт піднімається", 4, tips = listOf("Груди нерухомі", "Живіт надувається")),
                        ExerciseStep(3, "Видих", "Видихніть ротом на 4 рахунки, живіт опускається", 4),
                        ExerciseStep(4, "Повтор", "Повторіть 10 циклів", 100)
                    )
                ),
                Exercise(
                    id = "ex_2_2",
                    title = "Квадратне дихання 4-4-4-4",
                    instruction = "Техніка для контролю дихання та заспокоєння перед виступом",
                    type = ExerciseType.BREATHING,
                    targetDurationSeconds = 96,
                    repetitions = 6,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Вдих", "Вдихніть на 4 рахунки", 4),
                        ExerciseStep(2, "Затримка", "Затримайте дихання на 4 рахунки", 4),
                        ExerciseStep(3, "Видих", "Видихніть на 4 рахунки", 4),
                        ExerciseStep(4, "Пауза", "Пауза перед вдихом на 4 рахунки", 4),
                        ExerciseStep(5, "Повтор", "Повторіть 6 циклів", 80, tips = listOf("Ритм рівномірний", "Дихання спокійне"))
                    )
                ),
                Exercise(
                    id = "ex_2_3",
                    title = "Свічка",
                    instruction = "Тренуйте плавний, контрольований видих",
                    type = ExerciseType.BREATHING,
                    targetDurationSeconds = 90,
                    repetitions = 8,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Уявіть свічку", "Уявіть свічку перед собою", 5),
                        ExerciseStep(2, "Глибокий вдих", "Глибоко вдихніть діафрагмою", 4),
                        ExerciseStep(3, "Довгий видих", "Дуйте на свічку тонким струменем повітря, щоб полум'я хиталось, але не гасло", 10, tips = listOf("Видих рівномірний", "Струмінь тонкий", "Максимально довго")),
                        ExerciseStep(4, "Повтор", "Повторіть 8 разів", 70)
                    )
                ),
                Exercise(
                    id = "ex_2_4",
                    title = "Насос",
                    instruction = "Енергійне дихання для активізації перед виступом",
                    type = ExerciseType.BREATHING,
                    targetDurationSeconds = 60,
                    repetitions = 10,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Ритм", "Швидкі короткі вдихи та видихи через ніс", 5, tips = listOf("Темп швидкий", "Живіт рухається активно")),
                        ExerciseStep(2, "Виконання", "Робіть 10 циклів вдих-видих", 30),
                        ExerciseStep(3, "Відпочинок", "Спокійне дихання", 10),
                        ExerciseStep(4, "Повтор", "Повторіть ще 2 рази", 15)
                    )
                ),
                Exercise(
                    id = "ex_2_5",
                    title = "Дихання з рахунком",
                    instruction = "Збільшуйте тривалість видиху для витривалості голосу",
                    type = ExerciseType.BREATHING,
                    targetDurationSeconds = 120,
                    repetitions = 5,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Вдих", "Вдих на 4 рахунки", 4),
                        ExerciseStep(2, "Видих з рахунком", "Видихайте, рахуючи вголос: 1,2,3,4,5,6,7,8...", 16, tips = listOf("Рахуйте чітко", "Голос рівний", "Максимально довго")),
                        ExerciseStep(3, "Відпочинок", "Відновіть дихання", 5),
                        ExerciseStep(4, "Збільшення", "З кожним разом намагайтесь порахувати більше", 95)
                    )
                )
            )
        ),

        // УРОК 3: СКОРОМОВКИ ТА ДИКЦІЯ
        Lesson(
            id = "lesson_3",
            title = "Скоромовки та чіткість мови",
            description = "Покращте дикцію та швидкість мовлення за допомогою скоромовок",
            category = LessonCategory.DICTION,
            durationMinutes = 25,
            isPremium = false,
            order = 3,
            exercises = listOf(
                Exercise(
                    id = "ex_3_1",
                    title = "Скоромовка: Трава та дрова",
                    instruction = "Класична українська скоромовка для тренування чіткості",
                    exampleText = "На дворі трава, на траві дрова. Не руби дрова на траві двора.",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 90,
                    repetitions = 5,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Повільно", "Прочитайте повільно, чітко вимовляючи кожен звук", 20, tips = listOf("Кожен звук окремо", "Без поспіху")),
                        ExerciseStep(2, "Середній темп", "Прискорте до середнього темпу", 15),
                        ExerciseStep(3, "Швидко", "Прочитайте максимально швидко, зберігаючи чіткість", 10, tips = listOf("Чіткість важливіша за швидкість")),
                        ExerciseStep(4, "Повтор", "Повторіть 5 разів з різним темпом", 45)
                    )
                ),
                Exercise(
                    id = "ex_3_2",
                    title = "Скоромовка: Косар Касян",
                    instruction = "Тренування звуку 'К' та 'С'",
                    exampleText = "Косар Касян косою косить коси. Скосивши, склав коси на покоси.",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 90,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Вивчення", "Прочитайте і запам'ятайте текст", 20),
                        ExerciseStep(2, "Повільно", "Чітко вимовте кожне слово", 20),
                        ExerciseStep(3, "Прискорення", "Поступово прискорюйте", 30),
                        ExerciseStep(4, "Максимальна швидкість", "Максимально швидко і чітко", 20, tips = listOf("Зберігайте чіткість", "Не ковтайте звуки"))
                    )
                ),
                Exercise(
                    id = "ex_3_3",
                    title = "Скоромовка: Пилип Прип",
                    instruction = "Складна скоромовка для просунутих",
                    exampleText = "Пилип Прип прийшов, попелець підпалив, піп прокляв.",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 80,
                    repetitions = 5,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "По складах", "Прочитайте по складах", 20),
                        ExerciseStep(2, "По словах", "Прочитайте слово за словом", 20),
                        ExerciseStep(3, "Фраза", "Вся фраза повільно", 15),
                        ExerciseStep(4, "Швидко", "3 рази підряд швидко", 25, tips = listOf("Акцентуйте 'П'", "Не заплутуйтесь"))
                    )
                ),
                Exercise(
                    id = "ex_3_4",
                    title = "Скоромовка: Шість мишенят",
                    instruction = "Тренування шиплячих звуків",
                    exampleText = "Шість мишенят шурхотять у шухляді, шукають шматочок сушеного сиру.",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 80,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Звук Ш", "Зосередьтесь на чіткому 'Ш'", 20),
                        ExerciseStep(2, "Повільно", "Повільно вся фраза", 20),
                        ExerciseStep(3, "Прискорення", "Поступово прискорюйте", 40, tips = listOf("Ш чітке", "С чітке"))
                    )
                ),
                Exercise(
                    id = "ex_3_5",
                    title = "Скоромовка: Чотири чорненькі чумазенькі чортенята",
                    instruction = "Найскладніша скоромовка для майстрів",
                    exampleText = "Чотири чорненькі чумазенькі чортенята чертили чорними чорнилами креслення.",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 100,
                    repetitions = 3,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Розбір", "Розберіть кожне слово", 30),
                        ExerciseStep(2, "Повільно", "Дуже повільно вся фраза", 30),
                        ExerciseStep(3, "Середньо", "Середній темп", 20),
                        ExerciseStep(4, "Швидко", "Спроба швидко", 20, tips = listOf("Якість важливіша за швидкість", "Не поспішайте"))
                    )
                ),
                Exercise(
                    id = "ex_3_6",
                    title = "Складні звукосполучення",
                    instruction = "Вимовте складні слова чітко і повільно",
                    exampleText = "Взаємозамінюваність, найрозповсюдженіший, застосовується, використовувати",
                    type = ExerciseType.PRONUNCIATION,
                    targetDurationSeconds = 90,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "По складах", "Розділіть слова на склади", 30),
                        ExerciseStep(2, "Повільно", "Вимовте кожне слово повільно", 30),
                        ExerciseStep(3, "Нормальний темп", "Прочитайте в нормальному темпі", 30, tips = listOf("Всі звуки чіткі", "Не ковтайте склади"))
                    )
                )
            )
        ),

        // УРОК 4: ІНТОНАЦІЯ
        Lesson(
            id = "lesson_4",
            title = "Інтонація та виразність",
            description = "Навчіться керувати інтонацією для виразного мовлення",
            category = LessonCategory.INTONATION,
            durationMinutes = 22,
            isPremium = false,
            order = 4,
            exercises = listOf(
                Exercise(
                    id = "ex_4_1",
                    title = "Емоційне читання",
                    instruction = "Прочитайте одну фразу з різними емоціями",
                    exampleText = "Сьогодні прекрасний день!",
                    type = ExerciseType.READING,
                    targetDurationSeconds = 120,
                    repetitions = 6,
                    difficulty = Difficulty.BEGINNER,
                    steps = listOf(
                        ExerciseStep(1, "Радість", "Прочитайте радісно, енергійно", 15, tips = listOf("Голос вгору", "Енергія в словах")),
                        ExerciseStep(2, "Сум", "Прочитайте сумно, тихо", 15, tips = listOf("Голос вниз", "Повільно")),
                        ExerciseStep(3, "Здивування", "Прочитайте здивовано", 15),
                        ExerciseStep(4, "Гнів", "Прочитайте сердито", 15),
                        ExerciseStep(5, "Байдужість", "Прочитайте байдуже", 15),
                        ExerciseStep(6, "Захоплення", "Прочитайте із захопленням", 15, tips = listOf("Широкий діапазон", "Виразність"))
                    )
                ),
                Exercise(
                    id = "ex_4_2",
                    title = "Зміна висоти голосу",
                    instruction = "Тренуйте діапазон голосу",
                    exampleText = "Ми досягли нашої мети!",
                    type = ExerciseType.READING,
                    targetDurationSeconds = 90,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Низький голос", "Прочитайте максимально низьким голосом", 15, tips = listOf("Грудний резонанс")),
                        ExerciseStep(2, "Середній голос", "Ваш звичайний голос", 15),
                        ExerciseStep(3, "Високий голос", "Прочитайте високо", 15),
                        ExerciseStep(4, "Ковзання", "Плавно змінюйте висоту від низького до високого", 30, tips = listOf("Плавний перехід", "Без ривків")),
                        ExerciseStep(5, "Повтор", "Повторіть цикл", 15)
                    )
                ),
                Exercise(
                    id = "ex_4_3",
                    title = "Паузи та ритм",
                    instruction = "Навчіться робити виразні паузи",
                    exampleText = "Життя / це не очікування / коли закінчиться буря. // Це навчання / танцювати під дощем.",
                    type = ExerciseType.READING,
                    targetDurationSeconds = 100,
                    repetitions = 5,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Без пауз", "Прочитайте без пауз", 10),
                        ExerciseStep(2, "Короткі паузи", "/ - коротка пауза", 20, tips = listOf("Пауза 1 секунда")),
                        ExerciseStep(3, "Довгі паузи", "// - довга пауза", 20, tips = listOf("Пауза 2-3 секунди")),
                        ExerciseStep(4, "Виразно", "Прочитайте виразно з паузами", 30),
                        ExerciseStep(5, "Повтор", "Повторіть 5 разів", 20)
                    )
                ),
                Exercise(
                    id = "ex_4_4",
                    title = "Наголоси та акценти",
                    instruction = "Змінюйте наголос на різних словах",
                    exampleText = "Я хочу це зробити сьогодні.",
                    type = ExerciseType.READING,
                    targetDurationSeconds = 120,
                    repetitions = 6,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Я", "Я хочу це зробити сьогодні (акцент на Я)", 15),
                        ExerciseStep(2, "ХОЧУ", "Я ХОЧУ це зробити сьогодні", 15, tips = listOf("Енергія на ключовому слові")),
                        ExerciseStep(3, "ЦЕ", "Я хочу ЦЕ зробити сьогодні", 15),
                        ExerciseStep(4, "ЗРОБИТИ", "Я хочу це ЗРОБИТИ сьогодні", 15),
                        ExerciseStep(5, "СЬОГОДНІ", "Я хочу це зробити СЬОГОДНІ", 15),
                        ExerciseStep(6, "Аналіз", "Зверніть увагу, як змінюється сенс", 45, tips = listOf("Різний наголос = різний сенс"))
                    )
                ),
                Exercise(
                    id = "ex_4_5",
                    title = "Розповідь історії",
                    instruction = "Розкажіть коротку історію виразно",
                    exampleText = "Одного разу я зустрів старого друга. Він сказав мені щось важливе. І моє життя змінилось.",
                    type = ExerciseType.READING,
                    targetDurationSeconds = 90,
                    repetitions = 3,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Інтрига", "Перше речення - таємниче", 20, tips = listOf("Привабіть увагу")),
                        ExerciseStep(2, "Розвиток", "Друге речення - напруга", 20),
                        ExerciseStep(3, "Кульмінація", "Третє речення - емоційно", 20, tips = listOf("Максимальна виразність")),
                        ExerciseStep(4, "Повтор", "Повторіть з різною емоцією", 30)
                    )
                )
            )
        ),

        // УРОК 5: ПРЕЗЕНТАЦІЇ
        Lesson(
            id = "lesson_5",
            title = "Навички презентації",
            description = "Опануйте мистецтво публічних виступів та презентацій",
            category = LessonCategory.PRESENTATION,
            durationMinutes = 25,
            isPremium = true,
            order = 5,
            exercises = listOf(
                Exercise(
                    id = "ex_5_1",
                    title = "Впевнений вступ",
                    instruction = "Як почати презентацію впевнено",
                    exampleText = "Доброго дня! Мене звати [ім'я]. Сьогодні я розповім вам про [тема]. Це змінить ваше розуміння [проблема].",
                    type = ExerciseType.PRESENTATION,
                    targetDurationSeconds = 60,
                    repetitions = 3,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Привітання", "Привітайте впевнено, зі посмішкою", 15, tips = listOf("Зоровий контакт", "Посмішка", "Впевненість")),
                        ExerciseStep(2, "Представлення", "Представтесь чітко", 15),
                        ExerciseStep(3, "Інтрига", "Зацікавте темою", 20, tips = listOf("Перспектива для аудиторії", "Чому це важливо?"))
                    )
                ),
                Exercise(
                    id = "ex_5_2",
                    title = "Переконливі аргументи",
                    instruction = "Презентуйте свою ідею переконливо",
                    exampleText = "По-перше, це економить час. По-друге, збільшує ефективність. По-третє, зменшує витрати. Висновок: це саме те, що вам потрібно.",
                    type = ExerciseType.PRESENTATION,
                    targetDurationSeconds = 90,
                    repetitions = 3,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Структура", "Чітко розділіть аргументи", 20, tips = listOf("По-перше, по-друге, по-третє")),
                        ExerciseStep(2, "Наголос", "Підкресліть кожен пункт", 30, tips = listOf("Пауза між пунктами", "Інтонація вниз")),
                        ExerciseStep(3, "Висновок", "Сильний висновок", 20, tips = listOf("Впевнено", "Заклик до дії")),
                        ExerciseStep(4, "Повтор", "Повторіть виразніше", 20)
                    )
                ),
                Exercise(
                    id = "ex_5_3",
                    title = "Робота з запитаннями",
                    instruction = "Впевнено відповідайте на складні питання",
                    exampleText = "Дякую за питання. Це дійсно важливий момент. Дозвольте пояснити детальніше...",
                    type = ExerciseType.PRESENTATION,
                    targetDurationSeconds = 80,
                    repetitions = 5,
                    difficulty = Difficulty.ADVANCED,
                    steps = listOf(
                        ExerciseStep(1, "Подяка", "Подякуйте за питання", 10, tips = listOf("Завжди дякуйте", "Це час подумати")),
                        ExerciseStep(2, "Визнання", "Визнайте важливість питання", 15),
                        ExerciseStep(3, "Відповідь", "Дайте структуровану відповідь", 30, tips = listOf("Чітко", "По суті", "Впевнено")),
                        ExerciseStep(4, "Перевірка", "Уточніть, чи відповіли повністю", 10),
                        ExerciseStep(5, "Повтор", "Практикуйте різні типи питань", 15)
                    )
                ),
                Exercise(
                    id = "ex_5_4",
                    title = "Сильне завершення",
                    instruction = "Закінчіть презентацію запам'ятовуючись",
                    exampleText = "Підсумовуючи: ми розглянули [пункти]. Головний висновок - [ключова думка]. Дякую за увагу! Чекаю на ваші питання.",
                    type = ExerciseType.PRESENTATION,
                    targetDurationSeconds = 70,
                    repetitions = 3,
                    difficulty = Difficulty.INTERMEDIATE,
                    steps = listOf(
                        ExerciseStep(1, "Підсумок", "Коротко повторіть головні пункти", 20, tips = listOf("Максимум 3 пункти")),
                        ExerciseStep(2, "Ключова думка", "Одне речення - суть презентації", 15, tips = listOf("Запам'ятовується", "Просто", "Сильно")),
                        ExerciseStep(3, "Подяка", "Подякуйте аудиторії", 10),
                        ExerciseStep(4, "Заклик", "Відкрийте для питань", 10, tips = listOf("Посмішка", "Відкрита постава"))
                    )
                )
            )
        )
    )

    override fun getAllLessons(): Flow<List<Lesson>> {
        return flowOf(lessons.sortedBy { it.order })
    }

    override suspend fun getLessonById(id: String): Lesson? {
        return lessons.find { it.id == id }
    }

    override fun getFreeLessons(): Flow<List<Lesson>> {
        return flowOf(lessons.filter { !it.isPremium }.sortedBy { it.order })
    }
}
