package com.aivoicepower.data.content.courses

import android.util.Log
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Difficulty
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.course.TheoryContent
import com.aivoicepower.domain.model.exercise.LessonExercise
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.domain.model.exercise.ExerciseContent
import com.aivoicepower.domain.model.user.SkillType

object VoicePowerCourse {

    const val COURSE_ID = "course_3"
    private const val LESSON_PREFIX = "voice_lesson_"
    private const val EXERCISE_PREFIX = "voice_ex_"

    fun getCourse(): Course {
        return Course(
            id = COURSE_ID,
            title = "Сила голосу",
            description = "Постанова голосу: дихання, резонанс, проекція. Розвинь сильний, красивий, витривалий голос.",
            iconEmoji = "🎤",
            totalLessons = 21,
            isPremium = false,
            estimatedDays = 21,
            difficulty = Difficulty.INTERMEDIATE,
            skills = listOf(
                SkillType.BREATHING,
                SkillType.VOICE_QUALITY,
                SkillType.PROJECTION,
                SkillType.RESONANCE
            ),
            lessons = getLessons()
        )
    }

    fun getLessons(): List<Lesson> {
        val lessons = listOf(
            getLesson1(),
            getLesson2(),
            getLesson3(),
            getLesson4(),
            getLesson5(),
            getLesson6(),
            getLesson7(),
            // Тиждень 2
            getLesson8(),
            getLesson9(),
            getLesson10(),
            getLesson11(),
            getLesson12(),
            getLesson13(),
            getLesson14(),
            // Тиждень 3
            getLesson15(),
            getLesson16(),
            getLesson17(),
            getLesson18(),
            getLesson19(),
            getLesson20(),
            getLesson21()
        )

        // Debug: виводимо довжину теорії для кожного уроку
        lessons.take(7).forEachIndexed { index, lesson ->
            val text = lesson.theory?.text ?: ""
            val theoryLength = text.length
            val first50 = text.take(50).replace("\n", "\\n")
            val last50 = text.takeLast(50).replace("\n", "\\n")
            Log.d("VoicePowerCourse", "Урок ${index + 1}: ${theoryLength} симв, початок='$first50', кінець='$last50'")
        }

        return lessons
    }

    // ===== УРОК 1: Знайомство зі своїм голосом =====

