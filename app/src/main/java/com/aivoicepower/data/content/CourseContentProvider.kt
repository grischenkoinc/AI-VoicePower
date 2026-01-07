package com.aivoicepower.data.content

import android.util.Log
import com.aivoicepower.data.content.courses.BusinessCommunicationCourse
import com.aivoicepower.data.content.courses.CharismaticSpeakerCourse
import com.aivoicepower.data.content.courses.ClearSpeechCourse
import com.aivoicepower.data.content.courses.CleanSpeechCourse
import com.aivoicepower.data.content.courses.ConfidentSpeakerCourse
import com.aivoicepower.data.content.courses.IntonationMagicCourse
import com.aivoicepower.data.content.courses.VoicePowerCourse
import com.aivoicepower.domain.model.course.*
import com.aivoicepower.domain.model.exercise.*
import com.aivoicepower.domain.model.user.SkillType

/**
 * Hardcoded course content
 * Contains full content for all 7 courses, 21 lessons each
 */
object CourseContentProvider {

    fun getAllCourses(): List<Course> {
        return listOf(
            getCourse1(),
            getCourse2(),
            getCourse3(),
            getCourse4(),
            getCourse5(),
            getCourse6(),
            getCourse7()
        )
    }

    fun getCourseById(id: String): Course? {
        return getAllCourses().find { it.id == id }
    }

    fun getLessonById(courseId: String, lessonId: String): Lesson? {
        val course = getCourseById(courseId)
        Log.d("CourseProvider", "getLessonById: courseId=$courseId, lessonId=$lessonId")
        Log.d("CourseProvider", "Course found: ${course != null}, course.id=${course?.id}")
        if (course != null) {
            Log.d("CourseProvider", "Available lesson IDs: ${course.lessons.map { it.id }}")
        }
        val lesson = course?.lessons?.find { it.id == lessonId }
        Log.d("CourseProvider", "Lesson found: ${lesson != null}")
        return lesson
    }

    // ========== COURSE 1: Clear Speech in 21 Days ==========

    private fun getCourse1(): Course {
        return Course(
            id = "course_1",
            title = "Чітке мовлення за 21 день",
            description = "Покращ дикцію та чіткість вимови за 3 тижні. Щоденні вправи зі скоромовками та артикуляцією.",
            iconEmoji = "🗣️",
            totalLessons = 21,
            isPremium = true,
            estimatedDays = 21,
            difficulty = Difficulty.BEGINNER,
            skills = listOf(SkillType.DICTION, SkillType.TEMPO),
            lessons = getCourse1AllLessons()
        )
    }

    private fun getCourse1AllLessons(): List<Lesson> {
        // ClearSpeechCourse.getLessons() повертає всі 21 урок (1-21)
        return ClearSpeechCourse.getLessons()
    }

    /**
     * Course 1: Чітке мовлення за 21 день
     * Week 1: Основи (Уроки 1-7)
     * Уроки 1-7 перенесені в ClearSpeechCourse.kt
     */
    private fun getCourse1Week1(): List<Lesson> = ClearSpeechCourse.getLessons()

    // Уроки 1-7 видалено - тепер в data/content/courses/ClearSpeechCourse.kt


