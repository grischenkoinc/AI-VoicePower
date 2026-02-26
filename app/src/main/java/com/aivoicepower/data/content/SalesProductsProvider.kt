package com.aivoicepower.data.content

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesProductsProvider @Inject constructor() {

    data class SalesProduct(
        val id: String,
        val name: String,
        val description: String,
        val price: String,
        val isAbsurd: Boolean = false
    )

    data class CustomerProfile(
        val type: String,
        val description: String,
        val typicalObjections: List<String>
    )

    private val realProducts = listOf(
        SalesProduct(
            id = "product_001",
            name = "Онлайн-курс з публічних виступів",
            description = "12-тижнева програма для розвитку навичок презентацій",
            price = "₴4,999"
        ),
        SalesProduct(
            id = "product_002",
            name = "Смарт-годинник для фітнесу",
            description = "Моніторинг здоров'я 24/7, GPS, водонепроникний",
            price = "₴8,999"
        ),
        SalesProduct(
            id = "product_003",
            name = "Підписка на онлайн-бібліотеку",
            description = "10,000+ книжок та аудіокниг українською та англійською",
            price = "₴199/міс"
        ),
        SalesProduct(
            id = "product_004",
            name = "Роботизований пилосос",
            description = "Автоматичне прибирання, картографування квартири",
            price = "₴12,999"
        )
    )

    private val absurdProducts = listOf(
        SalesProduct(
            id = "absurd_001",
            name = "Невидимий парасольку",
            description = "Захищає від дощу за допомогою силового поля",
            price = "₴99,999",
            isAbsurd = true
        ),
        SalesProduct(
            id = "absurd_002",
            name = "Машина часу (лише в минуле)",
            description = "Повернення на 24 години назад, одноразове використання",
            price = "₴50,000",
            isAbsurd = true
        ),
        SalesProduct(
            id = "absurd_003",
            name = "Чарівний олівець",
            description = "Все, що намалюєш, стає реальністю (макс. 10 см)",
            price = "₴1,000,000",
            isAbsurd = true
        )
    )

    private val customerTypes = listOf(
        CustomerProfile(
            type = "Зайнятий професіонал",
            description = "Цінує час, шукає ефективність. Не терпить довгих пояснень",
            typicalObjections = listOf("У мене немає часу", "Це дорого", "Чи дійсно це працює?")
        ),
        CustomerProfile(
            type = "Скептик",
            description = "Не довіряє новим продуктам, потребує конкретних доказів та цифр",
            typicalObjections = listOf("Я чув негативні відгуки", "Це схоже на обман", "Навіщо мені це?")
        ),
        CustomerProfile(
            type = "Обережний покупець",
            description = "Хоче все зважити, боїться помилитися з покупкою",
            typicalObjections = listOf("Може, я подумаю", "Що якщо це мені не підійде?", "Чи можна повернути?")
        ),
        CustomerProfile(
            type = "Ентузіаст",
            description = "Цікавиться новинками, але критично ставиться до деталей",
            typicalObjections = listOf("А що ще він вміє?", "Чи є аналоги?", "Які гарантії?")
        ),
        CustomerProfile(
            type = "Економний покупець",
            description = "Рахує кожну гривню, порівнює ціни, шукає знижки",
            typicalObjections = listOf("Занадто дорого", "У конкурентів дешевше", "Чи є знижка?")
        ),
        CustomerProfile(
            type = "Досвідчений експерт",
            description = "Добре розбирається у темі, задає технічні питання",
            typicalObjections = listOf("Які характеристики?", "А що з продуктивністю?", "Чим це краще за аналоги?")
        ),
        CustomerProfile(
            type = "Імпульсивний покупець",
            description = "Легко захоплюється, але швидко втрачає інтерес",
            typicalObjections = listOf("Цікаво, але не зараз", "А щось новіше є?", "Мені треба подумати до завтра")
        ),
        CustomerProfile(
            type = "Вимогливий клієнт",
            description = "Очікує преміум-сервіс, увагу до деталей та персональний підхід",
            typicalObjections = listOf("Я звик до кращого", "А що ви зробите саме для мене?", "Мені потрібні гарантії якості")
        )
    )

    fun getAllProducts(includeAbsurd: Boolean = true): List<SalesProduct> {
        return if (includeAbsurd) {
            realProducts + absurdProducts
        } else {
            realProducts
        }
    }

    fun getRandomProduct(includeAbsurd: Boolean = true): SalesProduct {
        return getAllProducts(includeAbsurd).random()
    }

    fun getRandomCustomer(): CustomerProfile = customerTypes.random()
}