    private fun getLesson1(): Lesson = Lesson(
        id = "${LESSON_PREFIX}1",
        courseId = COURSE_ID,
        dayNumber = 1,
        title = "Знайомство зі своїм голосом",
        description = "Діагностика поточного стану голосу та усвідомлення точки старту",
        theory = TheoryContent(
            text = """
                Твій голос — це музичний інструмент, який ти носиш завжди з собою

                Більшість людей не замислюються про свій голос, поки він не зникне через хворобу або не охрипне після довгого виступу. Але твій голос — це потужний інструмент, який можна тренувати, як м'язи в спортзалі.

                Професійні співаки, актори, викладачі знають: голос потребує догляду та розвитку. Але на щастя, для цього не потрібно народитися з "талантом" — постанова голосу доступна кожному.

                Чому твій голос важливий? Дослідження показують: люди з сильним, впевненим голосом сприймаються як більш компетентні, харизматичні та надійні. Голос впливає на твою кар'єру, особисте життя, здатність переконувати та надихати інших.

                Цікавий факт: Дослідження Університету Дюка показало, що CEO компаній зі списку Fortune 500 у середньому мають голос на 25 Гц нижчий, ніж середньостатистична людина. Глибокий голос підсвідомо асоціюється з авторитетом та надійністю. І це не генетика — більшість з них свідомо або несвідомо "поставили" собі голос.
                
                Що означає "поставлений голос"? Це голос, який:
                — Звучить вільно, без напруги в горлі
                — Не втомлюється після години розмови
                — Чути навіть у великій аудиторії без крику
                — Має приємний тембр та насиченість
                — Легко змінює гучність та висоту

                Типова помилка: думати, що голос — це "дано від природи" і його не змінити. Насправді 80% якості голосу — це техніка: правильне дихання, розслаблення зайвих м'язів, використання резонаторів.

                Сьогодні ми починаємо з діагностики. Ти запишеш свій голос "до" курсу, щоб через 21 день порівняти результат. Більшість людей дивуються: "Невже це я так говорю?" Так, твій голос у записі звучить інакше, ніж "зсередини". І це нормально — адже інші чують саме таку версію.

                Ключовий інсайт: Не суди свій голос зараз. Прийми його як точку старту. Через 3 тижні ти здивуєшся, наскільки він може змінитися — стати сильнішим, глибшим, впевненішим.
            """.trimIndent(),
            tips = listOf(
                "Записуй себе щодня — це найкращий спосіб відслідковувати прогрес",
                "Не порівнюй свій голос з голосами професійних акторів — у них роки тренувань",
                "Навіть якщо тобі не подобається твій голос зараз — це тимчасово, все можна змінити"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_1",
                type = ExerciseType.ARTICULATION,
                title = "Масаж обличчя",
                instruction = "Розігрій м'язи обличчя перед роботою з голосом. Кінчиками пальців робіть кругові рухи: 1) Лоб (15 сек), 2) Скроні (15 сек), 3) Щоки (15 сек), 4) Підборіддя (15 сек). Натискай досить сильно, щоб відчути м'язи.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_2",
                type = ExerciseType.ARTICULATION,
                title = "Розминка губ",
                instruction = "1) Щільно стисни губи, потім розслаб — 10 разів. 2) Витягни губи трубочкою, потім розтягни в посмішку — 10 разів. 3) Покруть губами по колу — спочатку за годинниковою стрілкою 5 разів, потім проти 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_3",
                type = ExerciseType.ARTICULATION,
                title = "Розминка язика",
                instruction = "1) Висунь язик максимально вперед, потім заховай — 10 разів. 2) Дотягнись кінчиком язика до носа, потім до підборіддя — по 5 разів. 3) Води язиком по колу між губами та зубами — 5 кіл в кожну сторону.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_4",
                type = ExerciseType.ARTICULATION,
                title = "Позіхання",
                instruction = "Позіхни 5 разів. Не соромся — роби це повністю, широко розкриваючи рот, відчуваючи як розтягується горло. Позіхання — природний спосіб розслабити голосовий апарат.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_5",
                type = ExerciseType.ARTICULATION,
                title = "Розминка щелеп",
                instruction = "1) Повільно відкривай і закривай рот — 10 разів. 2) Пересунь щелепу вліво-вправо — по 5 разів в кожну сторону. 3) Виконай кругові рухи щелепою — 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_6",
                type = ExerciseType.FREE_SPEECH,
                title = "Діагностичний запис 'до курсу'",
                instruction = "Розкажи про себе протягом 2 хвилин: Хто ти? Чим займаєшся? Чому вирішив покращити голос? Які очікування від курсу? Говори природно, як би розповідав другу. Цей запис — твоя точка відліку.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Моє знайомство з курсом 'Сила голосу'",
                    hints = listOf(
                        "Розслабся, це не іспит — просто говори",
                        "Опиши свій голос: що тобі в ньому подобається, що хочеш змінити",
                        "Які ситуації, де тобі важливий голос: робота, виступи, спілкування?"
                    )
                ),
                durationSeconds = 120,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}1_7",
                type = ExerciseType.READING,
                title = "Читання тексту 'до курсу'",
                instruction = "Прочитай цей текст у звичайному темпі. Не намагайся бути ідеальним — читай так, як читаєш зазвичай. Цей запис потрібен для порівняння з фінальним тестом.",
                content = ExerciseContent.ReadingText(
                    text = "Голос — це дзеркало душі. Він видає наш настрій, впевненість, енергію. Сильний голос — це не обов'язково гучний. Це голос, який чути, який хочеться слухати, який залишається в пам'яті. Постанова голосу змінює не лише те, як тебе чують інші, а й те, як ти відчуваєш себе. Впевнений голос = впевнена людина.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.DICTION, SkillType.TEMPO)
            )
        ),
        estimatedMinutes = 12
    )

    // ===== УРОК 2: Діафрагмальне дихання =====

    private fun getLesson2(): Lesson = Lesson(
        id = "${LESSON_PREFIX}2",
        courseId = COURSE_ID,
        dayNumber = 2,
        title = "Діафрагмальне дихання",
        description = "Основа сильного голосу — правильне дихання животом",
        theory = TheoryContent(
            text = """
                Дихай як немовля — це не жарт, це найкраща порада для голосу
                
                Подивися на немовля, коли воно спить: його живіт плавно піднімається та опускається. Це природне діафрагмальне дихання. А тепер зверни увагу, як дихаєш ти: скоріш за все, твої плечі піднімаються, а живіт майже нерухомий. Це "грудне дихання" — найгірший варіант для голосу.
                
                Що таке діафрагма? Це куполоподібний м'яз, який розділяє грудну клітку та черевну порожнину. Коли діафрагма опускається вниз (при вдиху), легені розширюються і наповнюються повітрям набагато ефективніше, ніж при грудному диханні.
                
                Лайфхак: Хочеш швидко перевірити, чи дихаєш діафрагмою? Зроби це: встань, злегка нахилися вперед (градусів на 45), руки на колінах. Спробуй дихнути. Відчув як живіт сам виштовхнувся вперед? Це діафрагма опустилася вниз. В цій позиції грудьми дихати фізично незручно — організм автоматично перемикається на діафрагмальне дихання. Спробуй говорити в цій позі 30 секунд — запам'ятай відчуття, потім випрямся і спробуй відтворити те саме.

                Чому діафрагмальне дихання критично для голосу?
                — Більше повітря = довші фрази без пауз для вдиху
                — Повітря виходить плавно = голос не тремтить, не переривається
                — Діафрагма створює "опору" голосу = звук сильний без напруги в горлі
                — Розслаблені плечі та шия = голос звучить вільно, тембр багатший
                
                Ще один спосіб перевірити, чи дихаєш ти діафрагмою: ляж на спину, поклади одну руку на груди, другу — на живіт. Дихай. Рука на животі має підніматися та опускатися, а рука на грудях — залишатися майже нерухомою.
                
                Типова помилка: намагатися "надути живіт". Живіт має виходити природно при вдиху — він просто звільняє місце для діафрагми, яка опускається вниз. Не треба спеціально випинати живіт — це створює зайву напругу.
                
                Ще одна помилка: піднімати плечі при вдиху. Плечі мають залишатися розслабленими. Якщо плечі йдуть вгору — ти дихаєш грудьми, не діафрагмою.
                
                Ключовий інсайт: Перші 2-3 дні діафрагмальне дихання буде здаватися незручним і неприродним — адже ти роками дихав інакше. Це нормально. Мозок звикає до нового патерну за 7-14 днів. Після цього воно стане автоматичним.
            """.trimIndent(),
            tips = listOf(
                "Тренуй діафрагмальне дихання лежачи — так легше відчути рух живота",
                "Поклади на живіт книгу — вона має підніматися при вдиху як ліфт",
                "Не форсуй процес — дихай комфортно, без зусиль"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_1",
                type = ExerciseType.ARTICULATION,
                title = "Лежача позиція",
                instruction = "Ляж на спину, зігни ноги в колінах (стопи на підлозі). Одну руку поклади на груди, іншу — на живіт. Просто дихай і спостерігай 30 секунд: яка рука більше рухається?",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 30
                ),
                durationSeconds = 30,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_2",
                type = ExerciseType.BREATHING,
                title = "Базове діафрагмальне дихання",
                instruction = "Залишайся лежати. Вдихай через ніс (4 сек) — відчуй як живіт піднімається. Затримай дихання (2 сек). Видихай через рот (6 сек) — живіт опускається. Плечі та груди нерухомі! Повтори 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4,
                    holdSeconds = 2,
                    exhaleSeconds = 6,
                    cycles = 5,
                    instruction = "Вдих через ніс (живіт вгору) — затримка — видих через рот (живіт вниз)"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_3",
                type = ExerciseType.ARTICULATION,
                title = "Книга на животі",
                instruction = "Ляж, поклади легку книгу на живіт в районі пупка. Дихай діафрагмою — книга має підніматися при вдиху, опускатися при видиху. 10 циклів дихання.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_4",
                type = ExerciseType.ARTICULATION,
                title = "Сидяче дихання",
                instruction = "Сядь рівно, стопи на підлозі, спина пряма. Поклади руки на живіт. Дихай діафрагмою — руки мають рухатися вперед/назад разом з животом. 10 циклів: вдих 4 сек, видих 6 сек.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_5",
                type = ExerciseType.BREATHING,
                title = "Стоячи біля стіни",
                instruction = "Стань спиною до стіни, притулися п'ятами, сідницями, лопатками та потилицею. Дихай діафрагмою: при вдиху відчуєш як нижня частина спини трохи відходить від стіни. 5 циклів: вдих 4 сек, затримка 2 сек, видих 6 сек.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4,
                    holdSeconds = 2,
                    exhaleSeconds = 6,
                    cycles = 5,
                    instruction = "Спина до стіни, відчуй як нижня частина спини трохи відходить при вдиху"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_6",
                type = ExerciseType.FREE_SPEECH,
                title = "Говоріння з діафрагмальним диханням",
                instruction = "Стій або сидь рівно. Зроби глибокий вдих діафрагмою. Розкажи про свій вчорашній день протягом 1 хвилини, намагаючись дихати діафрагмою під час мовлення.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій вчорашній день (з діафрагмальним диханням)",
                    hints = listOf(
                        "Перед кожною фразою — вдих животом",
                        "Відчувай як повітря виходить плавно",
                        "Якщо збився — нічого страшного, це тренування"
                    )
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}2_7",
                type = ExerciseType.READING,
                title = "Читання з контролем дихання",
                instruction = "Прочитай текст, роблячи вдих діафрагмою після кожної крапки. Не поспішай. Відчуй як дихання підтримує голос.",
                content = ExerciseContent.ReadingText(
                    text = "Діафрагмальне дихання — це основа. Без нього всі інші техніки не працюють. Воно дає голосу силу, стабільність, витривалість. Співаки, актори, спікери — всі вони використовують діафрагму. Це не талант, це навичка. І ти вже почав її опановувати.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            )
        ),
        estimatedMinutes = 12
    )

    // ===== УРОК 3: Дихальна опора =====

    private fun getLesson3(): Lesson = Lesson(
        id = "${LESSON_PREFIX}3",
        courseId = COURSE_ID,
        dayNumber = 3,
        title = "Дихальна опора",
        description = "Контроль видиху — голос 'спирається' на дихання",
        theory = TheoryContent(
            text = """
                Голос без опори — як будинок без фундаменту
                
                Ти вже навчився дихати діафрагмою (вдих). Але для сильного голосу важливо не лише скільки повітря ти взяв, а як ти його витрачаєш (видих). Саме тут з'являється поняття "дихальна опора" або просто "опора голосу".
                
                Що таке опора? Уяви: ти тримаєш важкий предмет на витягнутих руках. М'язи живота напружені, утримують корпус стабільним. Подібно працює опора в диханні: м'язи преса та діафрагма контролюють вихід повітря, створюючи стабільну "подушку" для голосу.
                
                Без опори: Повітря виходить безконтрольно, швидко — голос тремтить, слабшає до кінця фрази, горло напружується компенсуючи слабке дихання. Це як намагатися говорити на останньому повітрі — неприємно і для тебе, і для слухачів.
                
                З опорою: Повітря виходить плавно, рівномірно — голос стабільний, сильний, тембр насичений. Горло розслаблене, бо основну роботу робить діафрагма. Можеш говорити довгі фрази без втрати якості звуку.
                
                Як знайти опору? Спробуй це: зроби вдих діафрагмою, потім починай повільно вимовляти звук "Ссссс" (як змія). Відчуєш напругу в м'язах живота — вони стримують повітря, не дають йому вийти занадто швидко. Це і є опора.
                
                Цікавий факт: Італійські оперні співаки використовують термін "appoggio" (опора), який буквально означає "підтримка". Легенда каже, що Енріко Карузо, один із найвидатніших тенорів в історії, міг поставити запалену свічку перед ротом і співати повну арію, не задувши її — такою рівномірною та контрольованою була його опора. Ти можеш не прагнути до оперних висот, але навіть 50% від такого контролю зроблять твій голос набагато сильнішим.

                Типова помилка: затискати горло замість використання опори. Якщо відчуваєш напругу в шиї або горлі при мовленні — це неправильно. Напруга має бути внизу, в районі преса, а горло розслаблене.
                
                Ще помилка: надмірне напруження живота. Опора — це не "залізний прес". Це контрольована, помірна напруга, яка регулює вихід повітря. Якщо перенапружитися — голос стане затиснутим.
                
                Ключовий інсайт: Опора — це баланс. Як водій контролює швидкість педаллю газу, так ти контролюєш силу голосу опорою. Більше опори = сильніший голос. Менше опори = м'якший голос. Але завжди — без напруги в горлі.
            """.trimIndent(),
            tips = listOf(
                "Уяви що співаєш довгу ноту — опора утримує звук стабільним",
                "Поклади руку на живіт: під час мовлення він має бути трохи напружений, але не кам'яний",
                "Якщо горло болить після вправ — ти використовуєш горло замість опори"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_1",
                type = ExerciseType.BREATHING,
                title = "Змія (контроль видиху)",
                instruction = "Глибокий вдих діафрагмою (4 сек). Видихай через рот зі звуком 'Ссссс' якомога довше, але рівномірно. Мета: 15-20 сек. Повтори 3 рази.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4,
                    holdSeconds = 0,
                    exhaleSeconds = 20,
                    cycles = 3,
                    instruction = "Вдих 4 сек, видих 'Ссссс' якомога довше (мінімум 15 сек)"
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_2",
                type = ExerciseType.BREATHING,
                title = "Свічка (плавний видих)",
                instruction = "Уяви перед собою свічку. Вдих діафрагмою. Видихай так, ніби хочеш зробити полум'я горизонтальним, але не задути свічку. 5 циклів: вдих 4 сек, видих 8 сек.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4,
                    holdSeconds = 0,
                    exhaleSeconds = 8,
                    cycles = 5,
                    instruction = "Видих плавний, ніби хилиш полум'я свічки, але не задуваєш"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_3",
                type = ExerciseType.ARTICULATION,
                title = "Опора на звуку 'А'",
                instruction = "Вдих діафрагмою. Вимовляй 'Аааааа' на комфортній ноті якомога довше. Рука на животі — відчуй легку напругу. Горло вільне! Мета: 15-20 секунд. 3 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_4",
                type = ExerciseType.ARTICULATION,
                title = "Градація гучності",
                instruction = "Вдих діафрагмою. Вимовляй 'Аааааа', починаючи тихо, поступово збільшуючи гучність до максимуму, потім знову зменшуючи до тиха. Все на одному диханні! 3 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_5",
                type = ExerciseType.ARTICULATION,
                title = "Рахунок з опорою",
                instruction = "Вдих діафрагмою. Рахуй вголос: 'Один, два, три, чотири...' якомога далі на одному диханні. Норма: 15-20. Мета: 30+. 3 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_6",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з опорою",
                instruction = "Розкажи про свій улюблений фільм або книгу 1-2 хвилини. Перед кожною фразою — вдих діафрагмою. Під час мовлення відчувай легку напругу в животі (опора).",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій улюблений фільм або книга",
                    hints = listOf(
                        "Говори повільніше, ніж зазвичай",
                        "Фокусуйся на відчутті опори в животі",
                        "Якщо закінчується повітря — зупинись, вдихни, продовжуй"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}3_7",
                type = ExerciseType.READING,
                title = "Читання довгих фраз",
                instruction = "Прочитай текст. Спробуй кожне речення вимовити на одному диханні. Відчуй як опора утримує голос стабільним до кінця фрази.",
                content = ExerciseContent.ReadingText(
                    text = "Дихальна опора — це фундамент сильного голосу. Вона дозволяє говорити довго без втоми. Вона робить голос стабільним, насиченим, впевненим. Професійні співаки можуть тримати ноту 30-40 секунд завдяки опорі. Ти теж можеш — просто продовжуй тренуватися.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            )
        ),
        estimatedMinutes = 14
    )

    // ===== УРОК 4: Глибина дихання =====

    private fun getLesson4(): Lesson = Lesson(
        id = "${LESSON_PREFIX}4",
        courseId = COURSE_ID,
        dayNumber = 4,
        title = "Глибина дихання",
        description = "Збільшення об'єму легень — більше повітря, довші фрази",
        theory = TheoryContent(
            text = """
                Більше повітря — це не про розмір легень, а про те, наскільки повно ти їх використовуєш
                
                Більшість людей використовують лише 20-30% об'єму своїх легень при звичайному диханні. Це достатньо для життя, але катастрофічно мало для голосу. Уяви машину, яка їздить постійно на першій передачі — технічно вона рухається, але потенціал розкрито на 10%.
                
                Глибоке дихання — це не про те, щоб наповнити легені "під завʼязку" за секунду. Це про те, щоб свідомо, повільно наповнити їх максимально: спочатку нижню частину (діафрагма опускається, живіт виходить), потім середню (ребра розширюються в боки), і нарешті верхню (ключиці трохи піднімаються, але плечі все ще розслаблені).
                
                Чому це важливо для голосу? Довгі фрази — основа переконливого мовлення. Коли тобі не вистачає повітря, ти робиш багато коротких вдихів — це розриває думку, звучить нервово, слухачі втрачають нитку розповіді. З глибоким диханням ти можеш вимовити 15-20 слів на одному диханні легко, без задишки.
                
                Цікавий факт: Професійні фридайвери можуть затримувати дихання на 5-8 хвилин. Їхній секрет не в розмірі легень (вони майже такі самі, як у пересічної людини), а в тренуванні ефективності дихання. Вони навчилися використовувати 100% об'єму легень та максимально економно витрачати кисень. Для голосу нам не потрібні 8 хвилин, але принцип той самий: максимум використання, мінімум втрат.
                
                Типова помилка: вдихати занадто швидко. Швидкий вдих = поверхневий вдих. Легені не встигають наповнитися повністю. Правильний глибокий вдих займає 4-6 секунд — це може здаватися вічністю, але саме так легені заповнюються повністю.
                
                Ще помилка: піднімати плечі при глибокому вдиху. Коли плечі йдуть вгору — це сигнал, що ти "добираєш" повітря грудьми, а не легенями. Плечі мають залишатися внизу навіть при максимальному вдиху.
                
                Ключовий інсайт: Глибоке дихання — це навичка, яка розвивається з часом. Перші дні буде незвично дихати так довго. Через тиждень це стане природним. Через два тижні ти помітиш: у розмовах більше не "задихаєшся" на довгих фразах.
            """.trimIndent(),
            tips = listOf(
                "Тренуй глибоке дихання вранці — легені найбільш розслаблені",
                "Уяви що наповнюєш легені як склянку водою: спочатку дно, потім середина, потім верх",
                "Не намагайся зразу вдихнути 'максимум' — це створює напругу"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_1",
                type = ExerciseType.BREATHING,
                title = "Триступеневий вдих",
                instruction = "Вдих розділи на 3 частини по 2 секунди: 1) Низ — живіт вперед. 2) Середина — ребра в боки. 3) Верх — ключиці вгору. Затримка 2 сек. Видих 8 сек. 5 разів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 6,
                    holdSeconds = 2,
                    exhaleSeconds = 8,
                    cycles = 5,
                    instruction = "Вдих на 3 етапи: низ (2 сек) → середина (2 сек) → верх (2 сек)"
                ),
                durationSeconds = 80,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_2",
                type = ExerciseType.BREATHING,
                title = "Повний вдих 6-3-9",
                instruction = "Вдих через ніс 6 секунд (повільно, заповнюючи низ→середину→верх). Затримка 3 секунди. Видих через рот 9 секунд. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 6,
                    holdSeconds = 3,
                    exhaleSeconds = 9,
                    cycles = 5,
                    instruction = "Повне наповнення легень: 6 сек вдих, 3 сек затримка, 9 сек видих"
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_3",
                type = ExerciseType.ARTICULATION,
                title = "Руки на ребрах",
                instruction = "Поклади долоні на ребра з боків. Глибоко вдихни — відчуй як ребра розширюються в боки, руки розходяться. 10 циклів дихання.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_4",
                type = ExerciseType.ARTICULATION,
                title = "Дихання зі звуком 'Ш'",
                instruction = "Глибокий вдих 6 секунд. Видихай зі звуком 'Шшшш' якомога довше. Мета: мінімум 20 секунд. 3 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_5",
                type = ExerciseType.ARTICULATION,
                title = "Довгий рахунок",
                instruction = "Глибокий вдих 6 секунд. Рахуй вголос: 'Один, два, три...' якомога далі на одному диханні. З глибоким диханням: 25-35 чисел. 3 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_6",
                type = ExerciseType.FREE_SPEECH,
                title = "Довга розповідь без пауз",
                instruction = "Глибокий вдих 6 секунд. Розкажи про своє хобі якомога довше на одному диханні. Норма: 15-20 сек. Мета: 25+ сек. 2 спроби.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Моє хобі або улюблене заняття (довга фраза)",
                    hints = listOf(
                        "Глибокий вдих перед початком — не поспішай",
                        "Говори трохи повільніше, ніж зазвичай",
                        "Якщо закінчується повітря — добре, ти дійшов до межі"
                    )
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}4_7",
                type = ExerciseType.READING,
                title = "Читання довгих речень",
                instruction = "Прочитай текст. Кожне речення намагайся вимовити на одному диханні. Перед кожним реченням — повний вдих 6 секунд.",
                content = ExerciseContent.ReadingText(
                    text = "Глибоке дихання — це основа витривалості голосу. Професійні спікери можуть говорити 30-40 секунд без вдиху завдяки правильній техніці. Це не магія, це тренування. Чим більше ти практикуєш глибоке дихання, тим легше стає говорити довго, переконливо, без задишки. Твої легені вже стають сильнішими — продовжуй!",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 5: Дихання в мовленні =====

    private fun getLesson5(): Lesson = Lesson(
        id = "${LESSON_PREFIX}5",
        courseId = COURSE_ID,
        dayNumber = 5,
        title = "Дихання в мовленні",
        description = "Інтеграція правильного дихання в природне мовлення",
        theory = TheoryContent(
            text = """
                Правильне дихання — це не вправа, а спосіб життя твого голосу
                
                Перші чотири дні ти тренував техніку дихання окремо. Ти знаєш як дихати діафрагмою, як тримати опору, як робити глибокий вдих. Але є велика різниця між вправами в тиші і реальним мовленням. Коли ти говориш, думки біжать швидко, емоції змінюються, темп зростає — і техніка дихання може "загубитися".
                
                Секрет професіоналів: вони дихають правильно не тому, що "думають про це", а тому що це стало автоматичною звичкою. Актор на сцені не думає "зараз зроблю вдих діафрагмою" — він просто дихає так завжди. Співак не контролює кожен вдих свідомо — тіло вже знає як.
                
                Де робити паузи для вдиху? Логічні паузи в мовленні — це розділові знаки: крапки, коми, тире. Але не всі коми вимагають вдиху — лише ті, де природна пауза в змісті. Наприклад: "Я думаю, [тут можна вдихнути] що це хороша ідея." Або: "Вчора, [вдих] коли я йшов додому, [вдих] сталася цікава історія."
                
                Лайфхак: Візьми будь-який текст (статтю, уривок з книги). Прочитай його про себе і олівцем познач місця, де б ти природно зробив паузу/вдих. Зазвичай це кінці речень та довгі фрази. Тепер прочитай вголос, роблячи вдих саме в цих місцях. Відчуєш: мовлення стає структурованішим, легшим для сприйняття. Хаотичні вдихи посеред слова або фрази руйнують логіку — плановані вдихи її підсилюють.
                
                Типова помилка: намагатися говорити якомога довше без вдиху. Це призводить до того, що наприкінці фрази повітря закінчується, голос слабшає, останні слова "проковтуються". Краще зробити вдих раніше і закінчити фразу сильно, ніж "дотягти" до кінця на останньому повітрі.
                
                Ще помилка: поспішні, шумні вдихи. Коли ти вдихаєш різко через рот між словами — це чутно слухачам як "задишка". Правильний вдих під час мовлення: швидкий (1-2 сек), через рот або ніс, тихий, діафрагмою. Практикуй це окремо: вдих за 1 секунду діафрагмою (без звуку!), потім одразу продовжуй говорити.
                
                Ключовий інсайт: Перші тижні ти будеш свідомо думати "зараз вдих". Це нормально. Через місяць правильне дихання стане автоматичним — тіло само підкаже коли і як вдихнути. Головне — не кидати практику, навіть коли здається що "не виходить". Виходить — просто потрібен час.
            """.trimIndent(),
            tips = listOf(
                "Під час розмов з друзями свідомо роби паузи для вдиху",
                "Якщо збиваєшся — не панікуй, просто зупинись, вдихни, продовжуй",
                "Записуй себе щодня — так ти почуєш де вдихи природні"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_1",
                type = ExerciseType.ARTICULATION,
                title = "Швидкий тихий вдих",
                instruction = "Практикуй швидкий вдих діафрагмою за 1 секунду. Вдихни через рот або ніс, живіт вперед, БЕЗ звуку. Повтори 10 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 30
                ),
                durationSeconds = 30,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_2",
                type = ExerciseType.ARTICULATION,
                title = "Фраза — вдих — фраза",
                instruction = "Промовляй короткі фрази зі швидким вдихом між ними: 'Сьогодні гарний день.' [вдих 1 сек] 'Я йду на прогулянку.' [вдих 1 сек] Повтори 5 фраз на свій вибір.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_3",
                type = ExerciseType.ARTICULATION,
                title = "Читання з позначками",
                instruction = "Візьми будь-який текст. Про себе прочитай і уявно познач місця для вдиху. Тепер прочитай вголос, роблячи вдих саме в цих місцях.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.STRUCTURE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_4",
                type = ExerciseType.ARTICULATION,
                title = "Довга фраза + пауза",
                instruction = "Вимовляй вголос: 'Я навчаюся правильно дихати, щоб мій голос став сильнішим, впевненішим і витривалішим.' — все на одному диханні. [Пауза 2 сек, вдих]. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.STRUCTURE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_5",
                type = ExerciseType.ARTICULATION,
                title = "Імпровізація з паузами",
                instruction = "Говори вголос на тему 'Мої плани на вихідні' 1 хвилину. Свідомо роби паузи для вдиху після кожної завершеної думки.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.STRUCTURE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_6",
                type = ExerciseType.READING,
                title = "Читання з планованими вдихами",
                instruction = "Прочитай текст, роблячи вдих тільки після крапок та у місцях, позначених '/'. Кожен вдих — швидкий (1 сек), діафрагмальний, тихий.",
                content = ExerciseContent.ReadingText(
                    text = "Правильне дихання в мовленні — це як пунктуація в тексті. / Воно структурує думки, / робить мовлення зрозумілим, / дає слухачам час осмислити сказане. Паузи для вдиху — це не слабкість. / Це професіоналізм. Найкращі спікери світу роблять паузи майстерно. / Ти теж навчишся — просто продовжуй практикувати.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.STRUCTURE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}5_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з контрольованим диханням",
                instruction = "Розкажи про свій типовий робочий день 1-2 хвилини. Свідомо контролюй дихання: перед кожною новою думкою — вдих діафрагмою.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій типовий день",
                    hints = listOf(
                        "Структуруй розповідь: ранок → день → вечір",
                        "Після опису кожної частини дня — пауза та вдих",
                        "Не поспішай, говори так, ніби пояснюєш другу"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.STRUCTURE, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 12
    )

    // ===== УРОК 6: Розслаблення голосового апарату =====

    private fun getLesson6(): Lesson = Lesson(
        id = "${LESSON_PREFIX}6",
        courseId = COURSE_ID,
        dayNumber = 6,
        title = "Розслаблення голосового апарату",
        description = "Зняття затисків — вільний голос без напруги",
        theory = TheoryContent(
            text = """
                Найбільший ворог красивого голосу — це затискання м'язів, про яке ти навіть не здогадуєшся
                
                Уяви гітару з перетягнутими струнами — звук буде різкий, неприємний, струни можуть навіть лопнути. Твої голосові зв'язки працюють подібно: коли навколишні м'язи (горло, щелепа, язик, шия) напружені — голос стає затиснутим, швидко втомлюється, може навіть охрипнути.
                
                Більшість людей мають хронічне напруження в голосовому апараті і не помічають цього. Ти звик говорити "через силу" — і тобі здається це нормальним. Але професійні співаки, актори знають: вільний голос = розслаблені м'язи. Звук має виходити легко, без опору, без відчуття "стискання" в горлі.
                
                Звідки береться затискання? Причин багато: стрес (ти підсвідомо напружуєш шию), поганий сон (м'язи не розслабилися за ніч), неправильна постава (голова вперед = шия напружена), спроби говорити гучно через горло замість діафрагми. Навіть холодна погода може спровокувати затиски — м'язи "закриваються", захищаючи горло.
                
                Цікавий факт: Дослідження в Королівській академії музики Лондона показало, що співаки перед виступом мають підвищену напругу м'язів шиї — до 40% вище норми. Але після 10 хвилин спеціальної розминки (масаж, позіхання, м'які звуки) напруга знижується до нормального рівня, і якість голосу різко покращується. Ти не співак, але принцип той самий: розслаблений апарат = якісний звук.
                
                Як зрозуміти, що в тебе затиск? Ознаки: 1) Після години розмови горло втомлюється або болить. 2) Голос до кінця дня стає хрипким. 3) Відчуваєш "ком" у горлі під час мовлення. 4) Шия напружена, плечі піднесені. 5) Щелепа стиснута навіть коли мовчиш.
                
                Типова помилка: ігнорувати розминку. Спортсмени не біжать марафон без розминки — м'язи треба підготувати. Голосовий апарат — це теж м'язи. Хочеш говорити 2 години без втоми? Зроби 5 хвилин розминки перед цим: масаж, позіхання, м'які звуки. Результат тебе здивує.
                
                Ще помилка: розслаблятися тільки перед "важливими" подіями. Розслаблення має бути щоденною звичкою. Ранковий масаж обличчя та шиї (2 хвилини), кілька позіхань протягом дня, м'які гудіння — і твій голос завжди буде в тонусі, без перенапруги.
                
                Ключовий інсайт: Розслаблення — це не пасивність. Це активне усунення зайвої напруги. Ти не "даєш м'язам обвиснути" — ти прибираєш те, що заважає вільному звуку. Опора в животі залишається, а горло, щелепа, язик — розслаблюються.
            """.trimIndent(),
            tips = listOf(
                "Перевіряй щелепу протягом дня: якщо зуби стиснені — розслаб",
                "Позіхай 5 разів перед будь-яким важливим мовленням",
                "Якщо відчуваєш напругу в горлі — промукай 'м-м-м' 10 секунд"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_1",
                type = ExerciseType.ARTICULATION,
                title = "Масаж шиї",
                instruction = "Кінчиками пальців масажуй шию з обох боків від підборіддя до ключиць. Кругові рухи, помірний натиск. 60 секунд.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_2",
                type = ExerciseType.ARTICULATION,
                title = "Масаж щелепи",
                instruction = "Поклади пальці на щелепні суглоби (перед вухами). Повільно відкривай та закривай рот. Потім робіть кругові рухи пальцями — 30 секунд.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_3",
                type = ExerciseType.ARTICULATION,
                title = "Позіхання (5 разів)",
                instruction = "Позіхни 5 разів повністю, широко розкриваючи рот. Відчуй як розтягується горло, м'язи розслабляються.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_4",
                type = ExerciseType.ARTICULATION,
                title = "М'яке гудіння 'М-м-м'",
                instruction = "Закрий рот, губи розслаблені. Промукай 'М-м-м' на низькій ноті 15 секунд. Звук має вібрувати в губах та носі. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_5",
                type = ExerciseType.ARTICULATION,
                title = "'Сонна' вимова",
                instruction = "Вимовляй слова 'Доброго ранку, як справи, чудовий день' максимально ліниво, розслаблено. Повтори 5 разів, кожен раз ще 'сонніше'.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_6",
                type = ExerciseType.READING,
                title = "Читання з розслабленим апаратом",
                instruction = "Спочатку зроби 3 позіхання та помукай 'М-м-м' 10 секунд. Тепер прочитай текст. Мета: відчути різницю — голос звучить м'якше, приємніше.",
                content = ExerciseContent.ReadingText(
                    text = "Розслаблений голос — це не слабкий голос. Це голос без зайвої напруги. Він звучить природно, приємно, довго не втомлюється. Професіонали знають: сила голосу йде від діафрагми, а горло лише пропускає звук. Якщо горло затиснуте — звук застряє. Розслаб його — і голос політь.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}6_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з перевіркою затисків",
                instruction = "Розкажи про свій улюблений фільм 1-2 хвилини. Під час мовлення перевіряй: 1) Щелепа розслаблена? 2) Плечі внизу? 3) Шія вільна?",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій улюблений фільм (з контролем розслабленості)",
                    hints = listOf(
                        "Говори повільніше, ніж зазвичай",
                        "Якщо відчуваєш напругу в горлі — промукай 'М-м-м' і продовжуй",
                        "Уяви що розповідаєш другу у затишній обстановці"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 12
    )

    // ===== УРОК 7: Підсумок тижня 1 =====

    private fun getLesson7(): Lesson = Lesson(
        id = "${LESSON_PREFIX}7",
        courseId = COURSE_ID,
        dayNumber = 7,
        title = "Підсумок тижня 1: Фундамент закладено",
        description = "Комплексна практика всіх технік дихання та розслаблення",
        theory = TheoryContent(
            text = """
                Перший тиждень позаду — ти вже не той самий спікер, що тиждень тому
                
                Можливо, ти ще не відчуваєш кардинальної різниці. Це нормально — голосові звички змінюються не за день. Але фундамент вже закладено: ти знаєш як дихати діафрагмою, як тримати опору, як розслабляти голосовий апарат. Це база, на якій будується все інше.
                
                Що ти вже вмієш?
                — Діафрагмальне дихання: живіт рухається, плечі нерухомі.
                — Дихальна опора: м'язи живота утримують повітря, горло розслаблене.
                — Глибоке дихання: повне заповнення легень, довгі фрази без задишки.
                — Інтеграція дихання в мовлення: плановані вдихи, структуроване мовлення.
                — Розслаблення: зняття затисків, вільний звук.
                
                Це може здаватися "просто вправами", але насправді ти вже зробив величезний крок. Більшість людей говорять грудьми, затискають горло, задихаються на довгих фразах — і не розуміють чому голос втомлюється. Ти тепер розумієш і вмієш інакше.
                
                Цікавий факт: Нейробіологи з Стенфорда виявили, що для формування нової моторної звички (такої як правильне дихання) мозку потрібно 21-30 днів регулярної практики. Перші 7 днів — найважчі: мозок опирається змінам, старі патерни "тягнуть назад". Але після тижня нові нейронні зв'язки вже починають формуватися. Ти пройшов найскладнішу частину — не зупиняйся зараз!
                
                Що робити далі? Наступні два тижні ми будемо розвивати резонанси (тембр голосу), проекцію (гучність без крику), витривалість. Але всі ці техніки працюють тільки на правильному диханні — без діафрагми та опори нічого не вийде. Тому продовжуй практикувати вправи з першого тижня щодня, навіть коли перейдемо далі.
                
                Типова помилка: кинути базові вправи після першого тижня. "Я вже вмію дихати діафрагмою, навіщо повторювати?" — думає людина і через тиждень повертається до старих звичок. Правда в тому, що навіть професійні співаки роблять дихальні вправи перед кожним виступом. Це як чистити зуби — не одноразова дія, а щоденна звичка.
                
                Ще помилка: очікувати миттєвих результатів. "Тиждень минув, а голос не став як у Моргана Фрімана." Розслабся. Зміни йдуть поступово, але вони йдуть. Порівняй свій запис з уроку 1 і запис сьогодні — навіть якщо різниця невелика, вона є. Через два тижні буде ще помітніше. Через місяць — ти здивуєшся.
                
                Ключовий інсайт: Постанова голосу — це марафон, не спринт. Перший тиждень — фундамент. Другий — стіни. Третій — дах. Не можна побудувати дім за день, але кожен день наближає до результату. Ти вже на правильному шляху — просто продовжуй крокувати.
            """.trimIndent(),
            tips = listOf(
                "Кожен ранок 5 хвилин дихальних вправ — це як кава, ритуал для голосу",
                "Записуй себе раз на тиждень — так ти побачиш прогрес",
                "Не намагайся бути ідеальним — навіть 70% техніки дають результат"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_1",
                type = ExerciseType.ARTICULATION,
                title = "Повна розминка",
                instruction = "1) Масаж обличчя — 30 сек. 2) Масаж шиї — 30 сек. 3) Масаж щелепних суглобів — 15 сек. 4) Позіхання 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_2",
                type = ExerciseType.BREATHING,
                title = "Комплекс дихальних вправ",
                instruction = "1) Базове діафрагмальне 4-2-6 (5 циклів). 2) Глибоке 6-3-9 (3 цикли). 3) Змія 'Ссссс' (2 спроби). 4) Довгий 'Аааааа' (2 спроби).",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 6,
                    holdSeconds = 3,
                    exhaleSeconds = 9,
                    cycles = 3,
                    instruction = "Комплекс усіх дихальних вправ тижня"
                ),
                durationSeconds = 180,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_3",
                type = ExerciseType.ARTICULATION,
                title = "Рахунок з глибоким диханням",
                instruction = "Глибокий вдих 6 секунд. Рахуй вголос: 'Один, два, три...' якомога далі. Мета: 30+ чисел. 2 спроби.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_4",
                type = ExerciseType.ARTICULATION,
                title = "М'яке гудіння для резонансу",
                instruction = "Промукай 'М-м-м' на низькій ноті 20 секунд. Відчуй вібрацію в губах, носі, грудях. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_5",
                type = ExerciseType.READING,
                title = "Контрольне читання тижня",
                instruction = "Прочитай текст, використовуючи всі техніки тижня. Порівняй з записом уроку 1.",
                content = ExerciseContent.ReadingText(
                    text = "Перший тиждень — фундамент сильного голосу. Ти навчився дихати правильно, утримувати опору, розслабляти голосовий апарат. Це може здаватися простим, але саме ці навички відрізняють професіонала від аматора. Продовжуй практикувати щодня — і через два тижні твій голос зміниться назавжди.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_6",
                type = ExerciseType.FREE_SPEECH,
                title = "Контрольна розповідь тижня",
                instruction = "Розкажи про те, що ти вивчив цього тижня, що змінилося, які вправи сподобалися найбільше. 1-2 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій перший тиждень курсу 'Сила голосу'",
                    hints = listOf(
                        "Які вправи були найскладнішими?",
                        "Що ти вже відчуваєш іншим у своєму голосі?",
                        "Які плани на наступні тижні?"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE, SkillType.STRUCTURE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}7_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Запис 'після першого тижня'",
                instruction = "Розкажи про себе, як у уроці 1, але тепер використовуючи всі техніки. 2 хвилини. Збережи цей запис для порівняння.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Про себе (після першого тижня курсу)",
                    hints = listOf(
                        "Говори спокійно, впевнено",
                        "Використовуй глибоке дихання та опору",
                        "Це не іспит — просто розповідай"
                    )
                ),
                durationSeconds = 120,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 15
    )

    // ==================== ТИЖДЕНЬ 2 ====================

    // ===== УРОК 8: Грудний резонатор =====

    private fun getLesson8(): Lesson = Lesson(
        id = "${LESSON_PREFIX}8",
        courseId = COURSE_ID,
        dayNumber = 8,
        title = "Грудний резонатор",
        description = "Теплота й глибина голосу через вібрацію в грудях",
        theory = TheoryContent(
            text = """
                Резонатори — це підсилювачі твого голосу, вбудовані в тіло

                Голосові зв'язки самі по собі видають слабкий, тихий звук — щось на кшталт дзижчання. Те, що ми чуємо як "голос", насправді формується резонаторами — порожнинами тіла, які підсилюють і забарвлюють початковий звук зв'язок. Це як різниця між гітарною струною окремо і струною на корпусі гітари: сам звук той самий, але корпус робить його багатим і об'ємним.

                У тебе є кілька основних резонаторів: грудний (низькі, теплі частоти), головний (високі, дзвінкі), носовий/масковий (середні, польотні). Сьогодні почнемо з грудного — найглибшого і найпотужнішого.

                Що таке грудний резонатор? Це вібрація звуку в грудній клітці — ти можеш відчути її, поклавши руку на груди і промовивши низький звук. Грудний резонанс додає голосу теплоти, глибини, авторитетності. Саме він асоціюється з "довірливим", "заспокійливим" голосом дикторів новин і психологів.

                Як знайти грудний резонанс? Промов звук "О" або "М" на найнижчій комфортній ноті — не форсуй, не тисни. Поклади руку на грудину. Якщо звук правильний, ти відчуєш легку вібрацію під рукою, майже як тихе двигтіння.

                Цікавий факт: акустичні дослідження голосу свідчать, що грудний резонанс формується не в самих легенях (звук фізично туди не потрапляє), а через кістковий резонанс грудної клітки — вібрація голосових зв'язок передається через трахею й кістки, а ми відчуваємо це як "вібрацію в грудях". Відчуття цілком реальне, хоча звук технічно народжується вище, у гортані.

                Типова помилка: намагатися "продавити" грудний резонанс силою — говорити навмисно низько й голосно, напружуючи горло. Це дає протилежний ефект: звук стає затиснутим, а не глибоким. Грудний резонанс — про розслаблення й правильну вібрацію, не про силу.

                Ще помилка: плутати грудний резонанс з "хрипким" голосом. Хрипота — ознака напруги чи втоми зв'язок, а не резонансу. Справжній грудний резонанс — чистий, теплий, без хрипоти.

                Ключовий інсайт: грудний резонанс — фундамент "довірливого" тембру, але сам по собі він не робить голос сильним. Це один інструмент з кількох — ти навчишся комбінувати резонатори пізніше цього тижня.
            """.trimIndent(),
            tips = listOf(
                "Клади руку на груди під час вправ — вібрація підкаже, чи ти на правильному шляху",
                "Не форсуй низькі ноти — грудний резонанс про вібрацію, не про силу",
                "Тренуй на голосних 'О' та 'М' — вони найкраще проявляють грудну вібрацію"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_1",
                type = ExerciseType.ARTICULATION,
                title = "Вібрація на 'М'",
                instruction = "Промукай 'Ммммм' на найнижчій комфортній ноті, поклавши руку на грудину. Відчуй вібрацію під рукою. Тримай 10 секунд. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_2",
                type = ExerciseType.ARTICULATION,
                title = "Низьке 'О'",
                instruction = "Вимов 'Оооо' на низькій ноті, спокійно, без напруги. Рука на грудях відчуває вібрацію. 5 повторів по 5 секунд.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_3",
                type = ExerciseType.BREATHING,
                title = "Вдих і грудний резонанс",
                instruction = "Вдихни діафрагмою (4 сек). На видиху (6 сек) промукай низьке 'Ммммм', тримаючи вібрацію в грудях. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 6, cycles = 5,
                    instruction = "Вдих 4 сек, видих з 'М-м-м' у грудному резонансі"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_4",
                type = ExerciseType.ARTICULATION,
                title = "Слова з грудним резонансом",
                instruction = "Промов слова 'дім', 'сон', 'вдома', 'спокій' на низькій ноті, відчуваючи грудну вібрацію на кожному слові. По 3 рази кожне слово.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_5",
                type = ExerciseType.ARTICULATION,
                title = "Порівняння резонансів",
                instruction = "Промов 'Привіт' звичайним голосом, потім навмисно з акцентом на грудний резонанс (нижче, з вібрацією в грудях). Відчуй різницю в теплоті звучання. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_6",
                type = ExerciseType.READING,
                title = "Читання з грудним резонансом",
                instruction = "Прочитай текст, тримаючи теплий, низький грудний резонанс на кожному реченні.",
                content = ExerciseContent.ReadingText(
                    text = "Довіра починається з голосу. Теплий, глибокий тон заспокоює співрозмовника ще до того, як прозвучали слова. Грудний резонанс — це звучання впевненості й спокою.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}8_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з грудним резонансом",
                instruction = "Розкажи про найспокійніший момент свого дня, тримаючи теплий грудний резонанс. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Найспокійніший момент мого дня",
                    hints = listOf(
                        "Говори повільно, низьким тоном",
                        "Тримай руку на грудях, перевіряй вібрацію",
                        "Уяви що заспокоюєш друга"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 9: Головний резонатор =====

    private fun getLesson9(): Lesson = Lesson(
        id = "${LESSON_PREFIX}9",
        courseId = COURSE_ID,
        dayNumber = 9,
        title = "Головний резонатор",
        description = "Дзвінкість і польотність голосу без напруги горла",
        theory = TheoryContent(
            text = """
                Дзвінкість голосу народжується не в горлі, а в голові

                Якщо грудний резонанс дає голосу теплоту й глибину, то головний резонанс дає протилежне — яскравість, дзвінкість, здатність "летіти" далеко без крику. Той самий принцип, тільки вібрація зміщується вгору: у кістки черепа, пазухи, тверде піднебіння.

                Головний резонанс особливо важливий на високих нотах і в ситуаціях, де голос має бути чути здалеку — виступ у великій залі, розмова на вулиці, викладання. Без нього високий голос звучить пронизливо й неприємно; з ним — дзвінко й яскраво.

                Як знайти головний резонанс? Промов високий звук "І" або носовий "Нг" (як у слові "дзвінок") і відчуй, чи вібрує щось у ділянці носа, лоба, маківки. Спробуй "промукати" звук просто в ніс, ніби наспівуєш із закритим ротом — вібрація має відчуватись у "масці" обличчя.

                Цікавий факт: вокальні педагоги часто використовують образ "звук летить у лоб" саме тому, що фізично верхня частина черепних пазух і кісток резонує на високих частотах, підсилюючи їх — це не метафора, а акустична реальність, підтверджена дослідженнями голосового апарату оперних співаків.

                Типова помилка: тягнутись до головного резонансу горлом — напружуючи зв'язки, "видавлюючи" високий звук. Це дає писклявий, неприємний тон замість дзвінкого. Головний резонанс досягається розслабленням і спрямуванням звуку вгору, а не силою.

                Ще помилка: плутати головний резонанс із фальцетом. Фальцет — інший режим роботи зв'язок (тонший, слабший звук), тоді як головний резонанс може бути присутній і в звичайному, повноцінному голосі — просто з акцентом вібрації вище.

                Ключовий інсайт: сильний, гнучкий голос використовує обидва резонатори — грудний для теплоти, головний для дзвінкості. Завтра ти навчишся їх змішувати.
            """.trimIndent(),
            tips = listOf(
                "Шукай вібрацію в ділянці носа й лоба — це ознака правильного головного резонансу",
                "Не тягни звук вгору горлом — розслабся і спрямуй його думкою, не силою",
                "Тренуй на носовому 'Нг' — цей звук найшвидше показує головний резонанс"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_1",
                type = ExerciseType.ARTICULATION,
                title = "Носове гудіння",
                instruction = "Промукай 'Нг-нг-нг' (як у слові 'дзвінок'), відчуваючи вібрацію в носі та лобі. 10 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_2",
                type = ExerciseType.ARTICULATION,
                title = "Високе 'І'",
                instruction = "Вимов звук 'Іііі' на високій, але комфортній ноті, спрямовуючи звук у ділянку лоба, не напружуючи горло. 5 повторів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_3",
                type = ExerciseType.BREATHING,
                title = "Дзвінке гудіння на видиху",
                instruction = "Вдих 4 сек. На видиху (6 сек) промукай 'М-нг' у високому регістрі, відчуваючи вібрацію в масці обличчя. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 6, cycles = 5,
                    instruction = "Вдих 4 сек, видих з дзвінким 'М-нг' у високому регістрі"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_4",
                type = ExerciseType.ARTICULATION,
                title = "Слова з головним резонансом",
                instruction = "Промов слова 'дзвін', 'сміх', 'ранок', 'легкість' у високому, дзвінкому тоні. По 3 рази кожне.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_5",
                type = ExerciseType.ARTICULATION,
                title = "Порівняння грудного й головного",
                instruction = "Промов 'Привіт' спочатку з акцентом на грудний резонанс (низько, тепло), потім на головний (вище, дзвінко). Відчуй різницю. Повтори 5 разів, чергуючи.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_6",
                type = ExerciseType.READING,
                title = "Читання з дзвінким тоном",
                instruction = "Прочитай текст із яскравим, дзвінким головним резонансом.",
                content = ExerciseContent.ReadingText(
                    text = "Дзвінкий голос летить далеко без крику. Він пробуджує увагу, звучить свіжо й енергійно. Головний резонанс — секрет ораторів, які чути навіть у найбільшій залі.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}9_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з дзвінким голосом",
                instruction = "Розкажи про щось, що тебе нещодавно порадувало, використовуючи дзвінкий головний резонанс. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Щось, що мене нещодавно порадувало",
                    hints = listOf(
                        "Говори трохи вище і яскравіше, ніж зазвичай",
                        "Уяви що ділишся новиною з великою групою людей",
                        "Слідкуй, щоб голос не ставав пронизливим"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 10: Маска резонансу =====

    private fun getLesson10(): Lesson = Lesson(
        id = "${LESSON_PREFIX}10",
        courseId = COURSE_ID,
        dayNumber = 10,
        title = "Маска резонансу",
        description = "Фокус звуку в ділянці обличчя для яскравості без напруги",
        theory = TheoryContent(
            text = """
                "Маска" — місце, де голос знаходить максимальну ефективність

                "Маска" — термін з вокальної педагогіки, що позначає ділянку обличчя навколо носа, вилиць і верхньої губи — там, де за формою нагадує театральну маску. Коли звук "спрямований у маску", він отримує максимальну яскравість і польотність при мінімальних зусиллях зв'язок.

                Це не окремий, третій резонатор поруч із грудним і головним — це швидше техніка фокусування звуку, комбінація правильної артикуляції та резонансу, яка робить голос "зібраним" і чутним здалеку, без напруги в горлі.

                Як відчути маску? Промов протяжне "Мммм-нн-нн", тримаючи губи зімкнутими, і зверни увагу, де відчувається найбільша вібрація — зазвичай це ділянка навколо носа й верхньої губи. Тепер спробуй відкрити рот на "Ма" — намагайся зберегти те саме відчуття вібрації в тій самій зоні.

                Цікавий факт: вокальні педагоги часто радять учням "посміхатися зсередини" під час співу чи мовлення — це не про вираз обличчя, а про підняте м'яке піднебіння й активні м'язи обличчя, які й формують правильне розташування маски. Легка внутрішня "посмішка" фізично змінює форму резонуючих порожнин і покращує фокусування звуку.

                Типова помилка: плутати "маску" з посмішкою на обличчі — це не про міміку, а про внутрішнє відчуття підняття та фокусування звуку. Можна тримати нейтральний вираз обличчя й при цьому правильно "потрапляти в маску".

                Ще помилка: занадто сильно "тиснути" звук у ніс — це дає гугнявий, неприємний тон замість дзвінкого. Маска — про фокус, а не про носовість.

                Ключовий інсайт: голос "у масці" — найефективніший режим для тривалого мовлення: він дзвінкий, чутний здалеку, і водночас не втомлює горло, бо основна робота йде через резонанс, а не силу зв'язок.
            """.trimIndent(),
            tips = listOf(
                "Шукай відчуття вібрації навколо носа й верхньої губи — це і є 'маска'",
                "Уяви легку 'внутрішню посмішку' — вона допомагає піднебінню зайняти правильне положення",
                "Не тисни звук у ніс — мета фокус, а не гугнявість"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_1",
                type = ExerciseType.ARTICULATION,
                title = "Знаходимо маску",
                instruction = "Промукай 'Мммм-нн-нн' із зімкнутими губами 10 секунд, шукаючи вібрацію навколо носа й верхньої губи. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_2",
                type = ExerciseType.ARTICULATION,
                title = "Ма-ме-мі-мо-му",
                instruction = "Відкрий рот на 'Ма-ме-мі-мо-му', намагаючись зберегти те саме відчуття вібрації в масці, що й на закритому 'М'. Повтори 5 циклів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_3",
                type = ExerciseType.ARTICULATION,
                title = "Внутрішня посмішка",
                instruction = "Промов речення 'Сьогодні чудовий день' з легкою внутрішньою 'посмішкою' (піднятим піднебінням), без зміни виразу обличчя. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_4",
                type = ExerciseType.BREATHING,
                title = "Фокус звуку на видиху",
                instruction = "Вдих 4 сек. На видиху (6 сек) промов 'Ма-ма-ма', утримуючи фокус звуку в масці. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 6, cycles = 5,
                    instruction = "Вдих 4 сек, видих з 'Ма-ма-ма', фокус звуку в масці"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_5",
                type = ExerciseType.ARTICULATION,
                title = "Слова у масці",
                instruction = "Промов слова 'мама', 'намисто', 'ранок', 'малина' з фокусом звуку в масці — дзвінко, легко. По 3 рази кожне слово.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_6",
                type = ExerciseType.READING,
                title = "Читання з фокусом у масці",
                instruction = "Прочитай текст, тримаючи фокус звуку в масці — дзвінко й легко, без напруги.",
                content = ExerciseContent.ReadingText(
                    text = "Голос у масці летить далеко, не втомлюючи горло. Це відчуття фокусу, зібраності звуку. Знайшовши його раз, ти зможеш повертатися до нього щоразу, коли потрібна дзвінкість без напруги.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}10_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з фокусом у масці",
                instruction = "Розкажи, яким уявляєш свій ідеальний вихідний, тримаючи фокус звуку в масці. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Як я уявляю свій ідеальний вихідний",
                    hints = listOf(
                        "Тримай легку внутрішню посмішку",
                        "Перевіряй вібрацію навколо носа",
                        "Говори дзвінко, але без напруги"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 11: Змішаний резонанс =====

    private fun getLesson11(): Lesson = Lesson(
        id = "${LESSON_PREFIX}11",
        courseId = COURSE_ID,
        dayNumber = 11,
        title = "Змішаний резонанс",
        description = "Баланс усіх резонаторів залежно від ситуації мовлення",
        theory = TheoryContent(
            text = """
                Найсильніший голос — це не один резонатор, а їх поєднання

                Ти вивчив три елементи: грудний резонанс (теплота), головний резонанс (дзвінкість), фокус у масці (польотність). Поодинці кожен дає особливий ефект, але справжня сила голосу — у вмінні змішувати їх залежно від ситуації.

                Що таке змішаний резонанс? Це баланс вібрації між грудьми, маскою і головою одночасно — голос, який одночасно теплий і дзвінкий, глибокий і чіткий. Саме такий голос сприймається як "поставлений" — не крайність в один бік, а гармонійне поєднання.

                Коли який резонанс переважає? У розмові з близькою людиною природно домінує грудний (теплота, довіра). У виступі перед великою аудиторією потрібно більше головного і маскового резонансу (дзвінкість, чутність). Хороший спікер вміє плавно переключати баланс залежно від контексту, не втрачаючи жодного елементу повністю.

                Як тренувати змішування? Почни з протяжного звуку, у якому свідомо "перетікаєш" від грудного відчуття до головного і назад, не втрачаючи опору й вібрацію в жодній точці. Це схоже на змішування фарб — потрібна практика, щоб відчути пропорції.

                Цікавий факт: вокальні педагоги називають цю навичку "мікс" (mixed voice) і вважають її найскладнішим елементом постановки голосу — саме тому, що вимагає одночасного контролю кількох незалежних м'язових груп. Але коли навичка сформована, вона стає автоматичною і не потребує свідомих зусиль.

                Типова помилка: намагатися утримувати "максимум" усіх резонаторів одночасно — це створює напругу і звучить неприродно. Змішаний резонанс — про баланс, гнучку пропорцію, а не про суму максимумів.

                Ключовий інсайт: голос — це не набір окремих технік, а єдина система. Резонатори працюють разом, підтримуючи одне одного через правильне дихання й розслаблення, які ти вже опанував.
            """.trimIndent(),
            tips = listOf(
                "Уяви змішування фарб — резонанси комбінуються в пропорціях, а не додаються максимумами",
                "У теплій розмові — більше грудного тону, у виступі — більше маски й голови",
                "Не форсуй усі резонатори одразу — шукай баланс, а не суму"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_1",
                type = ExerciseType.ARTICULATION,
                title = "Перехід грудний-головний",
                instruction = "Промов протяжне 'Аааааа', плавно переміщуючи відчуття вібрації від грудей до голови і назад, не перериваючи звук. 5 повторів по 10 секунд.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_2",
                type = ExerciseType.ARTICULATION,
                title = "Баланс на реченні",
                instruction = "Промов 'Я радий тебе бачити' з балансом теплоти (грудний) і дзвінкості (маска/голова) одночасно. Повтори 5 разів, шукаючи відчуття балансу.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_3",
                type = ExerciseType.BREATHING,
                title = "Мікс на видиху",
                instruction = "Вдих 4 сек. На видиху (8 сек) тримай протяжний звук 'Оооо', змішуючи грудну теплоту й маскову дзвінкість. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, видих 8 сек зі змішаним резонансом на 'Оооо'"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_4",
                type = ExerciseType.ARTICULATION,
                title = "Контекстне переключення",
                instruction = "Промов 'Привіт, як справи?' спершу тепло-грудним тоном (розмова з другом), потім дзвінко-масковим (звернення до великої групи). Відчуй різницю контексту. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_5",
                type = ExerciseType.ARTICULATION,
                title = "Довге речення зі змішаним резонансом",
                instruction = "Промов 'Сьогодні я хочу поділитися дечим важливим' одним диханням, тримаючи баланс усіх резонаторів. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_6",
                type = ExerciseType.READING,
                title = "Читання зі змішаним резонансом",
                instruction = "Прочитай текст, тримаючи баланс теплоти і дзвінкості одночасно.",
                content = ExerciseContent.ReadingText(
                    text = "Найкращий голос — не найглибший і не найдзвінкіший, а найбільш збалансований. Змішаний резонанс дає теплоту довіри і чіткість, яку чути здалеку. Це вершина техніки, яку ти вже майже опанував.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}11_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь зі змішаним резонансом",
                instruction = "Розкажи, яку пораду дав би собі рік тому, тримаючи баланс усіх резонаторів. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Порада, яку я дав би собі рік тому",
                    hints = listOf(
                        "Почни тепло, по-дружньому",
                        "На головних думках додай трохи дзвінкості",
                        "Слідкуй за балансом, не форсуй жоден резонатор"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 12: Проекція голосу =====

    private fun getLesson12(): Lesson = Lesson(
        id = "${LESSON_PREFIX}12",
        courseId = COURSE_ID,
        dayNumber = 12,
        title = "Проекція голосу",
        description = "Далекочутність без крику через опору й резонанс",
        theory = TheoryContent(
            text = """
                Проекція — це відстань, на яку долітає твій голос, без крику

                Проекція голосу — здатність бути почутим на відстані чи в шумному приміщенні без напруги горла й без крику. Це не про гучність саму по собі, а про ефективність: скільки енергії голосу реально долітає до слухача, а скільки губиться.

                Гучний голос не дорівнює голосу з хорошою проекцією. Можна кричати і бути погано чутним (звук "розсіюється"), а можна говорити відносно тихо, але з хорошою проекцією — і бути чутним у великій залі.

                З чого складається проекція? По-перше, опора дихання (ти вже це вмієш) — вона дає стабільну енергію звуку. По-друге, резонанс (грудний, головний, маска) — він "фокусує" звук в один напрямок замість розсіювання. По-третє, чітка артикуляція — недбала вимова "з'їдає" гучність, навіть якщо голос сильний.

                Як тренувати проекцію? Уяви, що тобі треба, щоб людина в кінці великої кімнати почула кожне слово — але при цьому ти не кричиш, а "посилаєш" звук вперед за допомогою опори та резонансу. Спробуй сказати фразу так, ніби направляєш її в конкретну точку на стіні навпроти.

                Цікавий факт: актори класичного театру (до появи мікрофонів) тренувалися проектувати голос на зали в тисячі місць без жодного підсилення. Секрет був у комбінації опори дихання, резонансу і чіткої артикуляції — а не в силі крику, яка швидко зриває голос.

                Типова помилка: підвищувати гучність за рахунок горла — форсувати звук зв'язками. Це швидко втомлює голос і навіть може призвести до зриву. Проекція йде від діафрагми й резонансу, горло лише пропускає звук.

                Ще помилка: губити чіткість заради гучності — говорити голосно, але нерозбірливо. Слухач, який не розуміє слів, не почує тебе, навіть якщо звук гучний.

                Ключовий інсайт: хороша проекція — результат роботи всієї системи, яку ти вже тренував: дихання, опора, резонанс, артикуляція. Сьогодні ти вчишся об'єднувати все це в напрямлений, далекочутний звук.
            """.trimIndent(),
            tips = listOf(
                "Уявляй конкретну точку, куди 'посилаєш' звук — це допомагає фокусувати проекцію",
                "Проекція йде від опори та резонансу, а не від крику горлом",
                "Чітка артикуляція критична — нерозбірливий гучний звук не долітає до розуміння"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_1",
                type = ExerciseType.BREATHING,
                title = "Опора для проекції",
                instruction = "Вдих 4 сек, затримка 2 сек. На видиху (8 сек) тримай стабільний звук 'Аааа' з опорою. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 2, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, затримка 2 сек, видих 8 сек зі стабільним звуком з опорою"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.PROJECTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_2",
                type = ExerciseType.ARTICULATION,
                title = "Посилання звуку в точку",
                instruction = "Обери точку на стіні навпроти. Промов 'Привіт!' так, ніби направляєш звук саме в цю точку, не криком, а фокусом. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.PROJECTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_3",
                type = ExerciseType.ARTICULATION,
                title = "Проекція на відстань",
                instruction = "Уяви співрозмовника на відстані 10 метрів. Промов 'Ти мене чуєш?' так, щоб воно долетіло без крику — з опорою і резонансом. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_4",
                type = ExerciseType.ARTICULATION,
                title = "Чітка артикуляція на проекції",
                instruction = "Промов фразу 'Три чорних кроти рили нору' чітко і з проекцією одночасно — кожен звук має долетіти розбірливо. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_5",
                type = ExerciseType.ARTICULATION,
                title = "Проекція без крику",
                instruction = "Промов 'Зупиніться, будь ласка!' спочатку криком (відчуй напругу в горлі), потім з правильною проекцією (опора + резонанс, без крику). Відчуй різницю. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_6",
                type = ExerciseType.READING,
                title = "Читання з проекцією",
                instruction = "Прочитай текст, спрямовуючи звук вперед через опору й резонанс, без крику.",
                content = ExerciseContent.ReadingText(
                    text = "Справжня проекція — це не крик, а точність. Голос, спрямований опорою і резонансом, долітає далі, ніж голос, підсилений лише зусиллям горла. Тренуй фокус, а не гучність.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}12_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з проекцією",
                instruction = "Уяви, що робиш оголошення для великої групи людей. Говори з проекцією, без крику. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Оголошення для великої групи людей",
                    hints = listOf(
                        "Уяви що звертаєшся до залу на 50 людей",
                        "Тримай опору і резонанс, не переходь на крик",
                        "Говори трохи повільніше, ніж у звичайній розмові"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 13: Гучність без крику =====

    private fun getLesson13(): Lesson = Lesson(
        id = "${LESSON_PREFIX}13",
        courseId = COURSE_ID,
        dayNumber = 13,
        title = "Гучність без крику",
        description = "Свідома динаміка гучності без напруги голосових зв'язок",
        theory = TheoryContent(
            text = """
                Гучність — це навичка контролю, а не м'язове зусилля горла

                Учора ти навчився проекції — далекочутності без крику. Сьогодні ідемо далі: як свідомо регулювати гучність голосу в широкому діапазоні — від тихого до по-справжньому голосного — без жодного напруження зв'язок.

                Чому крик шкідливий? Коли ти кричиш, зв'язки стикаються з надмірною силою й швидкістю — це може викликати набряк, вузлики, тимчасову чи навіть постійну хрипоту. Учителі, тренери, батьки маленьких дітей — усі, хто говорить голосно щодня, ризикують "зірвати" голос, якщо покладаються на крик горлом замість техніки.

                Як збільшити гучність без крику? Джерело гучності — не горло, а комбінація трьох речей: сильніший видих з опорою (більше повітря під контролем = гучніший звук), активніший резонанс (фокус звуку підсилює сприйняту гучність без збільшення фізичної сили), відкритіший рот і активна артикуляція (звук виходить вільніше).

                Цікавий факт: акустичні виміри показують, що досвідчені актори можуть збільшити гучність голосу на 10-15 децибел без збільшення напруги зв'язок — весь приріст іде за рахунок резонансу й опори дихання. Для порівняння, крик горлом дає менший приріст гучності, але значно більше навантаження на зв'язки.

                Типова помилка: пов'язувати гучність виключно з силою видиху, ігноруючи резонанс. Сильний видих без фокусу дає "розмитий" гучний звук, який швидко втомлює.

                Ще помилка: підвищувати гучність різко, без розігріву. Як і в спорті, голосовий апарат потребує поступового переходу від тихого до гучного — різкий стрибок ризикований для зв'язок.

                Ключовий інсайт: гучність без крику — це навичка керування опорою, резонансом і артикуляцією одночасно. Хто опанував це, може говорити голосно цілий день без жодної втоми голосу.
            """.trimIndent(),
            tips = listOf(
                "Збільшуй гучність через опору дихання й резонанс, а не через горло",
                "Розігрівайся поступово — не стрибай одразу до максимальної гучності",
                "Якщо після гучного мовлення болить горло — це сигнал переглянути техніку"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_1",
                type = ExerciseType.ARTICULATION,
                title = "Драбинка гучності",
                instruction = "Промов 'Ааа' п'ятьма рівнями гучності — від пошепки до голосного, без напруги горла на жодному рівні. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_2",
                type = ExerciseType.BREATHING,
                title = "Опора для гучності",
                instruction = "Вдих 4 сек. На видиху (8 сек) поступово збільшуй гучність звуку 'Оооо', тримаючи опору. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, видих 8 сек з поступовим наростанням гучності"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_3",
                type = ExerciseType.ARTICULATION,
                title = "Гучно без горла",
                instruction = "Промов 'Слухайте всі!' голосно, але через опору й резонанс — перевір, чи не напружене горло. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.PROJECTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_4",
                type = ExerciseType.ARTICULATION,
                title = "Контроль динаміки",
                instruction = "Промов речення 'Я хочу, щоб мене почули' спершу тихо, потім поступово голосніше в межах одного речення, без зривів. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_5",
                type = ExerciseType.ARTICULATION,
                title = "Гучність з чіткою артикуляцією",
                instruction = "Промов скоромовку 'Бик тупогуб, тупогубенький бичок' голосно, зберігаючи чіткість кожного звуку. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.DICTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_6",
                type = ExerciseType.READING,
                title = "Читання з контрольованою гучністю",
                instruction = "Прочитай текст, свідомо контролюючи гучність через опору й резонанс, без напруги горла.",
                content = ExerciseContent.ReadingText(
                    text = "Гучний голос без напруги — це результат техніки, а не сили. Опора дихання й резонанс дають об'єм звуку, який чути далеко, залишаючи горло вільним і розслабленим цілий день.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}13_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь із гучністю без напруги",
                instruction = "Уяви, що оголошуєш важливу новину колективу. Говори голосно, але без напруги горла. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Оголошення важливої новини колективу",
                    hints = listOf(
                        "Говори голосніше, ніж зазвичай, але через опору",
                        "Перевіряй горло — воно має лишатися розслабленим",
                        "Використовуй резонанс для підсилення звуку"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.PROJECTION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 14
    )

    // ===== УРОК 14: Підсумок тижня 2 =====

    private fun getLesson14(): Lesson = Lesson(
        id = "${LESSON_PREFIX}14",
        courseId = COURSE_ID,
        dayNumber = 14,
        title = "Підсумок тижня 2",
        description = "Комплексна практика резонансу, проекції та гучності",
        theory = TheoryContent(
            text = """
                Другий тиждень — від дихання до потужного, дзвінкого голосу

                Вітаю з другим тижнем курсу "Сила голосу"! Якщо перший тиждень заклав фундамент (дихання, опора, розслаблення), то другий додав те, що робить голос по-справжньому сильним і красивим: резонанс і проекцію.

                Що ти вже вмієш:
                Урок 8: Грудний резонатор — теплота й глибина, вібрація в грудях
                Урок 9: Головний резонатор — дзвінкість і польотність, вібрація в масці й голові
                Урок 10: Маска резонансу — фокус звуку навколо носа й верхньої губи
                Урок 11: Змішаний резонанс — баланс усіх резонаторів залежно від ситуації
                Урок 12: Проекція голосу — далекочутність без крику через опору й резонанс
                Урок 13: Гучність без крику — контрольована динаміка гучності без напруги горла

                Це вже серйозний технічний арсенал. Більшість людей все життя говорять одним і тим самим тоном, не підозрюючи, що можна свідомо керувати теплотою, дзвінкістю, дальністю й гучністю голосу.

                Цікавий факт: голосові педагоги стверджують, що повне освоєння резонансної техніки в співі чи мовленні зазвичай займає місяці, а то й роки регулярної практики — ти пройшов базові принципи всього за тиждень. Це не означає ідеальне володіння, але фундамент закладено правильно.

                Що далі? Останній тиждень курсу буде про застосування: витривалість голосу протягом дня, тембр і діапазон, експресивність, поведінка голосу в стресі — і фінальний випускний тест.

                Типова помилка: після інтенсивного тижня різко кинути практику резонансу, повернувшись до старих звичок. Резонансні відчуття закріплюються тільки регулярним повторенням — продовжуй хоча б короткі вправи щодня.

                Ключовий інсайт: сила голосу — це не один прийом, а система з дихання, опори, розслаблення, резонансу й проекції, що працюють разом. Ти вже володієш усіма компонентами — залишається зібрати їх в єдине ціле на останньому тижні.
            """.trimIndent(),
            tips = listOf(
                "Практикуй пошук резонансу щодня хоча б 3-5 хвилин — навичка закріплюється повторенням",
                "Записуй себе раз на тиждень і порівнюй з попередніми записами",
                "Останній тиждень найважчий і найцікавіший — не зупиняйся зараз"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_1",
                type = ExerciseType.ARTICULATION,
                title = "Розминка резонансу",
                instruction = "1) 'Ммм' грудний резонанс, руки на грудях, 10 сек. 2) 'Нг' головний резонанс, 10 сек. 3) 'Ма-ме-мі' у масці, 10 сек.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 90),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_2",
                type = ExerciseType.BREATHING,
                title = "Опора тижня",
                instruction = "Комплексна вправа на опору: вдих 4 сек, затримка 2 сек, видих 8 сек зі звуком 'Оооо'. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 2, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, затримка 2 сек, видих 8 сек з опорою"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_3",
                type = ExerciseType.ARTICULATION,
                title = "Змішаний резонанс — повторення",
                instruction = "Промов 'Я готовий до нового тижня' з балансом грудного й головного резонансу одночасно. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_4",
                type = ExerciseType.ARTICULATION,
                title = "Проекція — повторення",
                instruction = "Промов 'Мене чути здалеку' так, ніби направляєш звук у далеку точку кімнати, без крику. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.PROJECTION)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_5",
                type = ExerciseType.ARTICULATION,
                title = "Гучність — повторення",
                instruction = "Промов 'Слухайте уважно' на трьох рівнях гучності — тихо, середньо, голосно — без напруги горла на жодному. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_6",
                type = ExerciseType.READING,
                title = "Контрольне читання тижня 2",
                instruction = "Прочитай текст, застосовуючи резонанс і проекцію одночасно.",
                content = ExerciseContent.ReadingText(
                    text = "Резонанс і проекція — це те, що перетворює звичайний голос на інструмент впливу. Теплота грудного тону, дзвінкість головного, фокус маски, сила без крику — усе це тепер частина твого арсеналу. Другий тиждень завершено.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.PROJECTION, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з усіма техніками тижня",
                instruction = "Розкажи, що нового відчув у своєму голосі за цей тиждень, свідомо використовуючи резонанс і проекцію. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Що я відчув нового у своєму голосі за цей тиждень",
                    hints = listOf(
                        "Спробуй усвідомлено використати резонанс і проекцію",
                        "Говори про конкретні відчуття, а не загальні фрази",
                        "Не поспішай, тримай опору дихання"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.PROJECTION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}14_8",
                type = ExerciseType.FREE_SPEECH,
                title = "Запис 'після другого тижня'",
                instruction = "Розкажи про себе, як у уроці 1, але тепер свідомо використовуючи резонанс і проекцію. 2 хвилини. Збережи цей запис для порівняння.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Про себе (після другого тижня курсу)",
                    hints = listOf(
                        "Порівняй із записом дня 1 подумки",
                        "Використай резонанс і проекцію свідомо",
                        "Говори природно, як у розмові з другом"
                    )
                ),
                durationSeconds = 120,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.PROJECTION, SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 16
    )

    // ==================== ТИЖДЕНЬ 3 ====================

    // ===== УРОК 15: Витривалість голосу =====

    private fun getLesson15(): Lesson = Lesson(
        id = "${LESSON_PREFIX}15",
        courseId = COURSE_ID,
        dayNumber = 15,
        title = "Витривалість голосу",
        description = "Як говорити довго без втоми та зривів голосу",
        theory = TheoryContent(
            text = """
                Голос, який не втомлюється за годину — не талант, а тренована витривалість

                Уяви два сценарії: вчитель, який після шести уроків поспіль ще звучить енергійно, і людина, чий голос сідає вже після 20 хвилин розмови. Різниця не в силі голосових зв'язок від природи, а у витривалості, яку можна тренувати так само, як витривалість м'язів у спорті.

                Що виснажує голос найшвидше? Говоріння через горло замість опори (зв'язки перевантажуються), поверхневе дихання (часті вдихи, нестабільний звук), затиснуті м'язи шиї й щелепи (додаткова напруга), і банально — недостатнє зволоження (суха слизова швидше подразнюється).

                Як тренувати витривалість? Головний принцип той самий, що в спорті: поступове збільшення навантаження. Замість того, щоб одразу говорити годину поспіль, тренуй короткі сесії з правильною технікою (опора, резонанс, розслаблення), поступово збільшуючи тривалість.

                Цікавий факт: логопеди й фоніатри (лікарі, що спеціалізуються на голосі) відзначають, що вчителі — одна з професій із найвищим ризиком розвитку вузликів на голосових зв'язках, саме через багатогодинне щоденне навантаження без належної техніки. При цьому вчителі, навчені правильної постановки голосу, мають значно нижчий ризик голосових проблем за роки кар'єри.

                Типова помилка: компенсувати втому голосу підвищенням гучності через горло — це прискорює виснаження ще більше. Коли голос втомлюється, потрібно, навпаки, зменшити навантаження на зв'язки й більше покладатися на опору й резонанс.

                Ще помилка: ігнорувати водний баланс. Слизова оболонка голосових зв'язок працює краще зволоженою — вода (не кава чи газовані напої) підтримує голос протягом дня.

                Ключовий інсайт: витривалість голосу — не про "терпіти" втому, а про правильну техніку, яка не дає втомі накопичуватись. Чим краще ти володієш диханням, опорою й резонансом, тим довше можеш говорити без втрати якості.
            """.trimIndent(),
            tips = listOf(
                "Пий воду протягом дня — зволожена слизова витриваліша",
                "Якщо голос втомлюється — зменш навантаження на горло, збільш опору й резонанс",
                "Тренуй витривалість поступово: короткі сесії з технікою краще за довгі без неї"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_1",
                type = ExerciseType.ARTICULATION,
                title = "Довга вправа на опорі",
                instruction = "Промов протяжне 'Аааааа' з опорою, тримаючи стабільну гучність і тон якомога довше без напруги. Мета: 20+ секунд. 3 спроби.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 90),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_2",
                type = ExerciseType.BREATHING,
                title = "Дихання для витривалості",
                instruction = "Комплекс дихальних циклів для розвитку витривалості голосу: вдих 4 сек, затримка 2 сек, видих 8 сек. 6 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 2, exhaleSeconds = 8, cycles = 6,
                    instruction = "Вдих 4 сек, затримка 2 сек, видих 8 сек — для розвитку витривалості"
                ),
                durationSeconds = 85,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_3",
                type = ExerciseType.READING,
                title = "П'ять хвилин безперервного читання",
                instruction = "Прочитай будь-який текст вголос протягом 3-5 хвилин, стежачи за технікою: опора, резонанс, розслаблене горло. Якщо відчуваєш втому — зроби паузу, а не форсуй.",
                content = ExerciseContent.ReadingText(
                    text = "Голос — це витривалість, яку тренують поступово. Кожна хвилина усвідомленого мовлення з правильною технікою зміцнює навичку. Не поспішай — дай собі час звикнути до тривалого навантаження.",
                    emotion = null
                ),
                durationSeconds = 180,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_4",
                type = ExerciseType.ARTICULATION,
                title = "Перевірка втоми",
                instruction = "Після попередньої вправи оціни: чи болить горло? Чи змінився тембр? Промов кілька слів і зверни увагу на власні відчуття.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 30),
                durationSeconds = 30,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_5",
                type = ExerciseType.ARTICULATION,
                title = "Відновлювальна розминка",
                instruction = "Позіхни 5 разів, промасажуй шию 30 секунд, промукай 'М-м-м' на низькій ноті 15 секунд — віднови голос після навантаження.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_6",
                type = ExerciseType.READING,
                title = "Читання на витривалість",
                instruction = "Прочитай текст, зберігаючи стабільну техніку до останнього слова.",
                content = ExerciseContent.ReadingText(
                    text = "Витривалий голос — результат щоденної практики, а не природний дар. Опора дихання, вільне горло, правильний резонанс дозволяють говорити годинами без втоми. Це навичка, яку тренує кожен професійний спікер.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}15_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Довга розповідь на витривалість",
                instruction = "Розкажи детально про подорож або пригоду 2-3 хвилини без зупинки, стежачи за технікою.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Детальна розповідь про подорож або пригоду",
                    hints = listOf(
                        "Говори 2-3 хвилини без зупинки",
                        "Слідкуй за технікою, а не швидкістю",
                        "Якщо відчуваєш втому — зменш гучність, не зупиняйся різко"
                    )
                ),
                durationSeconds = 150,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.BREATHING, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 16: Тембр голосу =====

    private fun getLesson16(): Lesson = Lesson(
        id = "${LESSON_PREFIX}16",
        courseId = COURSE_ID,
        dayNumber = 16,
        title = "Тембр голосу",
        description = "Розкриваємо багатство власного, унікального звучання",
        theory = TheoryContent(
            text = """
                Тембр — це "колір" голосу, який робить тебе впізнаваним

                Тембр — унікальне забарвлення голосу, завдяки якому ти впізнаєш людину по телефону ще до того, як вона представиться. Це комбінація резонансів, форми голосового тракту й індивідуальних особливостей зв'язок — частково вроджена, але значною мірою піддається свідомому формуванню.

                Чому тембр важливий? Приємний, насичений тембр викликає довіру й бажання слухати довше. Різкий, металевий чи "плаский" тембр, навпаки, підсвідомо втомлює слухача, навіть якщо зміст мовлення цікавий.

                Як покращити тембр? Тембр збагачується через: резонанс (більше резонаторів задіяно — багатший звук), розслаблення (затиснутий апарат дає "плаский", бідний тембр), артикуляційну чіткість (розмита вимова "змиває" тембральні відтінки), емоційну залученість (байдужий тон завжди звучить бідніше за щирий).

                Цікавий факт: акустичний аналіз голосу показує, що приємний, "теплий" тембр зазвичай має багатий спектр обертонів (додаткових частот понад основний тон) — саме обертони, а не сама висота голосу, визначають, чи звучить голос "багато" й приємно. Резонанс безпосередньо збільшує кількість і силу цих обертонів.

                Типова помилка: намагатися штучно "поставити" тембр, копіюючи чийсь голос (диктора, актора). Це звучить награно й нещиро. Тембр покращується через техніку (резонанс, розслаблення), а не через імітацію чужого голосу.

                Ще помилка: недооцінювати вплив емоційного стану на тембр. Втомлений, байдужий, роздратований стан звучить у голосі навіть коли слова нейтральні — тембр видає внутрішній стан.

                Ключовий інсайт: твій тембр вже унікальний і не потребує заміни — потрібно лише прибрати те, що його "приглушує" (напруга, поверхневе дихання, слабкий резонанс), і він розкриється повністю.
            """.trimIndent(),
            tips = listOf(
                "Не копіюй чужий тембр — розкривай власний через техніку",
                "Емоційна залученість збагачує тембр більше, ніж будь-яка вправа",
                "Записуй себе в різних емоційних станах і слухай різницю в тембрі"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_1",
                type = ExerciseType.ARTICULATION,
                title = "Обертони на голосній",
                instruction = "Промов протяжне 'Ааааа', намагаючись 'відкрити' звук максимально — відчуй, як змінюється насиченість тембру при різному ступені розслаблення. 5 повторів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_2",
                type = ExerciseType.ARTICULATION,
                title = "Тембр у різних станах",
                instruction = "Промов 'Доброго ранку' втомлено, потім енергійно, потім щиро-тепло. Відчуй, як тембр змінюється залежно від внутрішнього стану. Повтори 3 рази кожен варіант.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_3",
                type = ExerciseType.ARTICULATION,
                title = "Насичення резонансом",
                instruction = "Промов речення 'Мій голос стає багатшим' спочатку плоским тоном, потім з повним резонансом (груди+маска+голова). Відчуй різницю в насиченості. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_4",
                type = ExerciseType.BREATHING,
                title = "Розслаблення для тембру",
                instruction = "Вдих 4 сек. На видиху (6 сек) промукай 'Ммм', максимально розслабляючи щелепу й горло для найбагатшого тембру. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 6, cycles = 5,
                    instruction = "Вдих 4 сек, видих з розслабленим 'Ммм' для насиченого тембру"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_5",
                type = ExerciseType.ARTICULATION,
                title = "Щирість і тембр",
                instruction = "Промов фразу 'Я справді радий тебе бачити' спочатку формально-байдуже, потім щиро, від душі. Поміть різницю в тембрі. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_6",
                type = ExerciseType.READING,
                title = "Читання з насиченим тембром",
                instruction = "Прочитай текст, розкриваючи максимально насичений, теплий тембр голосу.",
                content = ExerciseContent.ReadingText(
                    text = "Тембр — це підпис твого голосу. Він не потребує заміни, лише розкриття через розслаблення, резонанс і щирість. Слухай себе уважно — і ти почуєш, як багатіє звучання з кожним тижнем практики.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}16_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь із залученим тембром",
                instruction = "Розкажи історію, яка тебе справді зворушила, дозволяючи собі щиро проживати емоцію. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Історія, яка мене справді зворушила",
                    hints = listOf(
                        "Дозволь собі щиро проживати емоцію під час розповіді",
                        "Не стримуй природні відтінки голосу",
                        "Слухай, як тембр змінюється з емоцією"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ===== УРОК 17: Діапазон голосу =====

    private fun getLesson17(): Lesson = Lesson(
        id = "${LESSON_PREFIX}17",
        courseId = COURSE_ID,
        dayNumber = 17,
        title = "Діапазон голосу",
        description = "Розширюємо палітру висот для виразнішого мовлення",
        theory = TheoryContent(
            text = """
                Широкий діапазон — це палітра, вузький — олівець одного кольору

                Діапазон голосу — це весь спектр висот (від найнижчої до найвищої ноти) і гучностей, доступних тобі. Чим ширший діапазон ти вмієш використовувати свідомо, тим виразніше й цікавіше звучить мовлення — так само, як художник з великою палітрою фарб малює багатше за того, хто має лише один олівець.

                Більшість людей у побутовому мовленні використовують лише невелику частину свого природного діапазону — говорять в одному вузькому регістрі. Це не через фізичні обмеження, а через звичку: мозок "заощаджує" зусилля, використовуючи мінімальний необхідний діапазон.

                Як розширити доступний діапазон? Регулярні вправи на "сирену" (плавний рух голосу від найнижчої до найвищої ноти і назад) розтягують діапазон, який ти можеш контролювати. Важливо: мета не в тому, щоб постійно говорити крайніми нотами, а в тому, щоб мати ширший вибір, коли він потрібен для виразності.

                Цікавий факт: середній діапазон людського голосу в розмовному мовленні охоплює приблизно одну октаву, тоді як фізично гортань здатна відтворювати значно ширший спектр — професійні оратори й актори свідомо використовують більшу частину цього потенціалу, тоді як більшість людей — лише невелику його частину.

                Типова помилка: форсувати крайні ноти діапазону (найнижчі чи найвищі) без розігріву — це може напружити зв'язки. Розширення діапазону має бути поступовим і комфортним.

                Ще помилка: плутати "широкий діапазон" з "хаотичною" інтонацією — мета не стрибати випадково по нотах, а мати доступ до потрібної висоти саме тоді, коли вона потрібна для сенсу чи емоції.

                Ключовий інсайт: діапазон — це інструмент виразності, а не самоціль. Розширюй його для того, щоб мати більше вибору в передачі сенсу, а не для демонстрації техніки самої по собі.
            """.trimIndent(),
            tips = listOf(
                "Тренуй 'сирену' щодня — плавний рух від найнижчої до найвищої ноти",
                "Не форсуй крайні ноти без розігріву — розширюй діапазон поступово",
                "Мета — мати вибір висоти, а не використовувати весь діапазон одразу"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_1",
                type = ExerciseType.ARTICULATION,
                title = "Сирена",
                instruction = "Промов звук 'Ааа', плавно рухаючи голос від найнижчої до найвищої комфортної ноти і назад, як сирена. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_2",
                type = ExerciseType.ARTICULATION,
                title = "Сходинки діапазону",
                instruction = "Промов 'На' на п'яти висотах: низько, нижче середнього, середньо, вище середнього, високо. Потім спускайся назад. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_3",
                type = ExerciseType.ARTICULATION,
                title = "Розширення меж",
                instruction = "Знайди свою найнижчу комфортну ноту і потримай її 5 секунд. Потім знайди найвищу комфортну і потримай 5 секунд. Не форсуй — лише комфортні межі.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_4",
                type = ExerciseType.ARTICULATION,
                title = "Речення з рухом висоти",
                instruction = "Промов 'Це просто неймовірно' з висхідною інтонацією (радісний подив), потім те саме з низхідною (спокійна констатація). Повтори 5 разів кожен варіант.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_5",
                type = ExerciseType.ARTICULATION,
                title = "Емоційний діапазон",
                instruction = "Промов 'Привіт' на п'яти різних висотах голосу, відчуваючи, як кожна висота передає інший відтінок настрою. 2 кола.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_6",
                type = ExerciseType.READING,
                title = "Читання з широким діапазоном",
                instruction = "Прочитай текст, свідомо використовуючи різну висоту голосу для різних частин тексту.",
                content = ExerciseContent.ReadingText(
                    text = "Широкий діапазон — це палітра художника. Хто вміє свідомо рухати голос вгору й вниз, той малює мовленням об'ємні, живі картини замість однотонних начерків. Це вміння вже частково твоє.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}17_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Розповідь з використанням діапазону",
                instruction = "Розкажи історію з несподіваним поворотом, свідомо використовуючи різну висоту голосу для різних моментів. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Історія з несподіваним поворотом",
                    hints = listOf(
                        "Використовуй різну висоту голосу для різних моментів історії",
                        "Не бійся підняти чи знизити тон для ефекту",
                        "Слухай, як діапазон робить розповідь живішою"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 14
    )

    // ===== УРОК 18: Експресивність =====

    private fun getLesson18(): Lesson = Lesson(
        id = "${LESSON_PREFIX}18",
        courseId = COURSE_ID,
        dayNumber = 18,
        title = "Експресивність",
        description = "Об'єднуємо резонанс, проекцію, гучність і діапазон у живе мовлення",
        theory = TheoryContent(
            text = """
                Експресивність — це коли всі техніки працюють разом, а не по черзі

                До цього моменту ти тренував резонанс, проекцію, гучність, тембр і діапазон окремо, як окремі м'язи в спортзалі. Експресивність — це момент, коли всі ці елементи починають працювати одночасно, підпорядковані змісту й меті мовлення, а не свідомій техніці.

                Що робить мовлення експресивним? Не одна яскрава деталь, а динаміка — зміна гучності там, де потрібен акцент, зсув резонансу від теплого до дзвінкого залежно від настрою фрази, розширення діапазону на емоційно важливих словах. Монотонність — це коли жоден з цих параметрів не змінюється; експресивність — коли вони змінюються доречно.

                Як тренувати експресивність? Найкращий спосіб — перестати думати про окрему техніку і сфокусуватися на змісті: що ти хочеш, щоб слухач відчув у цьому конкретному місці тексту? Тіло й голос, вже натреновані попередніми уроками, самі підберуть потрібну комбінацію резонансу, гучності й висоти.

                Цікавий факт: дослідження сприйняття мовлення показують, що слухачі оцінюють експресивного спікера як компетентнішого й переконливішого навіть при однаковому змісті промови порівняно з монотонним спікером — форма подачі суттєво впливає на сприйняття інформації, не лише сам зміст.

                Типова помилка: плутати експресивність із перебільшенням — надмірні стрибки гучності чи висоти без зв'язку зі змістом звучать театрально й награно. Експресивність підпорядкована сенсу, а не є самоціллю.

                Ще помилка: намагатися свідомо контролювати кожен параметр окремо під час живого мовлення — це занадто складно і призводить до "заклякання". Довірся натренованим навичкам і фокусуйся на змісті.

                Ключовий інсайт: справжня експресивність виникає не від контролю, а від довіри до вже натренованого тіла й голосу, коли увага повністю на сенсі того, що ти кажеш.
            """.trimIndent(),
            tips = listOf(
                "Фокусуйся на змісті, а не на техніці — тіло вже знає, як озвучити сенс",
                "Динаміка (зміна) важливіша за окрему яскраву деталь",
                "Не плутай експресивність з перебільшенням — усе підпорядковане сенсу"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_1",
                type = ExerciseType.ARTICULATION,
                title = "Динаміка на реченні",
                instruction = "Промов 'Це найважливіший момент мого життя' з динамічною зміною гучності й висоти на слові 'найважливіший'. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOLUME, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_2",
                type = ExerciseType.ARTICULATION,
                title = "Фокус на змісті",
                instruction = "Промов фразу 'Я вірю, що це спрацює' думаючи виключно про сенс, не контролюючи техніку свідомо. Повтори 5 разів, щоразу трохи інакше.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_3",
                type = ExerciseType.ARTICULATION,
                title = "Три відтінки одного речення",
                instruction = "Промов 'Подумай про це ще раз' як пораду, як попередження, як заохочення — змінюючи гучність, темп, резонанс відповідно. По 2 рази кожен варіант.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_4",
                type = ExerciseType.BREATHING,
                title = "Опора для динаміки",
                instruction = "Вдих 4 сек. На видиху (8 сек) промов протяжну фразу з природною зміною гучності й висоти. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, видих 8 сек з природною динамікою гучності й висоти"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_5",
                type = ExerciseType.ARTICULATION,
                title = "Виразне читання одного слова",
                instruction = "Промов слово 'Ні' п'ятьма різними способами — м'яко, твердо, здивовано, сумно, впевнено — використовуючи резонанс, гучність, висоту. Відчуй різницю.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE, SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_6",
                type = ExerciseType.READING,
                title = "Виразне читання тексту",
                instruction = "Прочитай текст, дозволяючи змісту вести динаміку голосу, без свідомого контролю кожного параметра.",
                content = ExerciseContent.ReadingText(
                    text = "Довірся своєму голосу. Він уже вміє більше, ніж тобі здається. Дозволь змісту вести звучання, а не контролюй кожен параметр окремо. Саме так народжується справжня експресивність — жива, природна, переконлива.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}18_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Експресивна розповідь",
                instruction = "Розкажи про момент, коли щось для себе зрозумів, фокусуючись на сенсі, а не на техніці. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Момент, коли я щось для себе зрозумів",
                    hints = listOf(
                        "Не думай про техніку — думай про сенс",
                        "Дозволь голосу природно змінюватися разом зі змістом",
                        "Говори так, ніби це справді важливо для тебе"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 14
    )

    // ===== УРОК 19: Голос у стресі =====

    private fun getLesson19(): Lesson = Lesson(
        id = "${LESSON_PREFIX}19",
        courseId = COURSE_ID,
        dayNumber = 19,
        title = "Голос у стресі",
        description = "Як швидко повернути контроль над голосом під час хвилювання",
        theory = TheoryContent(
            text = """
                Стрес — головний ворог голосу, і сьогодні ти вчишся його випереджати

                Хвилювання перед виступом, складна розмова, конфлікт — усе це змінює голос миттєво й непомітно для самої людини: він тремтить, стає вищим, темп прискорюється, дихання стає поверхневим. Це фізіологічна реакція організму на стрес — і саме вона руйнує все, що ти тренував попередні тижні.

                Що відбувається з голосом у стресі? Адреналін напружує м'язи, зокрема горло й щелепу. Дихання стає частим і поверхневим (грудним) замість діафрагмального. Темп мовлення прискорюється, бо тіло хоче "швидше завершити" стресову ситуацію. Результат: тремтячий, високий, задиханий голос — навіть якщо технічно ти все вмієш.

                Як повернути контроль? Перший крок — усвідомити, що відбувається: "Я хвилююся, і моє дихання стало поверхневим." Другий — свідомо уповільнити й поглибити вдих-видих кілька разів перед тим, як почати говорити. Третій — знизити темп мовлення навмисно, навіть якщо здається, що "хочеться швидше".

                Цікавий факт: актори й публічні спікери часто використовують техніку "фізіологічного зітхання" (два коротких вдихи носом, потім довгий видих ротом) безпосередньо перед виходом на сцену — ця техніка швидко знижує рівень фізіологічного збудження нервової системи ефективніше за звичайний глибокий вдих.

                Типова помилка: намагатися "просто заспокоїтися силою волі" без фізичної дії. Тривога — це тілесний стан, і змінити його найшвидше можна через тіло (дихання, поставу), а не тільки через думки.

                Ще помилка: приховувати хвилювання, форсуючи впевнений тон — це часто звучить награно й ще більше напружує голос. Краще визнати хвилювання (подумки чи навіть вголос: "Трохи хвилююся, але почнемо") і свідомо застосувати дихальну техніку.

                Ключовий інсайт: мета не в тому, щоб ніколи не хвилюватися — це нереалістично. Мета в тому, щоб мати конкретний, натренований інструмент (дихання, темп, опора), який повертає контроль над голосом за лічені секунди навіть у стресі.
            """.trimIndent(),
            tips = listOf(
                "Перед складною розмовою зроби 2-3 повільні дихальні цикли — це реально працює",
                "Свідомо уповільнюй темп мовлення, коли відчуваєш хвилювання",
                "Визнай хвилювання подумки замість того, щоб приховувати його силою"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_1",
                type = ExerciseType.BREATHING,
                title = "Фізіологічне зітхання",
                instruction = "Два коротких вдихи носом поспіль, потім довгий видих ротом (8 сек). Повтори 4 рази — це швидко знижує напругу.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 8, cycles = 4,
                    instruction = "Два коротких вдихи носом, потім довгий видих ротом — фізіологічне зітхання"
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_2",
                type = ExerciseType.ARTICULATION,
                title = "Уповільнення темпу",
                instruction = "Промов речення 'Я готовий впоратися з цією ситуацією' навмисно повільніше, ніж хочеться, ніби навмисно 'гальмуєш' поспіх. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.TEMPO, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_3",
                type = ExerciseType.ARTICULATION,
                title = "Симуляція стресу",
                instruction = "Уяви стресову ситуацію (важлива співбесіда, складна розмова). Промов 'Добрий день, дякую що знайшли час' спочатку швидко й напружено, потім свідомо уповільнено й з опорою. Відчуй різницю. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.CONFIDENCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_4",
                type = ExerciseType.BREATHING,
                title = "Заспокійливе дихання перед мовленням",
                instruction = "Вдих 4 сек, затримка 4 сек, видих 8 сек — техніка заспокоєння нервової системи перед складним мовленням. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 4, exhaleSeconds = 8, cycles = 5,
                    instruction = "Вдих 4 сек, затримка 4 сек, видих 8 сек — заспокоєння перед мовленням"
                ),
                durationSeconds = 80,
                targetMetrics = listOf(SkillType.BREATHING, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_5",
                type = ExerciseType.ARTICULATION,
                title = "Визнання хвилювання вголос",
                instruction = "Промов вголос: 'Я трохи хвилююся, і це нормально. Зараз я зроблю вдих і почну.' Потім продовж будь-якою фразою спокійним голосом. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_6",
                type = ExerciseType.READING,
                title = "Читання в умовах симульованого стресу",
                instruction = "Прочитай текст, свідомо застосовуючи уповільнене дихання й темп, ніби готуєшся до складної розмови.",
                content = ExerciseContent.ReadingText(
                    text = "Хвилювання — природна реакція, а не слабкість. Найкращі спікери світу теж хвилюються перед виступом. Різниця в тому, що вони мають натреновані інструменти — дихання, темп, опору — які повертають контроль за секунди.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.CONFIDENCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}19_7",
                type = ExerciseType.FREE_SPEECH,
                title = "Складна розмова",
                instruction = "Уяви реальну ситуацію, де хвилюєшся, і розкажи, як би почав цю складну розмову. Перед початком зроби дихальний цикл. 1-1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Як би я почав складну, незручну розмову",
                    hints = listOf(
                        "Уяви реальну ситуацію, де хвилюєшся",
                        "Перед початком зроби дихальний цикл",
                        "Свідомо говори повільніше, ніж хочеться"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.CONFIDENCE, SkillType.BREATHING, SkillType.TEMPO)
            )
        ),
        estimatedMinutes = 14
    )

    // ===== УРОК 20: Фінальна підготовка =====

    private fun getLesson20(): Lesson = Lesson(
        id = "${LESSON_PREFIX}20",
        courseId = COURSE_ID,
        dayNumber = 20,
        title = "Фінальна підготовка",
        description = "Комплексний огляд усіх технік курсу перед випускним",
        theory = TheoryContent(
            text = """
                Генеральна репетиція перед випускним

                Завтра — випускний урок курсу "Сила голосу". Сьогодні ми проходимось по всьому арсеналу технік трьох тижнів і перевіряємо, наскільки впевнено ти ними володієш.

                Чек-лист навичок курсу:

                Тиждень 1 — Фундамент:
                - Діафрагмальне дихання, опора, глибина дихання
                - Дихання в мовленні, розслаблення голосового апарату

                Тиждень 2 — Резонанс і сила:
                - Грудний, головний резонанс, маска, змішаний резонанс
                - Проекція голосу, гучність без крику

                Тиждень 3 — Майстерність:
                - Витривалість, тембр, діапазон, експресивність
                - Голос у стресі

                Сьогоднішній формат: серія коротких перевірок по кожній категорії, а наприкінці — комплексна вправа, яка поєднує все відразу.

                Як оцінювати себе? Якщо техніка застосовується без свідомого зусилля — навичка засвоєна. Якщо доводиться "згадувати кроки" — поверни увагу до відповідного уроку після курсу. Пам'ятай: мета не ідеальність, а помітний прогрес порівняно з диханням і голосом дня 1.

                Ти пройшов шлях від "хто взагалі думає про своє дихання" до свідомого керування резонансом, проекцією, тембром і поведінкою голосу в стресі. Завтра — час це показати й порівняти з самим початком.
            """.trimIndent(),
            tips = listOf(
                "Це самоперевірка, а не екзамен — будь чесним і спокійним",
                "Познач техніки, які даються важче — повернешся до них після курсу",
                "Прослухай запис дня 1 перед завтрашнім випускним — контраст надихне"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_1",
                type = ExerciseType.ARTICULATION,
                title = "Швидка розминка",
                instruction = "1) Масаж обличчя і шиї 30 сек. 2) Позіхання 3 рази. 3) М'яке гудіння 'М-м-м' 15 сек.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_2",
                type = ExerciseType.BREATHING,
                title = "Тест: діафрагмальне дихання й опора",
                instruction = "Вдих діафрагмою 4 сек, затримка 2 сек, видих з опорою 8 сек — перевір стабільність і відсутність напруги в плечах. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 2, exhaleSeconds = 8, cycles = 5,
                    instruction = "Тест діафрагмального дихання й опори"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_3",
                type = ExerciseType.ARTICULATION,
                title = "Тест: резонанси",
                instruction = "Промов 'Ммм' грудним резонансом, потім 'Нг' головним, потім 'Ма' у масці. Перевір, чи легко перемикаєшся між ними.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_4",
                type = ExerciseType.ARTICULATION,
                title = "Тест: проекція і гучність",
                instruction = "Промов 'Мене чути здалеку' з проекцією без крику, потім те саме голосніше, зберігаючи розслаблене горло.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.VOLUME)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_5",
                type = ExerciseType.ARTICULATION,
                title = "Тест: тембр і діапазон",
                instruction = "Промов 'Сьогодні особливий день' на трьох висотах голосу з максимально насиченим тембром на кожній.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_6",
                type = ExerciseType.BREATHING,
                title = "Тест: голос у стресі",
                instruction = "Симулюй хвилювання, потім виконай фізіологічне зітхання (2 вдихи носом, довгий видих ротом) і промов спокійну фразу. 3 цикли.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 0, exhaleSeconds = 8, cycles = 3,
                    instruction = "Тест техніки заспокоєння голосу перед мовленням у стресі"
                ),
                durationSeconds = 50,
                targetMetrics = listOf(SkillType.CONFIDENCE, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_7",
                type = ExerciseType.READING,
                title = "Комплексне читання",
                instruction = "Прочитай текст, застосовуючи опору, резонанс, проекцію і контрольовану гучність одночасно.",
                content = ExerciseContent.ReadingText(
                    text = "Це був довгий шлях — від першого невпевненого запису до голосу, яким керуєш свідомо. Дихання, опора, резонанс, проекція, тембр, стійкість у стресі — усе це тепер частина тебе. Завтра ти покажеш результат.",
                    emotion = null
                ),
                durationSeconds = 75,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}20_8",
                type = ExerciseType.FREE_SPEECH,
                title = "Генеральна репетиція випускного",
                instruction = "Підсумуй свій шлях за три тижні курсу, свідомо використовуючи всі техніки. 1.5 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Підсумок мого шляху за три тижні курсу",
                    hints = listOf(
                        "Згадай, яким був голос на початку",
                        "Використай усі техніки свідомо",
                        "Говори впевнено — завтра випускний"
                    )
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 15
    )

    // ===== УРОК 21: Випускний =====

    private fun getLesson21(): Lesson = Lesson(
        id = "${LESSON_PREFIX}21",
        courseId = COURSE_ID,
        dayNumber = 21,
        title = "Випускний",
        description = "Фінальний урок! Підсумок курсу та випускна практика",
        theory = TheoryContent(
            text = """
                Вітаю, випускнику!

                Ти пройшов 21-денний курс "Сила голосу". Три тижні тому твій голос звучав так, як звучав завжди — за звичкою, без усвідомленого контролю. Сьогодні ти володієш технікою, яку більшість людей ніколи свідомо не вивчають.

                Що ти опанував за цей час:

                Дихання і опора
                Діафрагмальне дихання, опора видиху, глибина вдиху, інтеграція дихання в природне мовлення.

                Розслаблення
                Зняття затисків голосового апарату — вільний, невимушений звук без напруги горла.

                Резонанс і проекція
                Грудний, головний резонанс, фокус у масці, змішаний резонанс, проекція без крику, гучність без напруги.

                Майстерність застосування
                Витривалість голосу, багатий тембр, широкий діапазон, жива експресивність, стійкість голосу під стресом.

                Що далі? Курс закінчився, але голос — інструмент, який слабшає без практики. Ось що рекомендую:

                1. Щоденна розминка — 5 хвилин дихання й резонансу вранці
                2. Свідома опора в важливих розмовах — не форсуй горло
                3. Раз на тиждень — довша практика витривалості (5-10 хвилин безперервного мовлення з технікою)
                4. Слухай себе — записуй важливі виступи й аналізуй, які резонатори домінують

                Сьогоднішній урок — твій випускний. Ми пройдемось по найяскравіших техніках курсу, а наприкінці ти запишеш фінальну розповідь для порівняння з днем 1.

                Готовий? Твій голос змінився. Час це почути.
            """.trimIndent(),
            tips = listOf(
                "Це святкування прогресу, а не екзамен — розслабся і насолодись",
                "Порівняй сьогоднішній запис із записом дня 1 — різниця буде разючою",
                "Обери одну техніку тижня і свідомо тренуй її в реальному житті після курсу"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_1",
                type = ExerciseType.ARTICULATION,
                title = "Церемоніальна розминка",
                instruction = "Востаннє в курсі: масаж обличчя і шиї (30 сек), позіхання (3 рази), сирена від найнижчої до найвищої ноти (2 рази).",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 90),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.VOICE_QUALITY)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_2",
                type = ExerciseType.BREATHING,
                title = "Випускне дихання й опора",
                instruction = "Фінальна дихальна вправа курсу: вдих 4 сек, затримка 2 сек, видих з опорою 8 сек. 5 циклів.",
                content = ExerciseContent.BreathingExercise(
                    inhaleSeconds = 4, holdSeconds = 2, exhaleSeconds = 8, cycles = 5,
                    instruction = "Фінальна дихальна вправа курсу з опорою"
                ),
                durationSeconds = 70,
                targetMetrics = listOf(SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_3",
                type = ExerciseType.ARTICULATION,
                title = "Усі резонатори поспіль",
                instruction = "Промов 'Ммм' грудним, 'Нг' головним, 'Ма' у масці, потім змішаний резонанс на 'Ааааа'. Відчуй, як легко тепер перемикаєшся.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.RESONANCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_4",
                type = ExerciseType.ARTICULATION,
                title = "Випускна проекція",
                instruction = "Промов 'Сьогодні я випускник курсу Сила голосу' з повною проекцією, без крику, направляючи звук у далеку точку кімнати.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 45),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.PROJECTION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_5",
                type = ExerciseType.ARTICULATION,
                title = "Випускне крещендо",
                instruction = "Промов з наростанням енергії: 'Це був не просто курс. Це була трансформація мого голосу. І вона триває!' Повтори 3 рази, нарощуючи впевненість.",
                content = ExerciseContent.ArticulationExercise(hasTimer = true, durationSeconds = 60),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_6",
                type = ExerciseType.READING,
                title = "Випускне читання",
                instruction = "Прочитай текст впевнено, застосовуючи всі техніки курсу.",
                content = ExerciseContent.ReadingText(
                    text = "Три тижні тому я не думав про власне дихання. Сьогодні я керую опорою, резонансом, проекцією свідомо. Мій голос став сильнішим, теплішим, витривалішим. Це не кінець шляху, а початок нового способу говорити.",
                    emotion = null
                ),
                durationSeconds = 75,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_7",
                type = ExerciseType.READING,
                title = "Фінальний тест: усі техніки",
                instruction = "Прочитай текст, свідомо поєднуючи дихання, опору, резонанс і проекцію.",
                content = ExerciseContent.ReadingText(
                    text = "Голос — це інструмент, яким варто вчитися керувати свідомо. Дихання дає силу, опора — стабільність, резонанс — красу звучання, проекція — дальність без крику. Все це тепер частина того, як я говорю щодня.",
                    emotion = null
                ),
                durationSeconds = 75,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.PROJECTION, SkillType.BREATHING)
            ),
            LessonExercise(
                id = "${EXERCISE_PREFIX}21_8",
                type = ExerciseType.FREE_SPEECH,
                title = "Випускне слово",
                instruction = "Розкажи про свій досвід проходження курсу 'Сила голосу'. Порівняй голос на старті й зараз. Яка техніка була найскладнішою? Яка найкориснішою? Говори 1.5-2 хвилини.",
                content = ExerciseContent.FreeSpeechTopic(
                    topic = "Мій досвід курсу 'Сила голосу'",
                    hints = listOf(
                        "Порівняй голос на старті й зараз",
                        "Яка техніка була найскладнішою? Яка найкориснішою?",
                        "Що плануєш тренувати далі?"
                    )
                ),
                durationSeconds = 120,
                targetMetrics = listOf(SkillType.VOICE_QUALITY, SkillType.RESONANCE, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 18
    )
}