    private fun getCourse1Week2(): List<Lesson> {
        return listOf(
            // Day 8
            Lesson(
                id = "lesson_8",
                courseId = "course_1",
                dayNumber = 8,
                title = "Складні звукосполучення",
                description = "Опановуємо складні поєднання звуків",
                theory = TheoryContent(
                    text = "Цього тижня переходимо до складніших завдань. Сьогодні працюємо зі звукосполученнями, які часто викликають труднощі: СТР, СКР, ПР, ТР та інші. Правильна артикуляція цих комбінацій значно покращить вашу дикцію.",
                    tips = listOf(
                        "Не пропускай жодного звуку",
                        "Вимовляй кожен звук окремо, потім об'єднуй",
                        "Починай повільно, поступово прискорюйся"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_8_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: СТР",
                        instruction = "Чітко вимовляй всі три звуки.",
                        content = ExerciseContent.TongueTwister(
                            text = "Стрімко стрибнув страх в стратосферу, стратосфера стратегічно стресувала",
                            difficulty = 4,
                            targetSounds = listOf("С", "Т", "Р")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_8_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: ПР-ТР",
                        instruction = "Зверни увагу на чіткість Р після приголосних.",
                        content = ExerciseContent.TongueTwister(
                            text = "На дворі трава, на траві дрова. Не руби дрова на траві двора",
                            difficulty = 3,
                            targetSounds = listOf("Т", "Р", "Д")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 9
            Lesson(
                id = "lesson_9",
                courseId = "course_1",
                dayNumber = 9,
                title = "Чіткість кінцівок слів",
                description = "Вчимося чітко вимовляти закінчення",
                theory = TheoryContent(
                    text = "Одна з найпоширеніших проблем — ковтання кінцівок слів. Це робить мовлення нечітким та важким для сприйняття. Сьогодні тренуємо чітке завершення кожного слова.",
                    tips = listOf(
                        "Не ковтай останні склади",
                        "Закінчення так само важливі, як і початок слова",
                        "Контролюй себе особливо у швидкому темпі"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_9_1",
                        type = ExerciseType.READING,
                        title = "Читання з акцентом на закінчення",
                        instruction = "Читай текст, чітко вимовляючи кожне закінчення.",
                        content = ExerciseContent.ReadingText(
                            text = "Прекрасний ранок починався поступово. Сонячне проміння пробивалося крізь хмари, освітлюючи сонні вулиці міста. Люди поспішали до своїх справ, кожен зі своїми думками та планами."
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_9_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка на закінчення",
                        instruction = "Особлива увага на -УВАЛИ.",
                        content = ExerciseContent.TongueTwister(
                            text = "Працювали, працювали, працювали, та допрацювали, опрацювали, перепрацювали",
                            difficulty = 3,
                            targetSounds = listOf("Р", "П", "В")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 10
            Lesson(
                id = "lesson_10",
                courseId = "course_1",
                dayNumber = 10,
                title = "Дзвінкі та глухі приголосні",
                description = "Розрізняємо парні приголосні",
                theory = TheoryContent(
                    text = "Парні приголосні (Б-П, Д-Т, Г-К, Ж-Ш, З-С) відрізняються лише наявністю чи відсутністю вібрації голосових зв'язок. Правильне розрізнення цих звуків критичне для чіткого мовлення.",
                    tips = listOf(
                        "Приклади руку до горла — відчуй вібрацію",
                        "Дзвінкі звуки вібрують, глухі — ні",
                        "Не оглушуй дзвінкі в кінці слів"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_10_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Пари: Б-П",
                        instruction = "Чітко розрізняй Б та П.",
                        content = ExerciseContent.TongueTwister(
                            text = "Бобер біля берега бубнів набубнів, а Петро по полю попелу попив",
                            difficulty = 4,
                            targetSounds = listOf("Б", "П")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_10_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Пари: Д-Т",
                        instruction = "Не плутай Д та Т.",
                        content = ExerciseContent.TongueTwister(
                            text = "Дід Данило ділив диню: дольку Дині, дольку Дані, дольку Тані, дольку Толі",
                            difficulty = 3,
                            targetSounds = listOf("Д", "Т")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 11
            Lesson(
                id = "lesson_11",
                courseId = "course_1",
                dayNumber = 11,
                title = "М'які та тверді звуки",
                description = "Практикуємо м'якість та твердість",
                theory = TheoryContent(
                    text = "В українській мові більшість приголосних мають м'яку та тверду форму. М'яка вимова створюється підняттям середньої частини язика до піднебіння. Правильне використання м'якості робить мовлення красивим та зрозумілим.",
                    tips = listOf(
                        "М'які звуки перед І, Ї, Е, Є, Ю, Я та Ь",
                        "Язик піднімається до піднебіння",
                        "Не перебільшуй м'якість"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_11_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "М'які приголосні",
                        instruction = "Відчуй різницю між твердими та м'якими.",
                        content = ExerciseContent.TongueTwister(
                            text = "Ліла лілії лила, Люба любисток любила, Ляля ляльку полюбляла",
                            difficulty = 3,
                            targetSounds = listOf("Л", "Ль")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_11_2",
                        type = ExerciseType.READING,
                        title = "Читання з м'якими звуками",
                        instruction = "Зверни увагу на м'яку вимову.",
                        content = ExerciseContent.ReadingText(
                            text = "Синє небо сяяло над полями. Легенький вітерець ніжно торкався колосся. Пісня жайворонка линула в безмежній блакиті, наповнюючи серце радістю та спокоєм."
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 12
            Lesson(
                id = "lesson_12",
                courseId = "course_1",
                dayNumber = 12,
                title = "Носові звуки",
                description = "Правильна вимова М та Н",
                theory = TheoryContent(
                    text = "Носові звуки М та Н утворюються при проходженні повітря через ніс. Правильна вимова цих звуків важлива не тільки для чіткості, але й для резонансу голосу.",
                    tips = listOf(
                        "Повітря виходить через ніс",
                        "Губи зімкнуті для М, язик торкається верхніх зубів для Н",
                        "Відчуй вібрацію в носі"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_12_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Звук М",
                        instruction = "Відчуй вібрацію в носі.",
                        content = ExerciseContent.TongueTwister(
                            text = "Мама милу Милу милом намилила, мила Мила милом милу мило",
                            difficulty = 3,
                            targetSounds = listOf("М", "Л")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_12_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Звук Н",
                        instruction = "Язик торкається верхніх зубів.",
                        content = ExerciseContent.TongueTwister(
                            text = "Ніна несе Ніці новини з Ніжина",
                            difficulty = 2,
                            targetSounds = listOf("Н")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),
            // Day 13
            Lesson(
                id = "lesson_13",
                courseId = "course_1",
                dayNumber = 13,
                title = "Свистячі у швидкому темпі",
                description = "С, З, Ц у складних словах",
                theory = TheoryContent(
                    text = "Повертаємось до свистячих звуків, але тепер у швидкому темпі та складних словах. Це справжній виклик для артикуляції!",
                    tips = listOf(
                        "Тримай язик стабільно",
                        "Не допускай шепелявості",
                        "Повітря йде рівним потоком"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_13_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Швидкі свистячі",
                        instruction = "Спочатку повільно, потім швидше.",
                        content = ExerciseContent.TongueTwister(
                            text = "Цап ціпав цибулю в городі, а коза засівала цибулею сад, цибуля цвіла, цвіт цвіте цвітом",
                            difficulty = 4,
                            targetSounds = listOf("Ц", "С", "З")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_13_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Складні слова",
                        instruction = "Не пропускай жодного звуку.",
                        content = ExerciseContent.TongueTwister(
                            text = "Сім косарів накосили сім копиць сіна, сіно сохне, косарі косять",
                            difficulty = 3,
                            targetSounds = listOf("С", "К")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 14
            Lesson(
                id = "lesson_14",
                courseId = "course_1",
                dayNumber = 14,
                title = "Підсумок другого тижня",
                description = "Комплексна перевірка прогресу",
                theory = TheoryContent(
                    text = "Два тижні наполегливої роботи позаду! Сьогодні перевіряємо, наскільки покращилась твоя дикція. Виконай всі вправи та порівняй з першим тижнем.",
                    tips = listOf(
                        "Записуй себе для порівняння",
                        "Будь чесним у самооцінці",
                        "Відзнач свій прогрес!"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_14_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Комплексний тест",
                        instruction = "Вимов без запинок.",
                        content = ExerciseContent.TongueTwister(
                            text = "Карл у Клари вкрав коралі, а Клара у Карла вкрала кларнет",
                            difficulty = 3,
                            targetSounds = listOf("К", "Л", "Р")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_14_2",
                        type = ExerciseType.FREE_SPEECH,
                        title = "Рефлексія",
                        instruction = "Розкажи про свій прогрес.",
                        content = ExerciseContent.FreeSpeechTopic(
                            topic = "Мій прогрес за два тижні",
                            hints = listOf(
                                "Які звуки стали легшими?",
                                "Що ще потребує роботи?",
                                "Як змінилось твоє мовлення?"
                            )
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.STRUCTURE)
                    )
                ),
                estimatedMinutes = 15
            )
        )
    }

    private fun getCourse1Week3(): List<Lesson> {
        return listOf(
            // Day 15
            Lesson(
                id = "lesson_15",
                courseId = "course_1",
                dayNumber = 15,
                title = "Подвійні приголосні",
                description = "Чітка вимова подвоєних звуків",
                theory = TheoryContent(
                    text = "Подвійні приголосні — це не просто довший звук. Це два окремих звуки, які вимовляються разом. Правильна вимова подвоєння робить мовлення чітким та виразним.",
                    tips = listOf(
                        "Два окремих звуки, не один довгий",
                        "Відчуй коротку паузу між ними",
                        "Не скорочуй подвоєння"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_15_1",
                        type = ExerciseType.READING,
                        title = "Слова з подвоєнням",
                        instruction = "Читай, чітко вимовляючи подвійні.",
                        content = ExerciseContent.ReadingText(
                            text = "Багаття горіло яскраво. Життя в таборі було насиченим. Кожен вечір ми збиралися разом, ділилися враженнями та планували завтрашній день."
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_15_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка з подвоєнням",
                        instruction = "Не пропускай подвійні звуки.",
                        content = ExerciseContent.TongueTwister(
                            text = "В палаці паллада, в Олли оллі, в Аллі аллі, в Еммі еммі",
                            difficulty = 3,
                            targetSounds = listOf("Л", "М")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),
            // Day 16
            Lesson(
                id = "lesson_16",
                courseId = "course_1",
                dayNumber = 16,
                title = "Складні скоромовки",
                description = "Комбінації різних звуків",
                theory = TheoryContent(
                    text = "Час для справжнього виклику! Сьогодні працюємо з найскладнішими скоромовками, які поєднують всі типи звуків.",
                    tips = listOf(
                        "Почни дуже повільно",
                        "Розбий на частини",
                        "Швидкість прийде з практикою"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_16_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Майстер-скоромовка 1",
                        instruction = "Найвищий рівень складності!",
                        content = ExerciseContent.TongueTwister(
                            text = "Розпорядник розпорядився розпорядження розпорядити",
                            difficulty = 5,
                            targetSounds = listOf("Р", "З", "П")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_16_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Майстер-скоромовка 2",
                        instruction = "Не здавайся!",
                        content = ExerciseContent.TongueTwister(
                            text = "Всіх скоромовок не переговориш, не перевискоромовиш",
                            difficulty = 5,
                            targetSounds = listOf("Р", "В", "П", "С")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    )
                ),
                estimatedMinutes = 15
            ),
            // Day 17
            Lesson(
                id = "lesson_17",
                courseId = "course_1",
                dayNumber = 17,
                title = "Чіткість у швидкій мові",
                description = "Зберігаємо дикцію при прискоренні",
                theory = TheoryContent(
                    text = "Справжня майстерність — це зберігати чіткість при будь-якому темпі. Сьогодні вчимося прискорюватись без втрати якості.",
                    tips = listOf(
                        "Якість важливіша за швидкість",
                        "Прискорюйся поступово",
                        "При помилці — повернись до повільного темпу"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_17_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Прискорення",
                        instruction = "Почни повільно, закінчи швидко.",
                        content = ExerciseContent.TongueTwister(
                            text = "Петро Петрович Петренко приніс Петрові Петровичу перепілку",
                            difficulty = 4,
                            targetSounds = listOf("П", "Р", "Т")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_17_2",
                        type = ExerciseType.READING,
                        title = "Швидке читання",
                        instruction = "Читай швидко, але чітко.",
                        content = ExerciseContent.ReadingText(
                            text = "Технології змінюють світ швидше, ніж будь-коли. Кожного дня з'являються нові винаходи, які ще вчора здавалися фантастикою. Важливо встигати за прогресом, не втрачаючи людяності."
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 18
            Lesson(
                id = "lesson_18",
                courseId = "course_1",
                dayNumber = 18,
                title = "Артикуляція довгих слів",
                description = "Складні багатоскладові слова",
                theory = TheoryContent(
                    text = "Довгі слова часто стають пасткою для дикції. Ми ковтаємо склади, змазуємо звуки. Сьогодні вчимося вимовляти кожен склад чітко.",
                    tips = listOf(
                        "Розбий слово на склади",
                        "Вимов кожен склад окремо",
                        "Об'єднай у ціле слово"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_18_1",
                        type = ExerciseType.READING,
                        title = "Складні терміни",
                        instruction = "Кожен склад чіткий.",
                        content = ExerciseContent.ReadingText(
                            text = "Конституційний, систематизований, ідентифікований, характеристика, інтелектуалізація, перпендикулярність, протокольований."
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_18_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Довга скоромовка",
                        instruction = "Не губи жодного складу.",
                        content = ExerciseContent.TongueTwister(
                            text = "Константин констатував конституційні конфлікти у конфедеративній конструкції",
                            difficulty = 5,
                            targetSounds = listOf("К", "Н", "С", "Т")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 12
            ),
            // Day 19
            Lesson(
                id = "lesson_19",
                courseId = "course_1",
                dayNumber = 19,
                title = "Публічне читання",
                description = "Читання для аудиторії",
                theory = TheoryContent(
                    text = "Читання вголос для інших вимагає особливої чіткості. Сьогодні практикуємо читання так, ніби нас слухає аудиторія.",
                    tips = listOf(
                        "Уяви аудиторію перед собою",
                        "Проектуй голос",
                        "Роби паузи для сприйняття"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_19_1",
                        type = ExerciseType.READING,
                        title = "Публічне читання",
                        instruction = "Читай для уявної аудиторії.",
                        content = ExerciseContent.ReadingText(
                            text = "Шановні друзі! Сьогодні ми зібралися, щоб відзначити важливу подію. Кожен з вас зробив свій внесок у наш спільний успіх. Дякую за вашу відданість, працьовитість та віру в нашу справу. Разом ми здатні на велике!"
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.VOLUME)
                    ),
                    Exercise(
                        id = "ex_19_2",
                        type = ExerciseType.FREE_SPEECH,
                        title = "Імпровізована промова",
                        instruction = "Виголоси коротку промову.",
                        content = ExerciseContent.FreeSpeechTopic(
                            topic = "Моє привітання аудиторії",
                            hints = listOf(
                                "Привітай присутніх",
                                "Подякуй за увагу",
                                "Надихни слухачів"
                            )
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.CONFIDENCE)
                    )
                ),
                estimatedMinutes = 15
            ),
            // Day 20
            Lesson(
                id = "lesson_20",
                courseId = "course_1",
                dayNumber = 20,
                title = "Фінальна перевірка",
                description = "Перевірка всіх навичок",
                theory = TheoryContent(
                    text = "Передостанній день! Сьогодні проходимо всі основні вправи курсу, щоб перевірити прогрес перед фінальним тестом.",
                    tips = listOf(
                        "Будь уважним до деталей",
                        "Не квапся",
                        "Оціни свій прогрес чесно"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_20_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Фінальна скоромовка",
                        instruction = "Покажи все, чому навчився!",
                        content = ExerciseContent.TongueTwister(
                            text = "Краб крабу зробив грабі, подарував грабі крабу: грабай краб грабами гравій",
                            difficulty = 5,
                            targetSounds = listOf("Р", "К", "Б")
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_20_2",
                        type = ExerciseType.READING,
                        title = "Комплексне читання",
                        instruction = "Продемонструй всі навички.",
                        content = ExerciseContent.ReadingText(
                            text = "Мистецтво красномовства вимагає постійної практики. Кожен день приносить нові можливості для вдосконалення. Використовуй їх мудро, і твій голос стане твоїм найпотужнішим інструментом впливу на світ навколо тебе."
                        ),
                        durationSeconds = 120,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.INTONATION)
                    )
                ),
                estimatedMinutes = 15
            ),
            // Day 21
            Lesson(
                id = "lesson_21",
                courseId = "course_1",
                dayNumber = 21,
                title = "Підсумковий тест",
                description = "Фінальна оцінка прогресу",
                theory = TheoryContent(
                    text = "Вітаю з завершенням курсу! Сьогодні — день підсумків. Пройди фінальний тест та порівняй результати з початком курсу. Ти молодець!",
                    tips = listOf(
                        "Пишайся своїм прогресом!",
                        "Продовжуй практикувати",
                        "Чітке мовлення — це навичка на все життя"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_21_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Фінальний тест: скоромовки",
                        instruction = "Вимов всі скоромовки чітко.",
                        content = ExerciseContent.TongueTwister(
                            text = "На дворі трава, на траві дрова. Король орел, орел король. Шишки на шишках.",
                            difficulty = 4,
                            targetSounds = listOf("Р", "Л", "Ш", "Т")
                        ),
                        durationSeconds = 180,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_21_2",
                        type = ExerciseType.FREE_SPEECH,
                        title = "Підсумкова промова",
                        instruction = "Розкажи про свої досягнення.",
                        content = ExerciseContent.FreeSpeechTopic(
                            topic = "Мої 21 день до чіткого мовлення",
                            hints = listOf(
                                "Яким був твій шлях?",
                                "Що ти подолав?",
                                "Які плани на майбутнє?"
                            )
                        ),
                        durationSeconds = 180,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.CONFIDENCE, SkillType.STRUCTURE)
                    )
                ),
                estimatedMinutes = 20
            )
        )
    }

    // ========== COURSE 2: Magic of Intonation ==========

    private fun getCourse2(): Course {
        return IntonationMagicCourse.getCourse()
    }

    // ========== COURSE 3: Сила голосу ==========

    private fun getCourse3(): Course {
        return VoicePowerCourse.getCourse()
    }

    private fun getCourse4(): Course {
        return ConfidentSpeakerCourse.getCourse()
    }

    private fun getCourse5(): Course {
        return CleanSpeechCourse.getCourse()
    }

    private fun getCourse6(): Course {
        return BusinessCommunicationCourse.getCourse()
    }

    private fun getCourse7(): Course {
        return CharismaticSpeakerCourse.getCourse()
    }

    // Helper data class for lesson content
    private data class LessonContent(
        val title: String,
        val description: String,
        val theory: TheoryContent?,
        val exercises: List<Exercise>
    )
}
