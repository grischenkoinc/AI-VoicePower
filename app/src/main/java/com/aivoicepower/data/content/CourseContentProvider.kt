package com.aivoicepower.data.content

import com.aivoicepower.domain.model.course.*
import com.aivoicepower.domain.model.exercise.*
import com.aivoicepower.domain.model.user.SkillType

/**
 * Hardcoded дані курсів
 * Phase 4.1: Тільки перші 7 уроків кожного курсу
 * Phase 8: Додати решту уроків (8-21)
 */
object CourseContentProvider {

    fun getAllCourses(): List<Course> {
        return listOf(
            getCourse1(),
            getCourse2(),
            getCourse3(),
            getCourse4(),
            getCourse5(),
            getCourse6()
        )
    }

    fun getCourseById(id: String): Course? {
        return getAllCourses().find { it.id == id }
    }

    fun getLessonById(courseId: String, lessonId: String): Lesson? {
        return getCourseById(courseId)?.lessons?.find { it.id == lessonId }
    }

    // ========== КУРС 1: Чітке мовлення за 21 день ==========

    private fun getCourse1(): Course {
        return Course(
            id = "course_1",
            title = "Чітке мовлення за 21 день",
            description = "Покращ дикцію та чіткість вимови за 3 тижні. Щоденні вправи зі скоромовками та артикуляцією.",
            iconEmoji = "🗣️",
            totalLessons = 21,
            isPremium = true,  // Перші 7 free, 8-21 premium
            estimatedDays = 21,
            difficulty = Difficulty.BEGINNER,
            skills = listOf(SkillType.DICTION, SkillType.TEMPO),
            lessons = getCourse1Lessons()
        )
    }

