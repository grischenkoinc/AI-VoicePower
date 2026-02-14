package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.TongueTwister
import com.aivoicepower.domain.model.exercise.ExerciseContent

/**
 * Provider for tongue twisters (скоромовки)
 * 140+ Ukrainian tongue twisters categorized by target sounds
 *
 * usedInLesson — shows if twister is used in a course lesson.
 * null = not used anywhere, safe to delete.
 * Non-null = used in that lesson, must be replaced if deleted.
 */
object TongueTwistersProvider {

    fun getAllTongueTwisters(): List<TongueTwister> {
        return getLabial() + getLingual() + getSoundR() + getSoundL() +
               getWhistling() + getSibilant() + getCombined()
    }

    fun getById(id: String): TongueTwister? = getAllTongueTwisters().find { it.id == id }

    fun toExerciseContent(id: String): ExerciseContent.TongueTwister {
        val tt = getById(id) ?: error("Tongue twister not found: $id")
        return ExerciseContent.TongueTwister(
            text = tt.text,
            difficulty = tt.difficulty,
            targetSounds = tt.targetSounds
        )
    }

    fun toSlowMotionContent(id: String, minDurationSeconds: Int = 30): ExerciseContent.SlowMotion {
        val tt = getById(id) ?: error("Tongue twister not found: $id")
        return ExerciseContent.SlowMotion(
            text = tt.text,
            targetSounds = tt.targetSounds,
            minDurationSeconds = minDurationSeconds
        )
    }

    fun toTwisterBattleContent(vararg ids: String): ExerciseContent.TongueTwisterBattle {
        val twisters = ids.map { id ->
            val tt = getById(id) ?: error("Tongue twister not found: $id")
            ExerciseContent.TongueTwister(
                text = tt.text,
                difficulty = tt.difficulty,
                targetSounds = tt.targetSounds
            )
        }
        return ExerciseContent.TongueTwisterBattle(
            twisters = twisters,
            allowMistakes = 0
        )
    }

    fun getTongueTwistersByDifficulty(difficulty: Int): List<TongueTwister> {
        return getAllTongueTwisters().filter { it.difficulty == difficulty }
    }

    fun getTongueTwistersBySound(targetSound: String): List<TongueTwister> {
        return getAllTongueTwisters().filter { targetSound in it.targetSounds }
    }

    fun getTongueTwistersByCategory(category: String): List<TongueTwister> {
        return getAllTongueTwisters().filter { it.category == category }
    }

    fun getRandomTongueTwisters(count: Int): List<TongueTwister> {
        return getAllTongueTwisters().shuffled().take(count)
    }

