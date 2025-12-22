package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.TongueTwister

/**
 * Provider for tongue twisters (скоромовки)
 * 100+ Ukrainian tongue twisters categorized by target sounds
 */
object TongueTwistersProvider {

    fun getAllTongueTwisters(): List<TongueTwister> {
        return getSoundR() + getSoundL() + getWhistling() + getSibilant() + getCombined()
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

    // ========== Звук "Р" (22 скоромовки) ==========
    private fun getSoundR(): List<TongueTwister> {
        return listOf(
            TongueTwister(
                id = "tt_r_001",
                text = "На дворі трава, на траві дрова. Не руби дрова на траві двора.",
                difficulty = 2,
                targetSounds = listOf("Р", "Д", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_002",
                text = "Карл у Клари вкрав коралі, а Клара у Карла вкрала кларнет.",
                difficulty = 2,
                targetSounds = listOf("Р", "Л", "К"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_003",
                text = "Тридцять три кораблі лавірували, лавірували, та не вилавірували.",
                difficulty = 3,
                targetSounds = listOf("Р", "Л", "В"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_004",
                text = "Король Орел замовив торт із горіхів і родзинок.",
                difficulty = 2,
                targetSounds = listOf("Р", "О", "К"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_005",
                text = "Перепілка перепеленя перев'язала перев'яззю.",
                difficulty = 4,
                targetSounds = listOf("Р", "П"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_006",
                text = "Протокол про протокол протоколом записали.",
                difficulty = 3,
                targetSounds = listOf("Р", "П", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_007",
                text = "Розпорядник розпорядився розпорядження розпорядити.",
                difficulty = 5,
                targetSounds = listOf("Р", "З", "П"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_008",
                text = "Ріпа редька, редька ріпа, ріпа та редька – добра їжа.",
                difficulty = 2,
                targetSounds = listOf("Р"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_009",
                text = "Роман Романович романи писав про Романа Романовича.",
                difficulty = 3,
                targetSounds = listOf("Р", "М"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_010",
                text = "Тарас терасу терпляче тер, тер, та вітер терти перестав.",
                difficulty = 4,
                targetSounds = listOf("Р", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_011",
                text = "Три трубарі трублять у труби.",
                difficulty = 2,
                targetSounds = listOf("Р", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_012",
                text = "У перепела та перепілки п'ять перепелят.",
                difficulty = 3,
                targetSounds = listOf("Р", "П"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_013",
                text = "Прокіп Прокопович про Прокопа Прокоповича говорив.",
                difficulty = 4,
                targetSounds = listOf("Р", "П", "К"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_014",
                text = "Грицю, грицю до роботи, грицю, грицю до дороги.",
                difficulty = 2,
                targetSounds = listOf("Р", "Г", "Ц"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_015",
                text = "Архип охрип, а Архип не відстане, поки Архип не перестане хрипіти.",
                difficulty = 4,
                targetSounds = listOf("Р", "Х"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_016",
                text = "Краб крабу зробив грабі, подарував грабі крабу: грабай краб грабами гравій.",
                difficulty = 5,
                targetSounds = listOf("Р", "К", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_017",
                text = "Рано-рано два барани барабанили в барабани.",
                difficulty = 2,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_018",
                text = "Бобер біля берега робив берег борознами.",
                difficulty = 3,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_019",
                text = "Проворонила ворона вороненя.",
                difficulty = 1,
                targetSounds = listOf("Р", "В"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_020",
                text = "Риє кріт норі, риє кріт трав'яні доріжки.",
                difficulty = 3,
                targetSounds = listOf("Р", "К", "Т"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_021",
                text = "Бракороби робили брак, та вибракували бракоробів.",
                difficulty = 4,
                targetSounds = listOf("Р", "Б"),
                category = "Р"
            ),
            TongueTwister(
                id = "tt_r_022",
                text = "Брат Кирила брату Григорію подарував гриб.",
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
                text = "Ліла мила ляльку милом, лялька люба Лілі була.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_002",
                text = "Літали лелеки біля великої ріки.",
                difficulty = 1,
                targetSounds = listOf("Л", "Р"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_003",
                text = "Павло плавав на плоту по Дніпру від села.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_004",
                text = "Клала Клава лавку на лавку, на лавку клала Клава клавіші.",
                difficulty = 3,
                targetSounds = listOf("Л", "К"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_005",
                text = "Не любила Людка льодяників, любила Людка лише льодяники з лимоном.",
                difficulty = 3,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_006",
                text = "Лопотів лопотун, лопотів, та вилопотав.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_007",
                text = "Лола ліпить їжачка з пластиліну на столі.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_008",
                text = "Коля колов коло колоди, коли коло Колі ковзало.",
                difficulty = 4,
                targetSounds = listOf("Л", "К"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_009",
                text = "Летіли лелеки з ліликами, і білі лебеді плавали біля лепехи.",
                difficulty = 4,
                targetSounds = listOf("Л", "Б"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_010",
                text = "Павло палив поленця, поленця палили Павла.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_011",
                text = "Олена колола солому, солома Олену колола.",
                difficulty = 3,
                targetSounds = listOf("Л", "О"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_012",
                text = "Лопотіла лопотуха, лопотіла, аж лопнула.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_013",
                text = "Біля тополь лежали тополині кільки.",
                difficulty = 2,
                targetSounds = listOf("Л", "Т"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_014",
                text = "Плакала лялька на лавці, лежала лялька на полиці.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_015",
                text = "Летіла пілка через площу, ліпила пильно лише плющ.",
                difficulty = 3,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_016",
                text = "Їла Юля яблука в полі, любила Юля лише яблука з поля.",
                difficulty = 2,
                targetSounds = listOf("Л", "Я"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_017",
                text = "Летіла лялька коло вулика, ловила лялька вулик злякано.",
                difficulty = 3,
                targetSounds = listOf("Л", "В"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_018",
                text = "Лис лисичці ліс показав, лисиця лису лісові стежки показала.",
                difficulty = 4,
                targetSounds = listOf("Л", "С"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_019",
                text = "Пилип по липу пилкою пиляв.",
                difficulty = 2,
                targetSounds = listOf("Л", "П"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_020",
                text = "Лелека летів на луг, на лузі ловив жаб.",
                difficulty = 2,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_021",
                text = "Ляля та Льоля лимони лизали, лимони Лялі та Льолі лизали.",
                difficulty = 3,
                targetSounds = listOf("Л"),
                category = "Л"
            ),
            TongueTwister(
                id = "tt_l_022",
                text = "Мелодія льодяна лилася полями, поля мелодією льодяною наповнились.",
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
                text = "Сало сала не солоне, трохи треба присолити.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_002",
                text = "Сім синиць сиділи на сосні.",
                difficulty = 1,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_003",
                text = "Везли на возі сіно, сіно з'їла коза.",
                difficulty = 2,
                targetSounds = listOf("С", "З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_004",
                text = "Цап ціпав капусту в городі, а коза ціпала цибулю.",
                difficulty = 2,
                targetSounds = listOf("Ц", "К"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_005",
                text = "Їде Сава на коні, за Савою сім саней.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_006",
                text = "Цуцик сидів на ціпку, а ціпок на цуцика цикав.",
                difficulty = 3,
                targetSounds = listOf("Ц", "С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_007",
                text = "Коси, коса, поки роса, роса додолу – коса додому.",
                difficulty = 2,
                targetSounds = listOf("С", "К"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_008",
                text = "Засіяв Сава просо, просо зійшло погано.",
                difficulty = 2,
                targetSounds = listOf("С", "З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_009",
                text = "Осип осип, а Архип охрип.",
                difficulty = 1,
                targetSounds = listOf("С", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_010",
                text = "Зозуля зозуленя навчала букваря.",
                difficulty = 2,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_011",
                text = "Зозулині зойки зазвали зайчиків із зозулею заснути.",
                difficulty = 4,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_012",
                text = "Цвіте цвіт, цвіте цвіт, хто цей цвіт зірве?",
                difficulty = 2,
                targetSounds = listOf("Ц"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_013",
                text = "Співає співак, співає співачка, співає співак зі співачкою.",
                difficulty = 3,
                targetSounds = listOf("С", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_014",
                text = "Слон несе слонові слониху у слонячий сад.",
                difficulty = 3,
                targetSounds = listOf("С", "Л"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_015",
                text = "У Сави новеньке сито, сито у Сави новеньке.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_016",
                text = "Записував Захар запис про Захара в записнику.",
                difficulty = 3,
                targetSounds = listOf("З", "П"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_017",
                text = "Сонце світить, сонце сяє, сонце сосну освітляє.",
                difficulty = 2,
                targetSounds = listOf("С"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_018",
                text = "Сім косарів накосили сім копиць сіна.",
                difficulty = 3,
                targetSounds = listOf("С", "К"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_019",
                text = "Зозуля зузуленя в гнізді зігріває.",
                difficulty = 2,
                targetSounds = listOf("З"),
                category = "С-З-Ц"
            ),
            TongueTwister(
                id = "tt_w_020",
                text = "Циган цигарки не курив, цигарки цигану циган не дарив.",
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
                text = "Шапка та шубка, ось вам і вся Яшка.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_002",
                text = "Шила в мішку не сховаєш.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_003",
                text = "Жук жужелиці жужжав: ж-ж-ж-ж.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_004",
                text = "Чотири чорних чумачки чоботи чистили щіткою.",
                difficulty = 4,
                targetSounds = listOf("Ч", "Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_005",
                text = "Жаба жабеняті жужжала: жу-жу-жу.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_006",
                text = "Миша мишеняті шила штанці.",
                difficulty = 2,
                targetSounds = listOf("Ш", "М"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_007",
                text = "Чайка качці каченя купила, чайчиха чайченят чаєм частувала.",
                difficulty = 4,
                targetSounds = listOf("Ч", "К"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_008",
                text = "Шість мишей у шафі шелестіли.",
                difficulty = 2,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_009",
                text = "Щука щуренят навчала щедрості.",
                difficulty = 3,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_010",
                text = "Жайворонок жив у житі, жужелиця жабі щебетала.",
                difficulty = 3,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_011",
                text = "Шашки на столі, шахи поруч.",
                difficulty = 1,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_012",
                text = "Чорна галка чорній галці чорнущі боби принесла.",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_013",
                text = "Хижа жаба жабам жайворонків жарила.",
                difficulty = 4,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_014",
                text = "Чіпляла чайка чепчик, чепчик чіпляла чайка.",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_015",
                text = "Щиро щебетала щиголиха щиглові про щастя.",
                difficulty = 4,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_016",
                text = "Шумить, шелестить широкий ліс.",
                difficulty = 2,
                targetSounds = listOf("Ш"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_017",
                text = "Жорстокий жук жалить жабу.",
                difficulty = 2,
                targetSounds = listOf("Ж"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_018",
                text = "Чи чули ви, як чіпляли чобітки чорти?",
                difficulty = 3,
                targetSounds = listOf("Ч"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_019",
                text = "У нашій пущі щуки та лящі.",
                difficulty = 2,
                targetSounds = listOf("Щ"),
                category = "Ш-Ж-Ч-Щ"
            ),
            TongueTwister(
                id = "tt_sh_020",
                text = "Шишки на шишках, шишки під шишками.",
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
                text = "Був собі цап півтораокий, півторарогий та півторахвостий.",
                difficulty = 4,
                targetSounds = listOf("П", "Т", "Р", "Ц"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_002",
                text = "Бик тупогуб, у бика губа тупа.",
                difficulty = 2,
                targetSounds = listOf("Б", "Т", "П"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_003",
                text = "Шпак шпачці і шпаченяті шпаківню змайстрував.",
                difficulty = 4,
                targetSounds = listOf("Ш", "П", "К"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_004",
                text = "Попід піччю печуть печиво, попід піччю пече пішак.",
                difficulty = 4,
                targetSounds = listOf("П", "Ч"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_005",
                text = "Товкла Фекла товкачем гарбуза, товкла, товкла, та не витовкла.",
                difficulty = 3,
                targetSounds = listOf("Т", "К", "Ф"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_006",
                text = "Константин констатував конституційні конфлікти.",
                difficulty = 5,
                targetSounds = listOf("К", "Н", "С", "Т"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_007",
                text = "Шиє швець шапку шевцеві, шевець шиє швецеві чоботи.",
                difficulty = 4,
                targetSounds = listOf("Ш", "В", "Ц"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_008",
                text = "Купи кіп кіп, купи купу пуху.",
                difficulty = 3,
                targetSounds = listOf("К", "П"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_009",
                text = "Дід Данило ділив диню: дольку Дині, дольку Дані.",
                difficulty = 3,
                targetSounds = listOf("Д"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_010",
                text = "Король Кирило купив крокодилові кросівки для крокета.",
                difficulty = 4,
                targetSounds = listOf("К", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_011",
                text = "Баранець барана баранцем обарив.",
                difficulty = 3,
                targetSounds = listOf("Б", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_012",
                text = "Добривода добрий дорогою дібровою добирався додому.",
                difficulty = 4,
                targetSounds = listOf("Д", "Б", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_013",
                text = "Розчарований часник час од часу чахне.",
                difficulty = 4,
                targetSounds = listOf("Ч", "Р", "З"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_014",
                text = "Променіла Прокопенка Прокопові при Прокоповому порозі.",
                difficulty = 5,
                targetSounds = listOf("П", "Р", "К"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_015",
                text = "Всіх скоромовок не переговориш, не перевискоромовиш.",
                difficulty = 5,
                targetSounds = listOf("Р", "В", "П", "С"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_016",
                text = "Вози вівса переправляли гатком, а віз вівса впав у потік.",
                difficulty = 3,
                targetSounds = listOf("В", "П", "Т"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_017",
                text = "Біля Миколки кілька квіток, квіти зів'яли, Миколка заплакав.",
                difficulty = 3,
                targetSounds = listOf("К", "В", "Л"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_018",
                text = "Петро Петрович Петренко приніс Петрові Петровичу Перепелиці перепілку.",
                difficulty = 5,
                targetSounds = listOf("П", "Р", "Т"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_019",
                text = "Пішов Прокіп кропу купить, прийшов Прокіп — кропу скипів.",
                difficulty = 4,
                targetSounds = listOf("П", "К", "Р"),
                category = "Комбіновані"
            ),
            TongueTwister(
                id = "tt_c_020",
                text = "Король Орел — орел, а орел — Король.",
                difficulty = 3,
                targetSounds = listOf("Р", "Л", "К"),
                category = "Комбіновані"
            )
        )
    }
}