    private fun getCourse1Lessons(): List<Lesson> {
        return listOf(
            // День 1
            Lesson(
                id = "lesson_1",
                courseId = "course_1",
                dayNumber = 1,
                title = "Основи артикуляції",
                description = "Знайомство з артикуляційним апаратом та базовими вправами",
                theory = TheoryContent(
                    text = "Чітке мовлення починається з правильної роботи артикуляційного апарату: губ, язика, щелеп та м'якого піднебіння. Сьогодні ми познайомимося з базовими вправами, які допоможуть \"розігріти\" мовленнєвий апарат.",
                    tips = listOf(
                        "Виконуй вправи перед дзеркалом",
                        "Не поспішай, важлива якість, а не швидкість",
                        "Роби вправи щодня для кращого результату"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_1_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: П-Б-П",
                        instruction = "Вимовляй повільно, чітко артикулюючи кожен звук. Поступово збільшуй швидкість.",
                        content = ExerciseContent.TongueTwister(
                            text = "Бик тупогуб, у бика губа тупа",
                            difficulty = 1,
                            targetSounds = listOf("Б", "П", "Г")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_1_2",
                        type = ExerciseType.READING,
                        title = "Читання з паузами",
                        instruction = "Читай текст, роблячи паузи після кожного речення. Контролюй дихання.",
                        content = ExerciseContent.ReadingText(
                            text = "Мистецтво красномовства — це не тільки вміння говорити, але й вміння бути почутим. Кожне слово має значення. Кожна пауза має свій сенс."
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 2
            Lesson(
                id = "lesson_2",
                courseId = "course_1",
                dayNumber = 2,
                title = "Губні звуки",
                description = "Відпрацювання чіткої вимови губних приголосних",
                theory = TheoryContent(
                    text = "Губні звуки (П, Б, М, В, Ф) утворюються за допомогою губ. Для їх чіткої вимови важлива активна робота губних м'язів. Сьогодні будемо тренувати ці звуки через спеціальні скоромовки.",
                    tips = listOf(
                        "Відчуй напругу в губах при вимові",
                        "Не допомагай собі язиком",
                        "Контролюй рівномірність звучання"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_2_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Б-П",
                        instruction = "Чітко розрізняй Б та П. Вони відрізняються тільки вібрацією голосових зв'язок.",
                        content = ExerciseContent.TongueTwister(
                            text = "Купи кіп, купи кіп, купи кіп, купи кіп",
                            difficulty = 2,
                            targetSounds = listOf("П", "К")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_2_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: М-Б",
                        instruction = "Відчуй вібрацію в носі на звуці М.",
                        content = ExerciseContent.TongueTwister(
                            text = "Мамин мамін мамин мамі мамині макарони",
                            difficulty = 2,
                            targetSounds = listOf("М")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 3
            Lesson(
                id = "lesson_3",
                courseId = "course_1",
                dayNumber = 3,
                title = "Язикові звуки",
                description = "Тренування звуків, що утворюються язиком",
                theory = TheoryContent(
                    text = "Язик — найрухливіша частина артикуляційного апарату. Він відповідає за велику кількість звуків: Т, Д, Н, Л, Р та інші. Правильна позиція язика критично важлива для чіткості мовлення.",
                    tips = listOf(
                        "Відчуй кінчик язика",
                        "Не напружуй язик надто сильно",
                        "Контролюй положення язика"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_3_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Т-Д",
                        instruction = "Кінчик язика торкається верхніх зубів.",
                        content = ExerciseContent.TongueTwister(
                            text = "Ткач тче тканини на платтячко Тані",
                            difficulty = 3,
                            targetSounds = listOf("Т", "Д")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_3_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Л",
                        instruction = "Кінчик язика притиснутий до альвеол (горбочки за верхніми зубами).",
                        content = ExerciseContent.TongueTwister(
                            text = "Летіла лелека коло млина, ловила лелека мелену",
                            difficulty = 3,
                            targetSounds = listOf("Л", "М")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 4
            Lesson(
                id = "lesson_4",
                courseId = "course_1",
                dayNumber = 4,
                title = "Свистячі звуки",
                description = "Відпрацювання С, З, Ц",
                theory = TheoryContent(
                    text = "Свистячі звуки (С, З, Ц) утворюються при проходженні повітря через вузьку щілину між язиком та верхніми зубами. Для чіткої вимови важлива правильна форма язика — він має бути широким та плоским.",
                    tips = listOf(
                        "Язик широкий та плоский",
                        "Повітря проходить по центру язика",
                        "Не затискай щелепи"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_4_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: С",
                        instruction = "Повітря має йти плавним потоком, створюючи чистий свистячий звук.",
                        content = ExerciseContent.TongueTwister(
                            text = "Сім синиць на сосні сиділи, си-си-си співали",
                            difficulty = 2,
                            targetSounds = listOf("С")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_4_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: З-С",
                        instruction = "Розрізняй дзвінкий З та глухий С.",
                        content = ExerciseContent.TongueTwister(
                            text = "У лозі лози, у лузі лізе вуж",
                            difficulty = 3,
                            targetSounds = listOf("З", "С", "Л")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 5
            Lesson(
                id = "lesson_5",
                courseId = "course_1",
                dayNumber = 5,
                title = "Шиплячі звуки",
                description = "Відпрацювання Ш, Ж, Ч, Щ",
                theory = TheoryContent(
                    text = "Шиплячі звуки (Ш, Ж, Ч, Щ) вимагають підняття язика до піднебіння та створення ширшої щілини, ніж для свистячих. Ці звуки часто викликають труднощі, тому потребують особливої уваги.",
                    tips = listOf(
                        "Язик у формі \"чашечки\"",
                        "Губи злегка витягнуті вперед",
                        "Повітря виходить широким потоком"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_5_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Ш",
                        instruction = "Відчуй теплий потік повітря на долоні.",
                        content = ExerciseContent.TongueTwister(
                            text = "Шишки на сосні, шашки на столі",
                            difficulty = 2,
                            targetSounds = listOf("Ш", "С")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_5_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Ч-Щ",
                        instruction = "Ч — короткий звук, Щ — довгий.",
                        content = ExerciseContent.TongueTwister(
                            text = "Чіпляла чечевиця чіпку чарку",
                            difficulty = 4,
                            targetSounds = listOf("Ч")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 6
            Lesson(
                id = "lesson_6",
                courseId = "course_1",
                dayNumber = 6,
                title = "Звук Р",
                description = "Особлива увага найскладнішому звуку",
                theory = TheoryContent(
                    text = "Звук Р — один з найскладніших в українській мові. Він утворюється за рахунок вібрації кінчика язика. Навіть якщо ви вимовляєте Р правильно, його відпрацювання покращить загальну чіткість мовлення.",
                    tips = listOf(
                        "Кінчик язика біля альвеол",
                        "Язик розслаблений, але пружний",
                        "Сильний потік повітря викликає вібрацію"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_6_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Р простий",
                        instruction = "Почни повільно, відчуваючи кожну вібрацію.",
                        content = ExerciseContent.TongueTwister(
                            text = "Рано-рано два барани барабанили в барабани",
                            difficulty = 3,
                            targetSounds = listOf("Р", "Б")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    ),
                    Exercise(
                        id = "ex_6_2",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Скоромовка: Р складний",
                        instruction = "Контролюй силу потоку повітря.",
                        content = ExerciseContent.TongueTwister(
                            text = "Тчуть ткачі тканину в Тані на сорочку",
                            difficulty = 4,
                            targetSounds = listOf("Т", "Ч", "Р")
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            ),

            // День 7
            Lesson(
                id = "lesson_7",
                courseId = "course_1",
                dayNumber = 7,
                title = "Комплексні вправи",
                description = "Поєднання всіх звуків у складних скоромовках",
                theory = TheoryContent(
                    text = "Тиждень роботи позаду! Сьогодні закріплюємо все, що вивчили, через комплексні скоромовки, які поєднують різні групи звуків. Це виклик, але ви готові!",
                    tips = listOf(
                        "Не поспішай зі швидкістю",
                        "Якщо збився — почни спочатку",
                        "Записуй себе для самоконтролю"
                    )
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_7_1",
                        type = ExerciseType.TONGUE_TWISTER,
                        title = "Складна скоромовка 1",
                        instruction = "Використовує всі групи звуків. Спочатку по складах!",
                        content = ExerciseContent.TongueTwister(
                            text = "Король — орел, орел — король",
                            difficulty = 4,
                            targetSounds = listOf("Р", "Л", "К", "О")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.DICTION, SkillType.TEMPO)
                    ),
                    Exercise(
                        id = "ex_7_2",
                        type = ExerciseType.FREE_SPEECH,
                        title = "Вільна розповідь",
                        instruction = "Розкажи про свій тиждень тренувань. Стеж за чіткістю.",
                        content = ExerciseContent.FreeSpeechTopic(
                            topic = "Мої успіхи за тиждень",
                            hints = listOf(
                                "Які вправи були найскладнішими?",
                                "Що тобі вдалося покращити?",
                                "Які звуки далися легко?"
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

    // ========== КУРС 2: Магія інтонації ==========

    private fun getCourse2(): Course {
        return Course(
            id = "course_2",
            title = "Магія інтонації",
            description = "Навчись передавати емоції голосом. Виразність та інтонаційне різноманіття.",
            iconEmoji = "🎭",
            totalLessons = 21,
            isPremium = true,
            estimatedDays = 21,
            difficulty = Difficulty.INTERMEDIATE,
            skills = listOf(SkillType.INTONATION, SkillType.VOLUME),
            lessons = getCourse2LessonsPlaceholder()
        )
    }

    private fun getCourse2LessonsPlaceholder(): List<Lesson> {
        // TODO: Phase 8 — додати повний контент
        return (1..7).map { day ->
            Lesson(
                id = "lesson_$day",
                courseId = "course_2",
                dayNumber = day,
                title = "Урок $day: Інтонація (placeholder)",
                description = "Детальний контент буде додано в Phase 8",
                theory = TheoryContent(
                    text = "Теорія про інтонацію та емоції. Буде додано в Phase 8.",
                    tips = listOf("Слухай свій голос", "Експериментуй з емоціями")
                ),
                exercises = listOf(
                    Exercise(
                        id = "ex_${day}_1",
                        type = ExerciseType.EMOTION_READING,
                        title = "Емоційне читання",
                        instruction = "Прочитай текст з емоцією радості.",
                        content = ExerciseContent.ReadingText(
                            text = "Сьогодні чудовий день! Я відчуваю себе чудово.",
                            emotion = Emotion.JOY
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.INTONATION)
                    )
                ),
                estimatedMinutes = 10
            )
        }
    }

    // ========== КУРС 3: Впевнений спікер ==========

    private fun getCourse3(): Course {
        return Course(
            id = "course_3",
            title = "Впевнений спікер",
            description = "Публічні виступи без страху. Структура, аргументація, контакт з аудиторією.",
            iconEmoji = "🎤",
            totalLessons = 21,
            isPremium = true,
            estimatedDays = 21,
            difficulty = Difficulty.INTERMEDIATE,
            skills = listOf(SkillType.CONFIDENCE, SkillType.STRUCTURE),
            lessons = getCourse3LessonsPlaceholder()
        )
    }

    private fun getCourse3LessonsPlaceholder(): List<Lesson> {
        // TODO: Phase 8 — додати повний контент
        return (1..7).map { day ->
            Lesson(
                id = "lesson_$day",
                courseId = "course_3",
                dayNumber = day,
                title = "Урок $day: Публічні виступи (placeholder)",
                description = "Детальний контент буде додано в Phase 8",
                theory = null,
                exercises = listOf(
                    Exercise(
                        id = "ex_${day}_1",
                        type = ExerciseType.FREE_SPEECH,
                        title = "Короткий виступ",
                        instruction = "Розкажи про себе протягом 1 хвилини.",
                        content = ExerciseContent.FreeSpeechTopic(
                            topic = "Моя історія",
                            hints = listOf("Хто ти?", "Чим займаєшся?", "Що тебе надихає?")
                        ),
                        durationSeconds = 90,
                        targetMetrics = listOf(SkillType.CONFIDENCE, SkillType.STRUCTURE)
                    )
                ),
                estimatedMinutes = 10
            )
        }
    }

    // ========== КУРС 4-6: Placeholder ==========

    private fun getCourse4(): Course {
        return Course(
            id = "course_4",
            title = "Чисте мовлення",
            description = "Позбався від слів-паразитів. \"Ну\", \"як би\", \"типу\" більше немає.",
            iconEmoji = "🧹",
            totalLessons = 14,
            isPremium = true,
            estimatedDays = 14,
            difficulty = Difficulty.BEGINNER,
            skills = listOf(SkillType.FILLER_WORDS, SkillType.STRUCTURE),
            lessons = getPlaceholderLessons("course_4", 7)
        )
    }

    private fun getCourse5(): Course {
        return Course(
            id = "course_5",
            title = "Ділова комунікація",
            description = "Переговори, співбесіди, презентації. Мова професіонала.",
            iconEmoji = "💼",
            totalLessons = 20,
            isPremium = true,
            estimatedDays = 20,
            difficulty = Difficulty.ADVANCED,
            skills = listOf(SkillType.STRUCTURE, SkillType.CONFIDENCE),
            lessons = getPlaceholderLessons("course_5", 7)
        )
    }

    private fun getCourse6(): Course {
        return Course(
            id = "course_6",
            title = "Харизматичний оратор",
            description = "Майстер-клас публічних виступів. Просунутий рівень.",
            iconEmoji = "⭐",
            totalLessons = 21,
            isPremium = true,
            estimatedDays = 21,
            difficulty = Difficulty.ADVANCED,
            skills = listOf(SkillType.CONFIDENCE, SkillType.INTONATION, SkillType.STRUCTURE),
            lessons = getPlaceholderLessons("course_6", 7)
        )
    }

    private fun getPlaceholderLessons(courseId: String, count: Int): List<Lesson> {
        return (1..count).map { day ->
            Lesson(
                id = "lesson_$day",
                courseId = courseId,
                dayNumber = day,
                title = "Урок $day (placeholder)",
                description = "Детальний контент буде додано в Phase 8",
                theory = null,
                exercises = listOf(
                    Exercise(
                        id = "ex_${day}_1",
                        type = ExerciseType.READING,
                        title = "Вправа placeholder",
                        instruction = "Буде додано в Phase 8",
                        content = ExerciseContent.ReadingText(
                            text = "Placeholder текст для Phase 8"
                        ),
                        durationSeconds = 60,
                        targetMetrics = listOf(SkillType.DICTION)
                    )
                ),
                estimatedMinutes = 10
            )
        }
    }
}
