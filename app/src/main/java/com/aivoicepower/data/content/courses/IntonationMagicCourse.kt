package com.aivoicepower.data.content.courses

import com.aivoicepower.domain.model.course.*
import com.aivoicepower.domain.model.exercise.*
import com.aivoicepower.domain.model.user.SkillType

object IntonationMagicCourse {

    const val COURSE_ID = "course_2"

    fun getCourse(): Course = Course(
        id = COURSE_ID,
        title = "Магія інтонації",
        description = "Опануй мистецтво емоційного мовлення та виразності",
        iconEmoji = "🎭",
        totalLessons = 21,
        isPremium = false,
        estimatedDays = 21,
        difficulty = Difficulty.BEGINNER,
        skills = listOf(SkillType.INTONATION, SkillType.CONFIDENCE),
        lessons = getLessons()
    )

    fun getLessons(): List<Lesson> = listOf(
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
        getLesson15Placeholder(),
        getLesson16Placeholder(),
        getLesson17Placeholder(),
        getLesson18Placeholder(),
        getLesson19Placeholder(),
        getLesson20Placeholder(),
        getLesson21Placeholder()
    )

    // ==================== УРОК 1: Основи інтонації ====================
    private fun getLesson1(): Lesson = Lesson(
        id = "intonation_lesson_1",
        courseId = COURSE_ID,
        dayNumber = 1,
        title = "Основи інтонації",
        description = "Що таке інтонація і чому вона важливіша за слова",
        theory = TheoryContent(
            text = """
                Інтонація — душа мовлення

                Ти коли-небудь чув фразу "Важливо не що ти говориш, а як"? Це саме про інтонацію. Коли слова суперечать тону голосу, люди більше довіряють інтонації, ніж словам. Саме тому фраза "Все добре", сказана сумним голосом, не переконує нікого.Дослідження показують, що у емоційній промові, до 38% інформації ми передаємо через інтонацію, і лише 7% — через самі слова. Решта — мова тіла.

                Що таке інтонація? Це мелодика мовлення — підвищення та пониження голосу, паузи, темп, гучність, тембр. Одне слово "так" можна вимовити десятками способів: радісно, сумно, саркастично, невпевнено, агресивно, ніжно...

                Чому інтонація така важлива?

                1. Емоційний зв'язок — монотонне мовлення відштовхує, жива інтонація притягує
                2. Розуміння — інтонація допомагає зрозуміти справжній зміст сказаного
                3. Переконливість — той самий аргумент звучить по-різному з різною інтонацією
                4. Запам'ятовування — емоційно забарвлене мовлення краще запам'ятовується

                Базові елементи інтонації:

                • Мелодика — рух голосу вгору та вниз
                • Темп — швидкість мовлення
                • Паузи — мовчання між словами та фразами
                • Гучність — сила голосу
                • Тембр — "колір" голосу, його емоційне забарвлення

                Типова помилка: багато людей говорять монотонно, на одній ноті. Це втомлює слухача і робить мовлення нудним. Навіть найцікавіший зміст втрачається в монотонності.

                Цей курс навчить тебе грати голосом як музичним інструментом. Ти опануєш різні емоційні регістри, навчишся використовувати паузи та акценти, і твоє мовлення стане живим та захоплюючим.
            """.trimIndent(),
            tips = listOf(
                "Інтонація передає до 38% інформації — більше, ніж слова",
                "Монотонність — головний ворог цікавого мовлення",
                "Почни помічати інтонації інших людей — це найкраща школа"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_1_1",
                type = ExerciseType.ARTICULATION,
                title = "Діапазон голосу",
                instruction = "Вимов звук 'а' на найнижчій ноті, потім плавно підніми до найвищої, потім опусти назад. Це як сирена. Повтори 5 разів. Відчуй весь діапазон свого голосу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_1_2",
                type = ExerciseType.ARTICULATION,
                title = "Сходинки голосу",
                instruction = "Уяви 5 сходинок. Вимовляй 'ма' на кожній сходинці, піднімаючись вгору: низько-нижче середнього-середньо-вище середнього-високо. Потім спускайся вниз. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_1_3",
                type = ExerciseType.ARTICULATION,
                title = "Питання vs Твердження",
                instruction = "Вимов 'Це твоя книжка' як твердження (голос вниз в кінці). Потім як питання (голос вгору в кінці). Відчуй різницю. Повтори 10 разів, чергуючи.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_1_4",
                type = ExerciseType.ARTICULATION,
                title = "Логічний наголос",
                instruction = "Вимов речення 'Я сьогодні йду в кіно' 4 рази, кожен раз наголошуючи інше слово: Я (не хтось інший), СЬОГОДНІ (не завтра), ЙДУ (не їду), В КІНО (не в театр). Відчуй як змінюється зміст.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_1_5",
                type = ExerciseType.ARTICULATION,
                title = "Слово 'Так' — 10 способів",
                instruction = "Вимов слово 'Так' 10 різними способами: радісно, сумно, здивовано, сердито, невпевнено, саркастично, ніжно, офіційно, таємничо, захоплено. Відчуй як інтонація змінює значення.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_1_6",
                type = ExerciseType.EMOTION_READING,
                title = "Нейтральне читання",
                instruction = "Прочитай текст спокійно, нейтрально, але НЕ монотонно. Використовуй природні підйоми та спуски голосу.",
                content = ExerciseContent.ReadingText(
                    text = "Сонце повільно сідало за обрій. Небо змінювало кольори від золотого до рожевого. Птахи летіли додому. День закінчувався.",
                    emotion = Emotion.NEUTRAL
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_1_7",
                type = ExerciseType.EMOTION_READING,
                title = "Питальна інтонація",
                instruction = "Прочитай кожне речення як справжнє питання — з підйомом голосу в кінці та зацікавленістю.",
                content = ExerciseContent.ReadingText(
                    text = "Ти справді так думаєш? Це можливо? Хто б міг подумати? Чому ніхто не сказав раніше? Що тепер робити?",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            )
        ),
        estimatedMinutes = 12
    )

    // ==================== УРОК 2: Емоція — радість ====================
    private fun getLesson2(): Lesson = Lesson(
        id = "intonation_lesson_2",
        courseId = COURSE_ID,
        dayNumber = 2,
        title = "Емоція: радість",
        description = "Вчимося передавати радість та позитивні емоції голосом",
        theory = TheoryContent(
            text = """
                Радість — найзаразливіша емоція

                Радісна інтонація — це не просто "говорити весело". Це комплекс характеристик голосу, який миттєво передає слухачеві твій позитивний стан. І найголовніше — радість заразлива. Коли ти говориш радісно, слухачі автоматично починають почуватися краще.

                Як звучить радісна інтонація?

                • Підвищений тон — голос злегка вищий за звичайний
                • Швидший темп — радість додає енергії, мовлення пришвидшується
                • Яскраві інтонаційні перепади — голос "стрибає", не монотонний
                • Легкість — немає напруження, голос звучить вільно
                • "Посмішка в голосі" — навіть без посмішки на обличчі чутно, що людина радіє

                Де використовувати радісну інтонацію?

                1. Привітання — "Доброго ранку!" з радісною інтонацією починає день позитивно
                2. Гарні новини — підсилюй позитив відповідною інтонацією
                3. Компліменти — радісна інтонація робить комплімент щирим
                4. Мотиваційні промови — надихай інших своєю енергією

                Типова помилка: штучна, перебільшена радість звучить фальшиво. Справжня радісна інтонація має бути природною, органічною. Не грай радість — відчувай її.

                Лайфхак: перед тим як говорити радісно, згадай щось справді приємне. Справжня емоція завжди звучить краще за імітацію.

                Сьогодні ми будемо тренувати різні відтінки радості: від тихої задоволеності до бурхливого захоплення.
            """.trimIndent(),
            tips = listOf(
                "Посміхайся коли говориш — це автоматично змінює тембр голосу",
                "Радісна інтонація: вищий тон + швидший темп + більше перепадів",
                "Згадай щось приємне перед записом — щирість чутно"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_2_1",
                type = ExerciseType.ARTICULATION,
                title = "Посмішка в голосі",
                instruction = "Вимов 'Привіт!' спочатку з нейтральним обличчям, потім з широкою посмішкою. Почуй різницю. Повтори 10 разів з посмішкою, запам'ятай відчуття.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_2_2",
                type = ExerciseType.ARTICULATION,
                title = "Градації радості",
                instruction = "Вимов 'Чудово!' з різною інтенсивністю радості: 1) легке задоволення, 2) приємна новина, 3) справжня радість, 4) захоплення, 5) ейфорія. Відчуй градації.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_2_3",
                type = ExerciseType.ARTICULATION,
                title = "Радісне привітання",
                instruction = "Уяви, що зустрічаєш друга, якого не бачив рік. Вимов: 'Ой, привіт! Як я радий тебе бачити! Скільки часу минуло!' — з щирою радістю. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_2_4",
                type = ExerciseType.ARTICULATION,
                title = "Гарна новина",
                instruction = "Уяви, що повідомляєш гарну новину. Вимов з радістю: 'Уявляєш, я виграв! Це неймовірно! Я сам не вірю!' — передай захоплення. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_2_5",
                type = ExerciseType.ARTICULATION,
                title = "Комплімент",
                instruction = "Вимов компліменти з теплою радістю: 'Ти сьогодні чудово виглядаєш!', 'Як гарно в тебе вийшло!', 'Ти справжній талант!' — щиро, не фальшиво. По 3 рази кожен.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_2_6",
                type = ExerciseType.EMOTION_READING,
                title = "Радісна історія",
                instruction = "Прочитай текст з радісною інтонацією. Ти розповідаєш про найкращий день свого життя!",
                content = ExerciseContent.ReadingText(
                    text = "Це був найкращий день! Сонце сяяло, друзі зібралися разом, і ми сміялися до сліз. Все виходило ідеально. Я відчував себе найщасливішою людиною на світі!",
                    emotion = Emotion.JOY
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_2_7",
                type = ExerciseType.EMOTION_READING,
                title = "Святкове привітання",
                instruction = "Прочитай як святкову промову — з натхненням та радістю!",
                content = ExerciseContent.ReadingText(
                    text = "Дорогі друзі! Сьогодні особливий день! Ми зібралися, щоб святкувати разом. Нехай цей день принесе вам безмежну радість! Зі святом!",
                    emotion = Emotion.JOY
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 11
    )

    // ==================== УРОК 3: Емоція — сум ====================
    private fun getLesson3(): Lesson = Lesson(
        id = "intonation_lesson_3",
        courseId = COURSE_ID,
        dayNumber = 3,
        title = "Емоція: сум",
        description = "Вчимося передавати смуток та меланхолію голосом",
        theory = TheoryContent(
            text = """
                Сум — найглибша емоція

                Сумна інтонація — протилежність радісної. Вона повільна, низька, тиха. Але це не означає монотонну! Сум має свою мелодику, свої відтінки: від легкої меланхолії до глибокого горя.

                Як звучить сумна інтонація?

                • Знижений тон — голос нижчий за звичайний
                • Повільний темп — сум сповільнює мовлення
                • Тихіший голос — немає енергії для гучності
                • Спадна мелодика — фрази закінчуються низько
                • Більше пауз — сум потребує часу

                Навіщо вміти говорити сумно?

                1. Емпатія — співчуття іншим потребує відповідної інтонації
                2. Драматичні історії — оповідач має передати настрій
                3. Вибачення — щирі вибачення звучать м'яко і сумно
                4. Погані новини — правильна інтонація пом'якшує удар

                Важливо: сумна інтонація — це не депресія і не скиглення. Це контрольована емоція, яку ти використовуєш свідомо. Актори виходять з ролі після зйомок — і ти маєш вміти "вимикати" сум.

                Типова помилка: перебільшений сум звучить театрально і фальшиво. Справжній сум — стриманий, глибокий, без істерик.

                Сьогодні ми навчимося різним відтінкам сумної інтонації: від ностальгії до співчуття.
            """.trimIndent(),
            tips = listOf(
                "Сумна інтонація: нижчий тон + повільніший темп + тихіший голос",
                "Не перегравай — справжній сум стриманий, а не істеричний",
                "Після вправ на сум — обов'язково 'вийди з ролі', посміхнись"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_3_1",
                type = ExerciseType.ARTICULATION,
                title = "Зітхання",
                instruction = "Глибоко зітхни і на видиху вимов 'ох...' — м'яко, сумно. Потім 'ех...', потім 'ах...'. Відчуй як сум впливає на голос. Повтори 5 разів кожен звук.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_3_2",
                type = ExerciseType.ARTICULATION,
                title = "Градації суму",
                instruction = "Вимов 'Шкода...' з різною інтенсивністю: 1) легкий жаль, 2) розчарування, 3) смуток, 4) глибокий сум, 5) горе. Відчуй як змінюється голос.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_3_3",
                type = ExerciseType.ARTICULATION,
                title = "Ностальгія",
                instruction = "Вимов з ностальгією: 'Пам'ятаю ті часи... Як давно це було... Все змінилося...' — м'яко, з легким сумом за минулим. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_3_4",
                type = ExerciseType.ARTICULATION,
                title = "Співчуття",
                instruction = "Вимов з щирим співчуттям: 'Мені так шкода...', 'Я розумію, як тобі важко...', 'Тримайся, все буде добре...' — тепло, м'яко. По 3 рази кожну фразу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_3_5",
                type = ExerciseType.ARTICULATION,
                title = "Вибачення",
                instruction = "Вимов щире вибачення: 'Вибач мені, будь ласка. Я не хотів тебе образити. Мені дуже шкода.' — м'яко, сумно, без виправдань. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_3_6",
                type = ExerciseType.EMOTION_READING,
                title = "Сумна історія",
                instruction = "Прочитай текст з легким сумом — як спогад про щось, що минуло.",
                content = ExerciseContent.ReadingText(
                    text = "Осінній дощ барабанив по вікну. Стара фотографія лежала на столі. Обличчя на ній усміхалися, але ті часи вже минули. Залишилися лише спогади.",
                    emotion = Emotion.SADNESS
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_3_7",
                type = ExerciseType.EMOTION_READING,
                title = "Прощання",
                instruction = "Прочитай як прощання з близькою людиною — з теплим сумом.",
                content = ExerciseContent.ReadingText(
                    text = "Час летить так швидко. Здається, ще вчора ми зустрілися, а сьогодні вже прощаємося. Я буду сумувати. Бережи себе. До зустрічі.",
                    emotion = Emotion.SADNESS
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 11
    )

    // ==================== УРОК 4: Емоція — гнів ====================
    private fun getLesson4(): Lesson = Lesson(
        id = "intonation_lesson_4",
        courseId = COURSE_ID,
        dayNumber = 4,
        title = "Емоція: гнів",
        description = "Вчимося контрольовано виражати гнів та невдоволення",
        theory = TheoryContent(
            text = """
                Гнів — сила, яку треба контролювати

                Гнівна інтонація — найпотужніша з усіх. Вона може залякати, переконати, встановити кордони. Але некерований гнів руйнує — і стосунки, і голосові зв'язки. Тому ми вчимося контрольованому гніву.

                Як звучить гнівна інтонація?

                • Напружений голос — м'язи горла напружені
                • Різкий темп — слова "вистрілюють"
                • Акцентовані приголосні — "Т", "К", "П" звучать жорстко
                • Понижений тон — гнів звучить низько, загрозливо
                • Менше інтонаційних перепадів — голос "рубаний"

                Важливо: є різниця між гнівом і агресією. Гнів — це емоція, агресія — це дія. Можна виражати гнів без агресії: чітко, твердо, але без образ та крику.

                Де використовувати гнівну інтонацію?

                1. Встановлення кордонів — "Ні, це неприйнятно"
                2. Захист себе або інших — твердість без істерики
                3. Драматичні ролі — театр, кіно, озвучення
                4. Наголос на серйозності — підкреслення важливості

                Типові помилки:
                • Крик — це не гнів, це втрата контролю
                • Образи — гнів на ситуацію, а не на людину
                • Тривалий гнів — короткий спалах ефективніший

                Лайфхак: холодний гнів страшніший за гарячий. Тихий, контрольований гнів справляє більше враження, ніж крик.

                Сьогодні тренуємо різні відтінки: від легкого роздратування до твердого "ні".
            """.trimIndent(),
            tips = listOf(
                "Гнів — це не крик, а контрольована сила в голосі",
                "Холодний гнів ефективніший за гарячий — будь твердим, не істеричним",
                "Після вправ на гнів — розслаб горло, випий води"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_4_1",
                type = ExerciseType.ARTICULATION,
                title = "Тверде 'Ні'",
                instruction = "Вимов 'Ні' з різною інтенсивністю: 1) м'яка відмова, 2) тверда відмова, 3) категоричне ні, 4) гнівне ні, 5) залізне ні. Відчуй градації твердості.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_4_2",
                type = ExerciseType.ARTICULATION,
                title = "Роздратування",
                instruction = "Вимов з легким роздратуванням: 'Скільки можна?', 'Я вже казав!', 'Це неприйнятно!' — не кричи, але будь твердим. По 3 рази кожну фразу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_4_3",
                type = ExerciseType.ARTICULATION,
                title = "Холодний гнів",
                instruction = "Вимов тихо, але дуже твердо: 'Я. Сказав. Ні.' — кожне слово окремо, з паузами, низьким голосом. Це холодний, контрольований гнів. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_4_4",
                type = ExerciseType.ARTICULATION,
                title = "Встановлення кордонів",
                instruction = "Вимов твердо, але без агресії: 'Зупинись. Це переходить межу. Я не дозволю так зі мною поводитися.' — спокійно, впевнено. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_4_5",
                type = ExerciseType.ARTICULATION,
                title = "Контраст: прохання vs вимога",
                instruction = "Вимов 'Закрий двері' як: 1) м'яке прохання, 2) повторне прохання (трохи тверддіше), 3) тверда вимога, 4) наказ. Відчуй як змінюється інтонація.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_4_6",
                type = ExerciseType.EMOTION_READING,
                title = "Твердий монолог",
                instruction = "Прочитай як людина, яка спокійно, але твердо захищає свою позицію.",
                content = ExerciseContent.ReadingText(
                    text = "Досить. Я довго мовчав, але всьому є межа. Це рішення остаточне. Обговорення закрито. Більше до цієї теми не повертаємося.",
                    emotion = Emotion.ANGER
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_4_7",
                type = ExerciseType.EMOTION_READING,
                title = "Справедливий гнів",
                instruction = "Прочитай з контрольованим гнівом — ти обурений несправедливістю.",
                content = ExerciseContent.ReadingText(
                    text = "Це несправедливо! Люди працювали місяцями, а результат привласнили інші. Так не повинно бути. І я не збираюся мовчати.",
                    emotion = Emotion.ANGER
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_4_8",
                type = ExerciseType.ARTICULATION,
                title = "Розслаблення горла",
                instruction = "Після вправ на гнів важливо розслабити горло. Виконай: 1) Позіхни широко 3 рази. 2) Помасажуй шию м'якими круговими рухами 15 секунд. 3) Промукай 'м-м-м' на низькій комфортній ноті 10 секунд. 4) Випий теплої води. Горло має відчуватися розслабленим.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            )
        ),
        estimatedMinutes = 11
    )

    // ==================== УРОК 5: Емоція — здивування ====================
    private fun getLesson5(): Lesson = Lesson(
        id = "intonation_lesson_5",
        courseId = COURSE_ID,
        dayNumber = 5,
        title = "Емоція: здивування",
        description = "Вчимося передавати здивування, захоплення та подив",
        theory = TheoryContent(
            text = """
                Здивування — емоція відкриття

                Здивування — це миттєва реакція на щось несподіване. Воно може бути позитивним (захоплення), негативним (шок) або нейтральним (просто подив). Здивована інтонація робить мовлення живим і цікавим.

                Як звучить здивована інтонація?

                • Різкий підйом голосу — особливо на початку фрази
                • Розширений діапазон — голос "стрибає" більше
                • Подовжені голосні — "Оооо!", "Нічооого собі!"
                • Паузи після здивування — момент осмислення
                • Прискорення або уповільнення — залежно від типу здивування

                Типи здивування:

                1. Позитивне здивування — "Ого! Як круто!"
                2. Негативне здивування — "Що?! Цього не може бути!"
                3. Нейтральний подив — "Цікаво... Не знав."
                4. Захоплення — "Неймовірно! Фантастика!"
                5. Шок — "Як?! Коли?!"

                Де використовувати?

                • Реакція на новини — щира зацікавленість
                • Історії — підтримання інтересу слухачів
                • Компліменти — "Ого, як гарно!" звучить щиріше
                • Презентації — підкреслення цікавих фактів

                Типова помилка: фальшиве здивування звучить саркастично. Якщо не здивований — не грай здивування, краще використай іншу інтонацію.
            """.trimIndent(),
            tips = listOf(
                "Здивування — це різкий підйом голосу на початку фрази",
                "Подовжуй голосні для підсилення ефекту: 'Оооо!', 'Нічооого собі!'",
                "Різні типи здивування звучать по-різному — тренуй кожен"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_5_1",
                type = ExerciseType.ARTICULATION,
                title = "Базове здивування",
                instruction = "Вимов 'Ого!', 'Ох!', 'Ах!', 'Ух ти!' — з різким підйомом голосу, як реакцію на щось несподіване. Кожен вигук 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_5_2",
                type = ExerciseType.ARTICULATION,
                title = "Градації здивування",
                instruction = "Вимов 'Серйозно?' з різною інтенсивністю: 1) легкий подив, 2) зацікавленість, 3) недовіра, 4) шок, 5) повне здивування. По 2 рази кожну градацію.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_5_3",
                type = ExerciseType.ARTICULATION,
                title = "Позитивне здивування",
                instruction = "Вимов з радісним здивуванням: 'Не може бути! Це найкраща новина! Я так радий!' — як реакцію на чудову несподіванку. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_5_4",
                type = ExerciseType.ARTICULATION,
                title = "Негативне здивування",
                instruction = "Вимов з негативним здивуванням: 'Що?! Як це сталося?! Цього не може бути!' — як реакцію на погану новину. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_5_5",
                type = ExerciseType.ARTICULATION,
                title = "Захоплення",
                instruction = "Вимов з захопленням: 'Неймовірно! Це просто фантастика! Я вражений!' — з широко відкритими очима, з підйомом голосу. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_5_6",
                type = ExerciseType.EMOTION_READING,
                title = "Несподівана новина",
                instruction = "Прочитай як людина, яка дізналася щось неймовірне — з подивом та захопленням.",
                content = ExerciseContent.ReadingText(
                    text = "Ти не повіриш! Уявляєш, що сталося? Я сам не вірю своїм очам! Це просто неймовірно! Треба терміново всім розповісти!",
                    emotion = Emotion.SURPRISE
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_5_7",
                type = ExerciseType.EMOTION_READING,
                title = "Цікавий факт",
                instruction = "Прочитай як ведучий науково-популярної програми — з зацікавленим подивом.",
                content = ExerciseContent.ReadingText(
                    text = "А ви знали, що восьминоги мають три серця? Так, саме три! І кров у них блакитного кольору! Дивовижно, правда? Природа не перестає дивувати!",
                    emotion = Emotion.SURPRISE
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 11
    )

    // ==================== УРОК 6: Паузи та акценти ====================
    private fun getLesson6(): Lesson = Lesson(
        id = "intonation_lesson_6",
        courseId = COURSE_ID,
        dayNumber = 6,
        title = "Паузи та акценти",
        description = "Мистецтво мовчання та виділення ключових слів",
        theory = TheoryContent(
            text = """
                Пауза — найпотужніший інструмент оратора

                Початківці бояться мовчати. Вони заповнюють кожну секунду словами, "е-е-е", "ну-у-у". Професіонали знають: пауза — це не порожнеча, а потужний інструмент.

                Що робить пауза?

                • Створює напругу — слухачі чекають, що буде далі
                • Підкреслює важливе — слово після паузи запам'ятовується
                • Дає час осмислити — складна думка потребує перетравлення
                • Демонструє впевненість — невпевнена людина боїться мовчати
                • Керує увагою — пауза повертає увагу тих, хто відволікся

                Типи пауз:

                1. Логічна пауза — між реченнями та частинами речення
                2. Психологічна пауза — перед важливим словом для ефекту
                3. Пауза-роздум — показує, що ти думаєш
                4. Ритмічна пауза — для створення ритму мовлення

                Логічний наголос (акцент):

                Не всі слова в реченні однаково важливі. Логічний наголос — це виділення ключового слова голосом. Способи виділення:
                • Підвищення гучності
                • Зміна тону (вище або нижче)
                • Розтягування слова
                • Пауза перед словом

                Приклад: "Я ТЕБЕ люблю" vs "Я тебе ЛЮБЛЮ" vs "Я тебе люблю" — три різних значення!

                Золоте правило: одне речення — один головний акцент. Якщо виділяти все — не виділяється нічого.
            """.trimIndent(),
            tips = listOf(
                "Пауза перед важливим словом підсилює його в рази",
                "Не бійся мовчати — пауза демонструє впевненість",
                "Одне речення — один головний акцент, не більше"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_6_1",
                type = ExerciseType.ARTICULATION,
                title = "Комфортна пауза",
                instruction = "Вимов: 'Я хочу сказати дещо важливе.' Зроби паузу 3 секунди (рахуй в голові). Потім продовж: 'Дуже важливе.' Повтори 5 разів. Звикай до мовчання.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_6_2",
                type = ExerciseType.ARTICULATION,
                title = "Драматична пауза",
                instruction = "Вимов: 'І переможцем стає...' — довга пауза (5 секунд) — '...команда синіх!' Відчуй напругу очікування. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_6_3",
                type = ExerciseType.ARTICULATION,
                title = "Логічний наголос — варіації",
                instruction = "Вимов 'Вона не говорила що любить його' з наголосом на різних словах: ВОНА (не хтось інший), НЕ (заперечення), ГОВОРИЛА (не писала), ЛЮБИТЬ (не ненавидить), ЙОГО (не когось). 2 рази кожен варіант.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_6_4",
                type = ExerciseType.ARTICULATION,
                title = "Пауза + акцент",
                instruction = "Вимов з паузою перед ключовим словом: 'Це було... НЕЙМОВІРНО.', 'Я прийняв... РІШЕННЯ.', 'Відповідь... НІ.' Пауза 2 секунди, потім акцентоване слово. По 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_6_5",
                type = ExerciseType.ARTICULATION,
                title = "Ритмічні паузи",
                instruction = "Вимов з рівномірними паузами: 'Раз. Два. Три. Чотири. П'ять.' — кожне число окремо, пауза 1 секунда. Потім швидше, потім повільніше. Відчуй ритм.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_6_6",
                type = ExerciseType.READING,
                title = "Читання з паузами",
                instruction = "Прочитай текст, роблячи чіткі паузи на кожній крапці (2 сек) і комі (1 сек). Не поспішай!",
                content = ExerciseContent.ReadingText(
                    text = "Є три речі, які не можна повернути. Час. Слово. Можливість. Тому: не втрачай часу, обирай слова, не пропускай можливостей.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_6_7",
                type = ExerciseType.READING,
                title = "Акценти в промові",
                instruction = "Прочитай, виділяючи ВЕЛИКІ слова голосом — гучніше та з паузою перед ними.",
                content = ExerciseContent.ReadingText(
                    text = "Сьогодні ми починаємо НОВИЙ етап. Це не просто зміни — це ТРАНСФОРМАЦІЯ. Кожен з вас — ВАЖЛИВИЙ. Разом ми досягнемо НЕМОЖЛИВОГО.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 12
    )

    // ==================== УРОК 7: Підсумок тижня ====================
    private fun getLesson7(): Lesson = Lesson(
        id = "intonation_lesson_7",
        courseId = COURSE_ID,
        dayNumber = 7,
        title = "Підсумок першого тижня",
        description = "Повторення та закріплення: радість, сум, гнів, здивування, паузи",
        theory = TheoryContent(
            text = """
                Перший тиждень — базові емоції опановано!

                Вітаю! Ти пройшов перший тиждень курсу "Магія інтонації". Давай підсумуємо, що ти тепер вмієш:

                Урок 1: Основи інтонації
                • Діапазон голосу — від низького до високого
                • Різниця між питанням і твердженням
                • Логічний наголос змінює значення речення

                Урок 2: Радість
                • Вищий тон + швидший темп + посмішка в голосі
                • Градації: від задоволення до ейфорії

                Урок 3: Сум
                • Нижчий тон + повільний темп + тихіший голос
                • Градації: від ностальгії до співчуття

                Урок 4: Гнів
                • Твердий голос + холодний контроль
                • Гнів — це сила, не крик

                Урок 5: Здивування
                • Різкий підйом голосу + подовжені голосні
                • Позитивне, негативне, нейтральне здивування

                Урок 6: Паузи та акценти
                • Пауза — інструмент, не слабкість
                • Один акцент на речення

                Наступний тиждень буде ще цікавішим: наративна інтонація, ділове мовлення, риторичні прийоми та драматичні техніки!
            """.trimIndent(),
            tips = listOf(
                "Практикуй емоції в повсякденному житті — вітайся радісно, дякуй тепло",
                "Слухай як говорять актори, ведучі, оратори — вчися у найкращих",
                "Записуй себе і слухай — це найкращий спосіб помітити прогрес"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_7_1",
                type = ExerciseType.ARTICULATION,
                title = "Розминка діапазону",
                instruction = "Сирена: від найнижчої ноти до найвищої і назад на 'а-а-а'. Потім сходинки: 5 рівнів вгору і вниз на 'ма'. По 3 рази кожну вправу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_2",
                type = ExerciseType.ARTICULATION,
                title = "Швидка зміна емоцій",
                instruction = "Вимов 'Сьогодні чудовий день' з різними емоціями: 1) радісно, 2) сумно, 3) здивовано, 4) сердито, 5) нейтрально. Швидко перемикайся між ними. 2 кола.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_3",
                type = ExerciseType.ARTICULATION,
                title = "Одне слово — п'ять емоцій",
                instruction = "Вимов 'Привіт' з кожною емоцією: радість, сум, гнів, здивування, нейтрально. Потім 'Дякую' так само. Потім 'До побачення'. Відчуй різницю.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_4",
                type = ExerciseType.ARTICULATION,
                title = "Паузи та акценти — повторення",
                instruction = "Вимов: 'Це... ВАЖЛИВО.', 'Я... НЕ ЗГОДЕН.', 'Відповідь... ТАК!' — з паузою 2 сек перед акцентованим словом. По 3 рази кожну фразу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_5",
                type = ExerciseType.EMOTION_READING,
                title = "Радісна новина",
                instruction = "Прочитай з радістю та захопленням!",
                content = ExerciseContent.ReadingText(
                    text = "Це неймовірно! Ми виграли! Я так радий, що не можу повірити! Це найкращий день у моєму житті! Треба святкувати!",
                    emotion = Emotion.JOY
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_6",
                type = ExerciseType.EMOTION_READING,
                title = "Сумне прощання",
                instruction = "Прочитай з теплим сумом.",
                content = ExerciseContent.ReadingText(
                    text = "Ось і настав час прощатися. Час летить так швидко. Я буду сумувати за цими моментами. Бережи себе. До зустрічі.",
                    emotion = Emotion.SADNESS
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_7_7",
                type = ExerciseType.EMOTION_READING,
                title = "Тверда позиція",
                instruction = "Прочитай з холодним гнівом та твердістю.",
                content = ExerciseContent.ReadingText(
                    text = "Досить. Я більше не буду це терпіти. Моє рішення остаточне. Це не обговорюється. Крапка.",
                    emotion = Emotion.ANGER
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_7_8",
                type = ExerciseType.EMOTION_READING,
                title = "Фінальний тест: емоційна історія",
                instruction = "Прочитай історію, змінюючи інтонацію відповідно до змісту: здивування → радість → занепокоєння → полегшення.",
                content = ExerciseContent.ReadingText(
                    text = "Що це?! Лист! Я виграв подорож! Неймовірно! Але зачекай... термін закінчується завтра?! О ні! Стоп... сьогодні тільки понеділок. Уф, встигаю!",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            )
        ),
        estimatedMinutes = 14
    )

    // ==================== ТИЖДЕНЬ 2 ====================

    // ==================== УРОК 8: Наративна інтонація ====================
    private fun getLesson8(): Lesson = Lesson(
        id = "intonation_lesson_8",
        courseId = COURSE_ID,
        dayNumber = 8,
        title = "Наративна інтонація",
        description = "Вчимося розповідати історії голосом, який тримає увагу",
        theory = TheoryContent(
            text = """
                Наратор — це актор без костюма

                Коли ти розповідаєш історію, твій голос стає режисером сприйняття слухача. Наратор — не той, хто просто зачитує події, а той, хто керує увагою: де прискорити, де зупинитись, де стишити голос до шепоту. Хороша історія, розказана монотонно, звучить гірше за посередню історію, розказану живо.

                Що таке наративна інтонація? Це вміння змінювати темп, гучність і тон відповідно до того, що відбувається в сюжеті — не одна емоція на всю розповідь, а постійна динаміка.

                Три рівні наративного голосу:

                1. Голос розповіді — рівний, трохи нижчий, "голос за кадром"
                2. Голос дії — швидший, енергійніший, коли щось відбувається
                3. Голос прямої мови — змінюється залежно від персонажа, якого цитуєш

                Як керувати темпом у розповіді?

                • Опис і роздуми — повільніше, спокійніше
                • Дія і напруга — швидше, енергійніше
                • Кульмінація — коротка пауза перед нею, потім чіткий акцент
                • Розв'язка — сповільнення, ніби видих після напруги

                Типова помилка: розповідати всю історію в одному темпі й на одній гучності. Слухач втрачає інтерес уже на середині, бо немає динаміки, за якою можна стежити.

                Лайфхак: уяви, що розповідаєш історію другові за кавою, а не читаєш звіт. Природна жестикуляція голосом з'явиться сама, якщо ти справді "проживаєш" сюжет, поки розповідаєш.

                Сьогодні ми тренуємо голос оповідача: як звучить опис, як звучить дія, і як плавно переходити між ними.
            """.trimIndent(),
            tips = listOf(
                "Постав голос: опис — спокійний, дія — енергійна, кульмінація — з паузою",
                "Розповідай так, ніби ділишся історією з другом, а не читаєш звіт",
                "Слухай аудіокниги — професійні читці показують клас наративної інтонації"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_8_1",
                type = ExerciseType.ARTICULATION,
                title = "Голос оповідача",
                instruction = "Вимов 'Був звичайний вівторок' спокійним голосом оповідача. Потім 'Раптом двері розчинилися!' — швидко, енергійно. Відчуй контраст. Повтори 5 разів, чергуючи.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_8_2",
                type = ExerciseType.ARTICULATION,
                title = "Пряма мова персонажа",
                instruction = "Вимов фразу 'Я йду додому' від імені трьох різних персонажів: втомленої людини, наляканої дитини, впевненого лідера. Зміни голос під кожного. По 2 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_8_3",
                type = ExerciseType.ARTICULATION,
                title = "Прискорення до кульмінації",
                instruction = "Вимовляй з наростанням: 'Він біг... він біг швидше... він біг щодуху... і встиг!' — темп і енергія зростають до фінального слова. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_8_4",
                type = ExerciseType.ARTICULATION,
                title = "Пауза перед кульмінацією",
                instruction = "Вимов: 'Двері відчинилися і...' — пауза 2 секунди — 'там нікого не було.' Відчуй як пауза підсилює несподіванку. Повтори 5 разів з різними закінченнями.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_8_5",
                type = ExerciseType.ARTICULATION,
                title = "Сповільнення після напруги",
                instruction = "Вимов: 'Серце калатало. Але тепер... усе було спокійно.' — перша частина швидко й напружено, друга — повільно, ніби видих. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_8_6",
                type = ExerciseType.EMOTION_READING,
                title = "Історія з динамікою",
                instruction = "Прочитай історію, змінюючи темп і гучність відповідно до подій: спокійно → напружено → з полегшенням.",
                content = ExerciseContent.ReadingText(
                    text = "Вечір був тихим. Аня йшла додому, як завжди. Раптом позаду почулися кроки — швидше, ближче! Вона озирнулась... і засміялась: це був її сусід із собакою. Тиша знову огорнула вулицю.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_8_7",
                type = ExerciseType.EMOTION_READING,
                title = "Казка на ніч",
                instruction = "Прочитай як досвідчений оповідач, що розказує казку дитині перед сном — з м'якою динамікою.",
                content = ExerciseContent.ReadingText(
                    text = "Далеко-далеко, за синім морем, жив маленький дракон. Він мріяв навчитися літати. Одного дня він зібрав усю відвагу, розправив крила... і злетів! Відтоді небо стало його домом.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 12
    )

    // ==================== УРОК 9: Ділова інтонація ====================
    private fun getLesson9(): Lesson = Lesson(
        id = "intonation_lesson_9",
        courseId = COURSE_ID,
        dayNumber = 9,
        title = "Ділова інтонація",
        description = "Голос, якому довіряють на переговорах і презентаціях",
        theory = TheoryContent(
            text = """
                Голос, якому довіряють у переговорах

                Ділова інтонація — це баланс між теплом і авторитетом. Занадто м'яко — не сприймуть серйозно. Занадто жорстко — відштовхнеш співрозмовника. Мета ділового голосу — звучати впевнено, спокійно і компетентно, без зайвих емоційних перепадів.

                Як звучить ділова інтонація?

                • Рівний, контрольований темп — не поспішаєш, але й не тягнеш
                • Помірна гучність — чути кожне слово, без крику
                • Мінімум емоційних "гойдалок" — стабільність замість драми
                • Чіткі логічні наголоси — важливі цифри й факти виділяються голосом
                • Впевнене завершення речень — голос не "провисає" в кінці

                Де потрібна ділова інтонація?

                1. Переговори — спокійна впевненість переконує краще за тиск
                2. Презентації клієнтам — професійність через голос
                3. Робочі зустрічі — чіткість без зайвих емоцій
                4. Телефонні дзвінки — голос замінює мову тіла й вираз обличчя

                Типова помилка: плутати "діловий" з "монотонним". Ділова інтонація все ще жива — просто стриманіша, без амплітуди побутової розмови.

                Важливо: невпевненість чути миттєво — голос, що піднімається в кінці ствердного речення (ніби питання), сигналізує сумнів. Тренуй завершувати думки твердо.

                Сьогодні ми відпрацюємо голос, якому довіряють на переговорах і презентаціях.
            """.trimIndent(),
            tips = listOf(
                "Ділова інтонація — стримана, але не монотонна: тримай стабільний, впевнений темп",
                "Завершуй речення твердо, голос не повинен 'провисати' чи 'зависати' питально",
                "Виділяй голосом цифри та ключові факти — це підсилює довіру"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_9_1",
                type = ExerciseType.ARTICULATION,
                title = "Впевнене завершення",
                instruction = "Вимов 'Ми завершимо проєкт до п'ятниці' спочатку з питальною інтонацією в кінці (невпевнено), потім твердо, з чіткою крапкою в голосі. Відчуй різницю. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_9_2",
                type = ExerciseType.ARTICULATION,
                title = "Наголос на цифрах",
                instruction = "Вимов: 'Ми збільшили дохід на ТРИДЦЯТЬ відсотків за ТРИ місяці' — виділяючи цифри голосом, трохи гучніше й з мікропаузою перед ними. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_9_3",
                type = ExerciseType.ARTICULATION,
                title = "Спокійна впевненість",
                instruction = "Вимов: 'Я розумію ваше занепокоєння. Давайте розглянемо факти.' — рівним, спокійним, авторитетним голосом, без поспіху. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_9_4",
                type = ExerciseType.ARTICULATION,
                title = "Ділова відмова",
                instruction = "Вимов твердо, але ввічливо: 'На жаль, ці умови нам не підходять. Пропонуємо інший варіант.' — без агресії, але без вагань. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_9_5",
                type = ExerciseType.ARTICULATION,
                title = "Телефонна презентація",
                instruction = "Уяви, що презентуєш продукт по телефону, без візуального контакту. Вимов: 'Наш продукт вирішує саме цю проблему, і ось як' — голосом, компенсуючи відсутність міміки енергією тону. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_9_6",
                type = ExerciseType.READING,
                title = "Ділова презентація",
                instruction = "Прочитай як презентацію клієнту — впевнено, чітко, з наголосом на цифрах.",
                content = ExerciseContent.ReadingText(
                    text = "Доброго дня. Дякую, що знайшли час. Сьогодні я представлю рішення, яке скоротить ваші витрати на двадцять відсотків уже в першому кварталі. Перейдімо до деталей.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_9_7",
                type = ExerciseType.READING,
                title = "Переговори",
                instruction = "Прочитай як частину ділових переговорів — спокійно, професійно, доброзичливо.",
                content = ExerciseContent.ReadingText(
                    text = "Ми цінуємо нашу співпрацю і хочемо знайти рішення, вигідне обом сторонам. Пропоную розглянути три варіанти та обрати оптимальний. Готові обговорити деталі.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 12
    )

    // ==================== УРОК 10: Риторичні питання ====================
    private fun getLesson10(): Lesson = Lesson(
        id = "intonation_lesson_10",
        courseId = COURSE_ID,
        dayNumber = 10,
        title = "Риторичні питання",
        description = "Питання, яке насправді є твердженням — і як його вимовляти",
        theory = TheoryContent(
            text = """
                Питання, на яке не чекають відповіді

                Риторичне питання — потужний прийом оратора. Це питання, яке насправді є твердженням, замаскованим під запит. "Хіба ми не заслуговуємо на краще?" звучить переконливіше за "Ми заслуговуємо на краще", бо змушує слухача самому подумки відповісти "так".

                Як звучить риторичне питання? Тут криється парадокс: справжнє питання піднімає голос в кінці, а риторичне — часто ні. Воно звучить майже як твердження, з легким питальним відтінком, але без справжнього очікування відповіді.

                Функції риторичних питань:

                1. Залучення уваги — слухач мимоволі "відповідає" в голові
                2. Підкреслення очевидного — "Хіба це не логічно?"
                3. Створення драматизму — "Скільки ще можна чекати?"
                4. М'яка критика — питання звучить менш агресивно за твердження

                Типова помилка: вимовляти риторичне питання з надто вираженою питальною інтонацією — тоді слухач чекає відповіді і губиться, коли її не отримує. Риторичне питання потребує впевненого, майже стверджувального тону.

                Важливо: пауза після риторичного питання — обов'язкова. Дай слухачеві частку секунди, щоб подумки відповісти, перш ніж продовжувати думку.

                Приклад: "Хто з нас не мріяв про краще життя?" — (пауза) — "Саме тому ми тут сьогодні."

                Сьогодні тренуємо інтонацію, яка перетворює прості питання на потужний ораторський інструмент.
            """.trimIndent(),
            tips = listOf(
                "Риторичне питання звучить як твердження з легким питальним відтінком, не як справжнє запитання",
                "Роби паузу після риторичного питання — дай слухачеві подумки відповісти",
                "Використовуй риторичні питання, щоб залучити увагу без прямого твердження"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_10_1",
                type = ExerciseType.ARTICULATION,
                title = "Питання vs риторичне питання",
                instruction = "Вимов 'Ти цього хочеш?' як справжнє питання (з підйомом, чекаючи відповіді). Потім те саме як риторичне (впевнено, без очікування відповіді). Відчуй різницю. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_10_2",
                type = ExerciseType.ARTICULATION,
                title = "Пауза після риторичного питання",
                instruction = "Вимов: 'Хіба ми не заслуговуємо на краще?' — пауза 2 секунди — 'Звісно заслуговуємо.' Повтори 5 разів, тримаючи паузу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_10_3",
                type = ExerciseType.ARTICULATION,
                title = "Ланцюжок риторичних питань",
                instruction = "Вимов поспіль: 'Скільки ще чекати? Скільки ще терпіти? Скільки ще мовчати?' — з наростанням інтенсивності, короткі паузи між питаннями. Повтори 3 рази.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_10_4",
                type = ExerciseType.ARTICULATION,
                title = "М'яка критика через питання",
                instruction = "Вимов: 'Чи справді ми зробили все можливе?' замість прямого 'Ми не зробили все можливе' — тим самим тоном впевненого докору. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_10_5",
                type = ExerciseType.ARTICULATION,
                title = "Риторичне питання в промові",
                instruction = "Вимов: 'Хто з нас не мріяв змінити щось на краще?' — впевнено, з легкою паузою в кінці, ніби запрошуючи слухача погодитись подумки. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_10_6",
                type = ExerciseType.READING,
                title = "Промова з риторичними питаннями",
                instruction = "Прочитай як фрагмент мотиваційної промови, з паузами після кожного риторичного питання.",
                content = ExerciseContent.ReadingText(
                    text = "Хіба не час щось змінити? Хіба ми не заслуговуємо на краще майбутнє? Я вірю, що заслуговуємо. І саме тому пропоную почати вже сьогодні.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_10_7",
                type = ExerciseType.READING,
                title = "Заклик до дії",
                instruction = "Прочитай як заклик до дії — риторичні питання звучать впевнено, без справжнього очікування відповіді.",
                content = ExerciseContent.ReadingText(
                    text = "Скільки разів ми відкладали важливі рішення? Скільки можливостей втратили через сумніви? Досить чекати. Час діяти — саме зараз.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ==================== УРОК 11: Драматична пауза ====================
    private fun getLesson11(): Lesson = Lesson(
        id = "intonation_lesson_11",
        courseId = COURSE_ID,
        dayNumber = 11,
        title = "Драматична пауза",
        description = "Мовчання як інструмент напруги, гумору й ваги слів",
        theory = TheoryContent(
            text = """
                Мовчання, яке говорить голосніше за слова

                Ти вже знаєш, що пауза підкреслює важливе слово. Але драматична пауза — це окремий інструмент: вона не просто виділяє слово, вона керує емоціями слухача, тримаючи його в напрузі.

                Три види драматичної паузи:

                1. Пауза очікування — перед розкриттям таємниці чи результату ("І переможець... [пауза] ...команда синіх!")
                2. Комедійна пауза — перед панчлайном жарту, дає слухачу час "приготуватися" сміятися
                3. Пауза після важкої правди — після серйозного заявлення, дає час осмислити вагу сказаного

                Як довго тримати паузу? Довжина драматичної паузи залежить від контексту: секунда-дві для легкого ефекту, три-п'ять секунд для сильного драматичного моменту. Занадто довга пауза без причини звучить як забудькуватість, а не майстерність.

                Типова помилка: боятися тримати паузу довше секунди. Початківці поспішають заповнити тишу, і драматичний ефект зникає. Справжня майстерність — витримати незручність мовчання заради ефекту.

                Секрет коміків: пауза перед панчлайном працює, тому що вона створює очікування. Мозок слухача добудовує можливі варіанти — і несподіваний фінал спрацьовує сильніше.

                Ключовий інсайт: пауза — це не відсутність мовлення, а активна дія. Вона потребує впевненості й контролю над власним диханням і темпом не менше, ніж саме мовлення.

                Сьогодні ми тренуємо витримку — вміння мовчати саме стільки, скільки потрібно для максимального ефекту.
            """.trimIndent(),
            tips = listOf(
                "Тримай паузу довше, ніж здається комфортним — саме тоді вона працює",
                "Комедійна пауза перед панчлайном підсилює жарт удвічі",
                "Пауза — це дія, а не порожнеча: контролюй дихання під час неї"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_11_1",
                type = ExerciseType.ARTICULATION,
                title = "Пауза очікування",
                instruction = "Вимов: 'І результат аналізу показав...' — пауза 4 секунди — '...повне одужання!' Тримай паузу, не поспішай. Повтори 5 разів з різними фіналами.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_11_2",
                type = ExerciseType.ARTICULATION,
                title = "Комедійна пауза",
                instruction = "Вимов з паузою перед панчлайном: 'Я довго думав, що подарувати на день народження...' — пауза 2 секунди — '...і подарував собі вихідний.' Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_11_3",
                type = ExerciseType.ARTICULATION,
                title = "Пауза після важкої правди",
                instruction = "Вимов: 'Ми більше не працюємо разом.' — пауза 3 секунди, дай сказаному 'осісти' — потім продовж: 'Але я вдячний за цей досвід.' Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_11_4",
                type = ExerciseType.ARTICULATION,
                title = "Тримай мовчання",
                instruction = "Порахуй подумки до п'яти в повній тиші після фрази: 'Зараз я скажу дещо важливе.' Не заповнюй паузу звуками чи 'е-е'. Потім договори будь-яку думку. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_11_5",
                type = ExerciseType.ARTICULATION,
                title = "Три типи пауз поспіль",
                instruction = "Вимов три фрази з різними паузами: очікування (4 сек), комедійна (2 сек), після важкої правди (3 сек). Відчуй різницю в довжині та настрої кожної.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 90
                ),
                durationSeconds = 90,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_11_6",
                type = ExerciseType.EMOTION_READING,
                title = "Історія з драматичною паузою",
                instruction = "Прочитай, тримаючи довгу паузу перед останнім словом — дай напрузі накопичитися.",
                content = ExerciseContent.ReadingText(
                    text = "Він відкрив конверт. Руки тремтіли. Всередині був лист, якого він чекав роками. Він розгорнув аркуш... і прочитав перше слово: 'Вітаємо'.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_11_7",
                type = ExerciseType.EMOTION_READING,
                title = "Комедійна історія",
                instruction = "Прочитай як анекдот з другом — з паузою перед несподіваним фіналом.",
                content = ExerciseContent.ReadingText(
                    text = "Я готувався до співбесіди тижнями. Вивчив усе про компанію. Продумав кожну відповідь. А потім переплутав адресу і прийшов... на співбесіду в іншу компанію.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 13
    )

    // ==================== УРОК 12: Емоційна гучність ====================
    private fun getLesson12(): Lesson = Lesson(
        id = "intonation_lesson_12",
        courseId = COURSE_ID,
        dayNumber = 12,
        title = "Емоційна гучність",
        description = "Гучність як свідомий інструмент виразності, а не автоматична реакція",
        theory = TheoryContent(
            text = """
                Гучність — це не про децибели, а про силу

                Багато хто думає, що гучність = сила, а тихо = слабко. Це помилка. Шепіт може бути потужнішим за крик, якщо він доречний. Емоційна гучність — це свідомий вибір рівня звуку для максимального ефекту, а не автоматична реакція на емоцію.

                Як гучність передає емоції?

                • Голосно — енергія, впевненість, урочистість, гнів, ентузіазм
                • Тихо — інтимність, довіра, секрет, серйозність, ніжність
                • Різка зміна гучності — привертає увагу миттєво
                • Поступове наростання — створює відчуття кульмінації, що насувається

                Прийом "шепіт у залі": досвідчені оратори іноді навмисно стишують голос у важливий момент. Парадоксально, але це змушує аудиторію слухати уважніше — люди підсвідомо нахиляються ближче до тихого голосу.

                Контраст гучності: найсильніший ефект — це не постійна гучність, а контраст: тихий вступ → наростання → гучна кульмінація → знову тихо для фіналу. Монотонна гучність, як і монотонна інтонація, втомлює.

                Типова помилка: плутати "виразно" з "голосно". Можна говорити емоційно й переконливо на тихому рівні звуку — виразність в динаміці тону, а не тільки в силі.

                Сьогодні тренуємо керування гучністю як окремим інструментом виразності, незалежно від сили самої емоції.
            """.trimIndent(),
            tips = listOf(
                "Шепіт може бути сильнішим за крик — тихий голос змушує слухати уважніше",
                "Найсильніший ефект — це контраст гучності, а не постійна гучність",
                "Виразність — це не тільки про 'голосно', а про керовану динаміку"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_12_1",
                type = ExerciseType.ARTICULATION,
                title = "Діапазон гучності",
                instruction = "Вимов 'Слухай уважно' п'ятьма рівнями гучності: шепіт, тихо, звичайно, голосно, дуже голосно. Відчуй весь діапазон свого голосу.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_12_2",
                type = ExerciseType.ARTICULATION,
                title = "Шепіт у залі",
                instruction = "Вимов пошепки, але чітко й виразно: 'Це секрет, який я розповідаю тільки тобі.' Уяви, що навколо повна тиша і всі нахиляються, щоб почути. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_12_3",
                type = ExerciseType.ARTICULATION,
                title = "Наростання гучності",
                instruction = "Вимов: 'Ми можемо. Ми зробимо. МИ ПЕРЕМОЖЕМО!' — з поступовим наростанням гучності та енергії до фінального слова. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_12_4",
                type = ExerciseType.ARTICULATION,
                title = "Контраст гучності",
                instruction = "Вимов пошепки: 'Тихо...' потім голосно: '...а тепер СЛУХАЙТЕ ВСІ!' — різка зміна від шепоту до гучного заклику. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_12_5",
                type = ExerciseType.ARTICULATION,
                title = "Тиха впевненість",
                instruction = "Вимов твердо, але тихо: 'Я знаю, що права. І доведу це фактами.' — без крику, впевненість через тон, а не гучність. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_12_6",
                type = ExerciseType.EMOTION_READING,
                title = "Промова з динамікою гучності",
                instruction = "Прочитай, поступово підвищуючи гучність від майже пошепки до впевненого фіналу.",
                content = ExerciseContent.ReadingText(
                    text = "Спочатку було тихо. Дуже тихо. Ніхто не вірив у нас. Але ми працювали, день за днем, крок за кроком. І сьогодні я можу сказати на весь голос: ми зробили це!",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_12_7",
                type = ExerciseType.EMOTION_READING,
                title = "Секрет пошепки",
                instruction = "Прочитай пошепки, ніби ділишся важливою таємницею — чітко, але дуже тихо.",
                content = ExerciseContent.ReadingText(
                    text = "Підійди ближче. Те, що я зараз скажу, не повинен чути ніхто інший. Це найважливіша таємниця, яку я коли-небудь довіряв комусь.",
                    emotion = null
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            )
        ),
        estimatedMinutes = 13
    )

    // ==================== УРОК 13: Натхненна промова ====================
    private fun getLesson13(): Lesson = Lesson(
        id = "intonation_lesson_13",
        courseId = COURSE_ID,
        dayNumber = 13,
        title = "Натхненна промова",
        description = "Об'єднуємо наратив, паузи, гучність і риторику в одну потужну промову",
        theory = TheoryContent(
            text = """
                Голос, що запалює інших

                Натхненна промова об'єднує все, що ти вивчив цього тижня: наратив, паузи, гучність, риторичні питання. Це вершина ораторської майстерності — вміння говорити так, щоб люди відчули бажання діяти.

                З чого складається натхненна інтонація?

                1. Наростання енергії — від спокійного початку до потужного фіналу
                2. Повтори — ключова фраза, повторена кілька разів, б'є сильніше (анафора)
                3. Ритм — короткі речення в кульмінації звучать як удари барабана
                4. Паузи для дихання аудиторії — момент тиші перед головною думкою
                5. Особиста щирість — найпотужніші промови звучать від душі, не завчено

                Прийом повтору (анафора): "Ми не здамося. Ми не зупинимось. Ми не забудемо." — повторення структури створює ритм і запам'ятовується краще за різноманітні формулювання.

                Крещендо промови: найкращі мотиваційні промови будуються як музичний твір: спокійний вступ, наростання середини, потужна кульмінація, і фінальний акорд — коротка, потужна фраза, яка залишається в пам'яті.

                Типова помилка: почати на максимальній енергії з першого речення. Тоді нікуди рухатись — немає драматургії, немає наростання. Почни спокійно, збережи силу для фіналу.

                Ключовий інсайт: люди запам'ятовують не слова промови, а те, що відчули, слухаючи її. Твоя інтонація — головний інструмент створення цього відчуття.

                Сьогодні ти поєднаєш усі техніки тижня в одну потужну натхненну промову.
            """.trimIndent(),
            tips = listOf(
                "Почни спокійно — збережи енергію для наростання, а не витрачай одразу",
                "Повторюй ключову фразу (анафора) — це створює ритм і запам'ятовується",
                "Люди пам'ятають відчуття від промови, а не точні слова — грай на емоціях голосом"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_13_1",
                type = ExerciseType.ARTICULATION,
                title = "Анафора",
                instruction = "Вимов з наростанням: 'Ми не здамося. Ми не зупинимось. Ми не забудемо.' — кожне речення трохи енергійніше за попереднє. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_13_2",
                type = ExerciseType.ARTICULATION,
                title = "Ритм коротких речень",
                instruction = "Вимов чітко, як удари барабана: 'Встань. Іди. Дій. Перемагай.' — кожне слово окремо, з короткою паузою, наростаючою енергією. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_13_3",
                type = ExerciseType.ARTICULATION,
                title = "Крещендо промови",
                instruction = "Вимов, поступово нарощуючи енергію від спокійного до потужного: 'Колись це здавалось неможливим. Потім — важким. А сьогодні — це наша реальність!' Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_13_4",
                type = ExerciseType.ARTICULATION,
                title = "Пауза перед головною думкою",
                instruction = "Вимов: 'Є лише одне, що відділяє нас від мети...' — пауза 3 секунди — '...наша віра в себе.' Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_13_5",
                type = ExerciseType.ARTICULATION,
                title = "Фінальний акорд",
                instruction = "Вимов коротку, потужну фразу, яка має 'залишитись у пам'яті': 'Сьогодні починається нове ти.' — впевнено, з енергією, без поспіху. Повтори 5 разів різними тонами впевненості.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_13_6",
                type = ExerciseType.EMOTION_READING,
                title = "Натхненна промова",
                instruction = "Прочитай як фінал мотиваційної промови — з наростанням енергії до останнього речення.",
                content = ExerciseContent.ReadingText(
                    text = "Колись у нас нічого не було, крім мрії. Ми не мали ресурсів. Ми не мали досвіду. Але ми мали віру. І ця віра привела нас сюди. Сьогодні я кажу вам: наступний крок — ваш.",
                    emotion = null
                ),
                durationSeconds = 75,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_13_7",
                type = ExerciseType.EMOTION_READING,
                title = "Заклик до дії",
                instruction = "Прочитай як фінальний заклик — впевнено, з коротким ритмом у кінці.",
                content = ExerciseContent.ReadingText(
                    text = "Час настав. Досить сумніватися. Досить чекати ідеального моменту, бо ідеального моменту не існує. Є лише зараз. І зараз — саме той час, щоб почати.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 14
    )

    // ==================== УРОК 14: Підсумок тижня 2 ====================
    private fun getLesson14(): Lesson = Lesson(
        id = "intonation_lesson_14",
        courseId = COURSE_ID,
        dayNumber = 14,
        title = "Підсумок тижня 2",
        description = "Повторення та закріплення: наратив, ділова інтонація, риторика, пауза, гучність, натхнення",
        theory = TheoryContent(
            text = """
                Другий тиждень — від емоцій до майстерності

                Вітаю! Ти пройшов другий тиждень курсу "Магія інтонації". Якщо перший тиждень навчив тебе базових емоцій, то другий — навчив використовувати голос як інструмент впливу. Підсумуємо:

                Урок 8: Наративна інтонація
                • Голос оповідача: опис — спокійно, дія — енергійно
                • Пряма мова персонажів звучить по-різному

                Урок 9: Ділова інтонація
                • Стриманість без монотонності
                • Впевнене завершення речень, наголос на фактах

                Урок 10: Риторичні питання
                • Звучать як твердження, з паузою після
                • Залучають слухача без прямого твердження

                Урок 11: Драматична пауза
                • Три типи: очікування, комедійна, після важкої правди
                • Пауза — це дія, а не порожнеча

                Урок 12: Емоційна гучність
                • Шепіт може бути сильнішим за крик
                • Контраст гучності діє сильніше за постійну гучність

                Урок 13: Натхненна промова
                • Анафора, ритм, крещендо, фінальний акорд
                • Люди пам'ятають відчуття, а не слова

                Наступний тиждень — фінальний ривок: сарказм, тепла інтонація, авторитетність, емпатія, швидке перемикання емоцій — і випускний тест, де ти покажеш усе, чого навчився.
            """.trimIndent(),
            tips = listOf(
                "Практикуй риторичні питання й паузи в реальних розмовах — вони працюють і в побуті",
                "Записуй свої натхненні промови й порівнюй з першим тижнем — прогрес буде чутний",
                "Третій тиждень — найскладніший, готуйся тренуватись щодня"
            )
        ),
        exercises = listOf(
            LessonExercise(
                id = "intonation_ex_14_1",
                type = ExerciseType.ARTICULATION,
                title = "Наратив: опис vs дія",
                instruction = "Вимов 'Ліс був тихим' спокійно, потім 'Раптом щось затріщало!' — енергійно. Чергуй 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_14_2",
                type = ExerciseType.ARTICULATION,
                title = "Ділова впевненість",
                instruction = "Вимов твердо, без питальних 'хвостиків': 'Проєкт буде завершено вчасно.' Повтори 5 разів, стежачи, щоб голос не 'провисав' у кінці.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_14_3",
                type = ExerciseType.ARTICULATION,
                title = "Риторичне питання + пауза",
                instruction = "Вимов: 'Хіба ми не заслуговуємо на краще?' — пауза 2 секунди — і продовж власною думкою. Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_14_4",
                type = ExerciseType.ARTICULATION,
                title = "Драматична пауза",
                instruction = "Вимов: 'І тоді сталося...' — пауза 3 секунди — '...те, чого ніхто не очікував.' Повтори 5 разів.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 60
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_14_5",
                type = ExerciseType.ARTICULATION,
                title = "Контраст гучності",
                instruction = "Вимов пошепки: 'Тихо...' потім голосно: 'А ТЕПЕР УВАГА!' Повтори 5 разів, відчуваючи контраст.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION)
            ),
            LessonExercise(
                id = "intonation_ex_14_6",
                type = ExerciseType.ARTICULATION,
                title = "Анафора-фінал",
                instruction = "Вимов з наростанням: 'Ми зможемо. Ми доведемо. Ми переможемо!' Повтори 5 разів, нарощуючи енергію.",
                content = ExerciseContent.ArticulationExercise(
                    hasTimer = true,
                    durationSeconds = 45
                ),
                durationSeconds = 45,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            ),
            LessonExercise(
                id = "intonation_ex_14_7",
                type = ExerciseType.EMOTION_READING,
                title = "Комбінована історія",
                instruction = "Прочитай, поєднуючи напругу, паузу та полегшення з тихим фіналом.",
                content = ExerciseContent.ReadingText(
                    text = "Ніч була тихою. Раптом — крок. Ще один. Хтось наближався... Це виявився лише кіт сусідів. Ми засміялися з полегшенням і пошепки побажали одне одному на добраніч.",
                    emotion = null
                ),
                durationSeconds = 60,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.TEMPO)
            ),
            LessonExercise(
                id = "intonation_ex_14_8",
                type = ExerciseType.EMOTION_READING,
                title = "Фінальний тест тижня 2: натхненна промова",
                instruction = "Прочитай як фінальну промову тижня — з наростанням упевненості й енергії до останнього речення.",
                content = ExerciseContent.ReadingText(
                    text = "Два тижні тому ти ледве керував власним голосом. Сьогодні — ти вмієш розповідати історії, переконувати фактами, тримати паузу і надихати словом. Це лише початок. Наступний тиждень покаже, на що ти справді здатний.",
                    emotion = null
                ),
                durationSeconds = 75,
                targetMetrics = listOf(SkillType.INTONATION, SkillType.CONFIDENCE)
            )
        ),
        estimatedMinutes = 16
    )

    // ==================== ТИЖДЕНЬ 3: Заглушки ====================

    private fun getLesson15Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_15",
        courseId = COURSE_ID,
        dayNumber = 15,
        title = "Саркастична інтонація",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson16Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_16",
        courseId = COURSE_ID,
        dayNumber = 16,
        title = "Тепла інтонація",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson17Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_17",
        courseId = COURSE_ID,
        dayNumber = 17,
        title = "Авторитетна інтонація",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson18Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_18",
        courseId = COURSE_ID,
        dayNumber = 18,
        title = "Емпатійна інтонація",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson19Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_19",
        courseId = COURSE_ID,
        dayNumber = 19,
        title = "Переключення емоцій",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson20Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_20",
        courseId = COURSE_ID,
        dayNumber = 20,
        title = "Фінальна підготовка",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )

    private fun getLesson21Placeholder(): Lesson = Lesson(
        id = "intonation_lesson_21",
        courseId = COURSE_ID,
        dayNumber = 21,
        title = "Випускний",
        description = "Скоро буде доступно",
        theory = TheoryContent(
            text = "Цей урок ще в розробці. Ми працюємо над тим, щоб зробити його якомога кориснішим для тебе. Дякуємо за терпіння!",
            tips = emptyList()
        ),
        exercises = emptyList(),
        estimatedMinutes = 10
    )
}