    // ========== Губні (Б, П, М, В, Ф) — 18 скоромовок ==========
    private fun getLabial(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_labial_001",
                text = "Був бик тупогуб, тупогубенький бичок.",
                difficulty = 1,
                targetSounds = listOf("Б", "П"),
                category = "Б-П-М-В-Ф",
                usedInLesson = "Чітке мовлення, Урок 2"
            ),
            TongueTwister(
                id = "tt_labial_002",
                text = "Пилип до липи прилип, до липи прилип Пилип.",
                difficulty = 2,
                targetSounds = listOf("П", "Л"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_003",
                text = "Бабин біб розцвів у дощ — буде бабі біб у борщ.",
                difficulty = 2,
                targetSounds = listOf("Б"),
                category = "Б-П-М-В-Ф",
                usedInLesson = "Чітке мовлення, Уроки 2, 10, 16, 20"
            ),
            TongueTwister(
                id = "tt_labial_004",
                text = "Мама милу Мілу милом мила.",
                difficulty = 1,
                targetSounds = listOf("М", "Л"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_005",
                text = "Впав Влас на клас, бо слизько в нас.",
                difficulty = 2,
                targetSounds = listOf("В", "Л"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_006",
                text = "Полин поламався, полиці попадали.",
                difficulty = 2,
                targetSounds = listOf("П", "Л"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_007",
                text = "Борис у Бориса борщу поборошнив.",
                difficulty = 3,
                targetSounds = listOf("Б", "Р"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_008",
                text = "Прийшов Прокіп — кипить окріп, пішов Прокіп — кипить окріп.",
                difficulty = 2,
                targetSounds = listOf("П", "К"),
                category = "Б-П-М-В-Ф",
                usedInLesson = "Чітке мовлення, Урок 2"
            ),
            TongueTwister(
                id = "tt_labial_009",
                text = "Від топота копит пил по полю летить.",
                difficulty = 2,
                targetSounds = listOf("П", "Т"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_010",
                text = "Бджоли базікали, базікали та й повибазікувались.",
                difficulty = 3,
                targetSounds = listOf("Б", "З"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_011",
                text = "Фараон Фаїна фарбував фасад фасолею фіолетовою.",
                difficulty = 4,
                targetSounds = listOf("Ф"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_012",
                text = "Мишко масло маслив, маслив, та й промаслив.",
                difficulty = 2,
                targetSounds = listOf("М", "С"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_013",
                text = "Вимий, Влас, миску, Влас, мий миску чисто.",
                difficulty = 2,
                targetSounds = listOf("В", "М"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_014",
                text = "Побіг бичок на мосток, спіткнувся бичок — став бичок мокрий бік.",
                difficulty = 3,
                targetSounds = listOf("Б", "М"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_015",
                text = "Павло поперек перелазу переліз — перед перелазом переплигнув.",
                difficulty = 4,
                targetSounds = listOf("П"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_016",
                text = "Батько Пафнутій пофарбував паркан пофарбованою фарбою.",
                difficulty = 3,
                targetSounds = listOf("П", "Ф"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_017",
                text = "Малий Петрик плів підпругу, попліта підпруга.",
                difficulty = 3,
                targetSounds = listOf("П"),
                category = "Б-П-М-В-Ф"
            ),
            TongueTwister(
                id = "tt_labial_018",
                text = "Баба Параска просить Васька — присадити прасці пласко.",
                difficulty = 3,
                targetSounds = listOf("П", "Б", "В"),
                category = "Б-П-М-В-Ф"
            )
        )
    }

    // ========== Язикові (Т, Д, Н) — 18 скоромовок ==========
    private fun getLingual(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_lingual_001",
                text = "Тато тесав тесом тес — тато втомився і тес кинув.",
                difficulty = 2,
                targetSounds = listOf("Т"),
                category = "Т-Д-Н",
                usedInLesson = "Чітке мовлення, Урок 3"
            ),
            TongueTwister(
                id = "tt_lingual_002",
                text = "Дід Данило дав дітям два дуби.",
                difficulty = 1,
                targetSounds = listOf("Д"),
                category = "Т-Д-Н",
                usedInLesson = "Чітке мовлення, Урок 3"
            ),
            TongueTwister(
                id = "tt_lingual_003",
                text = "Наш Наум нам наварив нуду — ні нам нудно, ні Наум нудний.",
                difficulty = 3,
                targetSounds = listOf("Н"),
                category = "Т-Д-Н",
                usedInLesson = "Чітке мовлення, Урок 3"
            ),
            TongueTwister(
                id = "tt_lingual_004",
                text = "Дятел дуб довбав, та не видовбав.",
                difficulty = 1,
                targetSounds = listOf("Д"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_005",
                text = "Та тітка тікала, тікала та й натікала.",
                difficulty = 2,
                targetSounds = listOf("Т"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_006",
                text = "Тетяна тата та Настуня тітку тут і там тягали.",
                difficulty = 3,
                targetSounds = listOf("Т", "Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_007",
                text = "Дніпром дуднить, дуднить Дніпро.",
                difficulty = 2,
                targetSounds = listOf("Д", "Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_008",
                text = "Нитки ниточки тоненькі натягнули до тину.",
                difficulty = 2,
                targetSounds = listOf("Н", "Т"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_009",
                text = "Де тонко, там рветься, де двоє — там деруться.",
                difficulty = 2,
                targetSounds = listOf("Т", "Д"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_010",
                text = "Два дровосіки, два дроворуби, два дровоколи говорили про Ларіона.",
                difficulty = 3,
                targetSounds = listOf("Д", "Р"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_011",
                text = "Не дуди, Данку, та й не ходи задом наперед.",
                difficulty = 2,
                targetSounds = listOf("Д", "Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_012",
                text = "Ніна ніжно наніжнила Настю, а Настя нині ніжна.",
                difficulty = 3,
                targetSounds = listOf("Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_013",
                text = "Танок дітей на подвір'ї — танцюють дітки до зорі.",
                difficulty = 2,
                targetSounds = listOf("Т", "Д"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_014",
                text = "Тітка Тетяна та Дмитро дивилися де тонкий дим.",
                difficulty = 3,
                targetSounds = listOf("Т", "Д"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_015",
                text = "Наталка надіяла намисто, а Надійка нову хустку.",
                difficulty = 2,
                targetSounds = listOf("Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_016",
                text = "На дні Дону — дуб, а на дубі — дуда.",
                difficulty = 2,
                targetSounds = listOf("Д", "Н"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_017",
                text = "Ткач тканину тче — тче ткач тканину ту.",
                difficulty = 4,
                targetSounds = listOf("Т"),
                category = "Т-Д-Н"
            ),
            TongueTwister(
                id = "tt_lingual_018",
                text = "Данні Данку дано надвір, а надвір Данкові дано далеко.",
                difficulty = 3,
                targetSounds = listOf("Д", "Н"),
                category = "Т-Д-Н"
            )
        )
    }

    // ========== Звук "Р" (22 скоромовки) ==========
    private fun getSoundR(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_r_001",
                text = "На дворі́ трава́, на траві́ дрова́. Не руби́ дрова́ на траві́ двора́.",
                difficulty = 2,
                targetSounds = listOf("Р", "Д", "Т"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_002",
                text = "Карл у Кла́ри вкра́в кора́ли, а Кла́ра у Ка́рла вкра́ла кларне́т.",
                difficulty = 2,
                targetSounds = listOf("Р", "Л", "К"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_003",
                text = "Три́дцять три кораблі́ лаві́рували, лаві́рували, та не вилаві́рували.",
                difficulty = 3,
                targetSounds = listOf("Р", "Л", "В"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_004",
                text = "Коро́ль Оре́л замо́вив торт із горі́хів і родзи́нок.",
                difficulty = 2,
                targetSounds = listOf("Р", "О", "К"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_005",
                text = "Перепі́лка Петро́ві перепели́не перо́ подарува́ла.",
                difficulty = 4,
                targetSounds = listOf("Р", "П"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_006",
                text = "Протоко́л про протоко́л протоко́лом записа́ли.",
                difficulty = 3,
                targetSounds = listOf("Р", "П", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_007",
                text = "Розпоря́дник розпоряди́вся розпоряди́ти розпоря́дження.",
                difficulty = 5,
                targetSounds = listOf("Р", "З", "П"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_008",
                text = "Ра́я рва́ла рі́пу, Ро́ма рва́в ре́дьку, рі́па й ре́дька – смачна́ ї́жа.",
                difficulty = 2,
                targetSounds = listOf("Р"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_009",
                text = "Рома́н Рома́нович рома́ни писа́в про Рома́на Романо́вича.",
                difficulty = 3,
                targetSounds = listOf("Р", "М"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_010",
                text = "Тара́с тера́су терпля́че тер, тер, та потім те́рти переста́в.",
                difficulty = 4,
                targetSounds = listOf("Р", "Т"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_011",
                text = "Три трубарі́ трубля́ть у труби́.",
                difficulty = 2,
                targetSounds = listOf("Р", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_012",
                text = "У пе́репела та перепі́лки п'ять перепеля́т.",
                difficulty = 3,
                targetSounds = listOf("Р", "П"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_013",
                text = "Прокі́п Проко́пович про Проко́па Проко́повича говори́в.",
                difficulty = 4,
                targetSounds = listOf("Р", "П", "К"),
                category = "Р",
                usedInLesson = "Чітке мовлення, Урок 6"
            ),
            TongueTwister(
                id = "tt_r_014",
                text = "Гри́цько Григо́рович гриби́ збира́в, а Гри́ша грибни́цю вари́в.",
                difficulty = 2,
                targetSounds = listOf("Р", "Г"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_015",
                text = "Архи́п охри́п, О́сип оси́п, а Харито́н хворі́ти не хо́че.",
                difficulty = 4,
                targetSounds = listOf("Р", "Х"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_016",
                text = "Григо́рій гра́блями згріба́в гра́вій, а Бори́с бо́роною боронува́в бур'я́ни.",
                difficulty = 5,
                targetSounds = listOf("Р", "Г", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_017",
                text = "Ра́но-ра́но два бара́ни бараба́нили в бараба́ни.",
                difficulty = 2,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_018",
                text = "Бобе́р біля бе́рега рив бе́рег борозна́ми.",
                difficulty = 3,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_019",
                text = "Проворони́ла воро́на вороненя́.",
                difficulty = 1,
                targetSounds = listOf("Р", "В"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_020",
                text = "Риє кріт но́ру, риє кріт трав'я́ні доро́жки.",
                difficulty = 3,
                targetSounds = listOf("Р", "К", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_021",
                text = "Бракоро́би роби́ли брак, та вибракува́ли бракоробі́в.",
                difficulty = 4,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_022",
                text = "Брат Кири́ла бра́ту Григо́рію подарува́в гриб.",
                difficulty = 3,
                targetSounds = listOf("Р", "Б", "Г"),
                category = "Р"
            )
        )
    }

    // ========== Звук "Л" (22 скоромовки) ==========
    private fun getSoundL(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_l_001",
                text = "Лі́ла ми́ла ля́льку ми́лом, ля́лька лю́ба Лі́лі була́.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_002",
                text = "Літа́ли ле́леки бі́ля вели́кої ріки́.",
                difficulty = 1,
                targetSounds = listOf("Л", "Р"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_003",
                text = "Павло́ пла́вав на плоту́ по Дніпру́ від села́.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_004",
                text = "Кла́ла Кла́ва кла́виші на ла́вку, на ла́вку кла́ла Кла́ва кла́виші.",
                difficulty = 3,
                targetSounds = listOf("Л", "К"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_005",
                text = "Не люби́ла Лю́дка льодяникі́в, люби́ла Лю́дка ли́ше льодяники́ з лимо́ном.",
                difficulty = 3,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_006",
                text = "Лопоті́в лопоту́н, лопоті́в, та ви́лопотав.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_007",
                text = "Ло́ла лі́пить їжачка́ з пластилі́ну на столі́.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_008",
                text = "Ко́ля ко́лов ко́ло коло́ди, ко́ли ко́ло Ко́лі ковза́ло.",
                difficulty = 4,
                targetSounds = listOf("Л", "К"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_009",
                text = "Леті́ли ле́леки з лі́ликами, і бі́лі ле́беді пла́вали бі́ля бе́рега.",
                difficulty = 4,
                targetSounds = listOf("Л", "Б"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_010",
                text = "Павло́ пали́в поле́нця в пе́чі, а по́піл із пе́чі вимета́в.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_011",
                text = "Оле́на коло́ла соло́му, соло́ма Оле́ну коло́ла.",
                difficulty = 3,
                targetSounds = listOf("Л", "О"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_012",
                text = "Лопоті́ла лопоту́ха, лопоті́ла, аж ло́пнула.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_013",
                text = "То́ля бі́ля то́поль теля́ті ли́стя нарва́в.",
                difficulty = 2,
                targetSounds = listOf("Л", "Т"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_014",
                text = "Пла́кала ля́лька на ла́вці, лежа́ла ля́лька на поли́ці.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_015",
                text = "Леті́ла бджі́лка че́рез пло́щу, сі́ла бджі́лка там на плющ.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_016",
                text = "Ї́ла Ю́ля я́блука в по́лі, люби́ла Ю́ля ли́ше я́блука з по́ля.",
                difficulty = 2,
                targetSounds = listOf("Л", "Я"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_017",
                text = "Леті́в мете́лик ко́ло ву́лика, облі́тав мете́лик ву́лик обере́жно.",
                difficulty = 3,
                targetSounds = listOf("Л", "В"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_018",
                text = "Лис лиси́чці ліс показа́в, лиси́ця ли́су лісові́ сте́жки показа́ла.",
                difficulty = 4,
                targetSounds = listOf("Л", "С"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_019",
                text = "Пили́п по ли́пі пи́лкою пиля́в.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_020",
                text = "Леле́ка леті́в на луг, на лу́зі лови́в жаб.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_021",
                text = "Ля́ля та Льо́ля лимо́ни лиза́ли, лиза́ли лимо́ни Ля́ля та Льо́ля.",
                difficulty = 3,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_022",
                text = "Мело́дія льодяна́ лила́ся поля́ми, поля́ мело́дією льодяно́ю напо́внились.",
                difficulty = 4,
                targetSounds = listOf("Л", "М"),
                category = "Л"
            )
        )
    }

    // ========== Свистячі (С, З, Ц) — 20 скоромовок ==========
    private fun getWhistling(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_w_001",
                text = "Са́ло ста́ло несоло́не, тро́хи тре́ба присоли́ти.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_002",
                text = "Сім сини́ць сиді́ли на сосні́.",
                difficulty = 1,
                targetSounds = listOf("С"),
                category = "С-З-Ц",
                usedInLesson = "Чітке мовлення, Урок 4"
            ),
            TongueTwister(
                id = "tt_w_003",
                text = "Везли́ на возі́ сі́но, сі́но з'ї́ла коза́.",
                difficulty = 2,
                targetSounds = listOf("С", "З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_004",
                text = "Цап ці́пав капу́сту в горо́ді, а коза́ ціпа́ла цибу́лю.",
                difficulty = 2,
                targetSounds = listOf("Ц", "К"),
                category = "С-З-Ц",
                usedInLesson = "Чітке мовлення, Урок 4"
            ),
            TongueTwister(
                id = "tt_w_005",
                text = "Ї́де Са́ва на коні́, за Са́вою сім сане́й.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_006",
                text = "Цу́цик сиді́в на ці́пку, а ці́пок на цу́цика цика́в.",
                difficulty = 3,
                targetSounds = listOf("Ц", "С"),
                category = "С-З-Ц",
                usedInLesson = "Чітке мовлення, Урок 4"
            ),
            TongueTwister(
                id = "tt_w_007",
                text = "Коси́, коса́, по́ки роса́, роса́ додо́лу – коса́ додо́му.",
                difficulty = 2,
                targetSounds = listOf("С", "К"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_008",
                text = "Засі́яв Са́ва про́со, про́со зійшло́ пога́но.",
                difficulty = 2,
                targetSounds = listOf("С", "З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_009",
                text = "О́сип оси́п, а Архи́п охри́п.",
                difficulty = 1,
                targetSounds = listOf("С", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_010",
                text = "Зозу́ля зозуленя́ навча́ла букваря́.",
                difficulty = 2,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_011",
                text = "Зозули́ні зо́йки зазва́ли за́йчиків із зозу́лею засну́ти.",
                difficulty = 4,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_012",
                text = "Цвіте́ цвіт, цвіте́ цвіт, хто цей цвіт зірве́?",
                difficulty = 2,
                targetSounds = listOf("Ц"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_013",
                text = "Співа́є співа́к, співа́є співа́чка, співа́є співа́к зі співа́чкою.",
                difficulty = 3,
                targetSounds = listOf("С", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_014",
                text = "Слон несе́ слонові́ слони́ху у слоня́чий сад.",
                difficulty = 3,
                targetSounds = listOf("С", "Л"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_015",
                text = "У Са́ви нове́ньке си́то, си́то у Са́ви нове́ньке.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_016",
                text = "Записува́в За́хар за́пис про За́хара в за́писнику.",
                difficulty = 3,
                targetSounds = listOf("З", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_017",
                text = "Со́нце сві́тить, со́нце ся́є, со́нце сосну́ освітля́є.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_018",
                text = "Сім косарі́в накоси́ли сім копи́ць сі́на.",
                difficulty = 3,
                targetSounds = listOf("С", "К"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_019",
                text = "Зозу́ля зозуленя́ в гнізді́ зігріва́є.",
                difficulty = 2,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_020",
                text = "Цига́н цига́рки не кури́в, цига́рки цига́ну цига́н не дари́в.",
                difficulty = 4,
                targetSounds = listOf("Ц"),
                category = "С-З-Ц"
            )
        )
    }

    // ========== Шиплячі (Ш, Ж, Ч, Щ) — 20 скоромовок ==========
    private fun getSibilant(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_sh_001",
                text = "Шу́ра шука́в ша́пку, а ша́пка у Шу́рика.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ",
                usedInLesson = "Чітке мовлення, Урок 5"
            ),
            TongueTwister(
                id = "tt_sh_002",
                text = "Ші́сть ми́шок ши́шки шука́ли в мішка́х.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_003",
                text = "Жук жужели́ці жужча́в: ж-ж-ж-ж.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ",
                usedInLesson = "Чітке мовлення, Урок 5"
            ),
            TongueTwister(
                id = "tt_sh_004",
                text = "Чоти́ри чо́рних чума́чки чобо́ти чи́стили щі́ткою.",
                difficulty = 4,
                targetSounds = listOf("Ч", "Щ"),
                category = "Ш-Ж-Ч-Щ",
                usedInLesson = "Чітке мовлення, Урок 5"
            ),
            TongueTwister(
                id = "tt_sh_005",
                text = "Жа́ба жабеня́ті жужча́ла: жу-жу-жу.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_006",
                text = "Ми́ша мишеня́ті ши́ла штанці́.",
                difficulty = 2,
                targetSounds = listOf("Ш", "М"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_007",
                text = "Ча́йка ка́чці каченя́ купи́ла, ча́йчиха ча́йченят ча́єм частува́ла.",
                difficulty = 4,
                targetSounds = listOf("Ч", "К"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_008",
                text = "Шість мише́й у ша́фі шелесті́ли.",
                difficulty = 2,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_009",
                text = "Щу́ка щуреня́т навча́ла ще́дрості.",
                difficulty = 3,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ",
                usedInLesson = "Чітке мовлення, Урок 5"
            ),
            TongueTwister(
                id = "tt_sh_010",
                text = "Жайворо́нок жив у жи́ті, жужели́ця жабі́ щебета́ла.",
                difficulty = 3,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_011",
                text = "Ша́шки на столі́, ша́хи по́руч.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_012",
                text = "Чо́рна га́лка чо́рній га́лці чорну́щі боби́ принесла́.",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_013",
                text = "Хи́жа жа́ба жа́бам жайворо́нків жа́рила.",
                difficulty = 4,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_014",
                text = "Чіпля́ла ча́йка чепчи́к, чепчи́к чіпля́ла ча́йка.",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_015",
                text = "Щи́ро щебета́ла щиголи́ха щигло́ві про ща́стя.",
                difficulty = 4,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_016",
                text = "Шуми́ть, шелести́ть широ́кий ліс.",
                difficulty = 2,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_017",
                text = "Жорсто́кий жук жа́лить жа́бу.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_018",
                text = "Чи чу́ли ви, як чіпля́ли чобітки́ чо́рти?",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_019",
                text = "У на́шій пу́щі щу́ки та лящі́.",
                difficulty = 2,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_020",
                text = "Ши́шки на ши́шках, ши́шки під ши́шками.",
                difficulty = 2,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            )
        )
    }

    // ========== Комбіновані (20 скоромовок) ==========
    private fun getCombined(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_c_001",
                text = "Був собі́ цап півторао́кий, півтораро́гий та півторахво́стий.",
                difficulty = 4,
                targetSounds = listOf("П", "Т", "Р", "Ц"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 7"
            ),
            TongueTwister(
                id = "tt_c_002",
                text = "Бик тупогу́б, у бика́ губа́ тупа́.",
                difficulty = 2,
                targetSounds = listOf("Б", "Т", "П"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 1"
            ),
            TongueTwister(
                id = "tt_c_003",
                text = "Шпак шпа́чці і шпаченя́ті шпакі́вню змайструва́в.",
                difficulty = 4,
                targetSounds = listOf("Ш", "П", "К"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 7"
            ),
            TongueTwister(
                id = "tt_c_004",
                text = "Попі́д пі́ччю пе́чуть пе́чиво, попі́д пі́ччю пе́че піша́к.",
                difficulty = 4,
                targetSounds = listOf("П", "Ч"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 7"
            ),
            TongueTwister(
                id = "tt_c_005",
                text = "Товкла́ Фе́кла товкаче́м гарбуза́, товкла́, товкла́, та не ви́товкла.",
                difficulty = 3,
                targetSounds = listOf("Т", "К", "Ф"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 1"
            ),
            TongueTwister(
                id = "tt_c_006",
                text = "Константи́н констатува́в конституці́йні конфлі́кти.",
                difficulty = 5,
                targetSounds = listOf("К", "Н", "С", "Т"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 7"
            ),
            TongueTwister(
                id = "tt_c_007",
                text = "Шиє швець ша́пку шевцеві́, ше́вець шиє швецеві́ чобо́ти.",
                difficulty = 4,
                targetSounds = listOf("Ш", "В", "Ц"),
                category = "Комбіновані",
                usedInLesson = "Чітке мовлення, Урок 7"
            ),
            TongueTwister(
                id = "tt_c_008",
                text = "Купи́ кіп кіп, купи́ ку́пу пу́ху.",
                difficulty = 3,
                targetSounds = listOf("К", "П"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_009",
                text = "Дід Дани́ло ді́лив ди́ню: до́льку Ді́ні, до́льку Да́ні.",
                difficulty = 3,
                targetSounds = listOf("Д"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_010",
                text = "Коро́ль Кири́ло купи́в крокоди́лові кросі́вки для кроке́та.",
                difficulty = 4,
                targetSounds = listOf("К", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_011",
                text = "Баране́ць барана́ баранце́м обари́в.",
                difficulty = 3,
                targetSounds = listOf("Б", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_012",
                text = "Добриво́да до́брий доро́гою дібро́вою добира́вся додо́му.",
                difficulty = 4,
                targetSounds = listOf("Д", "Б", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_013",
                text = "Розчаро́ваний часни́к час од ча́су ча́хне.",
                difficulty = 4,
                targetSounds = listOf("Ч", "Р", "З"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_014",
                text = "Проміня́ла Прокопе́нка Проко́пові при Проко́повому поро́зі.",
                difficulty = 5,
                targetSounds = listOf("П", "Р", "К"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_015",
                text = "Всіх скоромо́вок не перегово́риш, не перевискоромо́виш.",
                difficulty = 5,
                targetSounds = listOf("Р", "В", "П", "С"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_016",
                text = "Во́зи вівса́ перепра́вляли га́тком, а віз вівса́ впав у поті́к.",
                difficulty = 3,
                targetSounds = listOf("В", "П", "Т"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_017",
                text = "Бі́ля Мико́лки кі́лька кві́ток, кві́ти зів'я́ли, Мико́лка запла́кав.",
                difficulty = 3,
                targetSounds = listOf("К", "В", "Л"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_018",
                text = "Петро́ Петро́вич Петре́нко прині́с Петро́ві Петро́вичу Перепели́ці перепі́лку.",
                difficulty = 5,
                targetSounds = listOf("П", "Р", "Т"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_019",
                text = "Пішо́в Прокі́п кро́пу купи́ть, прийшо́в Прокі́п — кро́пу скипі́в.",
                difficulty = 4,
                targetSounds = listOf("П", "К", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_020",
                text = "Коро́ль Оре́л — оре́л, а оре́л — Коро́ль.",
                difficulty = 3,
                targetSounds = listOf("Р", "Л", "К"),
                category = "Комбіновані"
            )
        )
    }
}
