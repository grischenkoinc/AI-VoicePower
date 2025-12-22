package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.Product
import com.aivoicepower.domain.model.content.CustomerProfile
import com.aivoicepower.domain.model.content.ImprovisationTask

/**
 * Provider for sales practice products and customer profiles
 * 15+ products for practicing sales pitch skills
 */
object SalesProductsProvider {

    fun getAllProducts(): List<Product> {
        return getRealProducts() + getAbsurdProducts()
    }

    fun getRealProducts(): List<Product> {
        return listOf(
            Product(
                name = "Розумний годинник для спорту",
                description = "Водонепроникний годинник з GPS, пульсометром та трекером сну. Батарея тримає до 14 днів.",
                price = "4999 грн",
                benefits = listOf(
                    "Відстеження 50+ видів спорту",
                    "Моніторинг здоров'я 24/7",
                    "Підключення до смартфона",
                    "Водонепроникність до 50 метрів"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Онлайн-курс з публічних виступів",
                description = "21-денний курс для розвитку впевненості на сцені з практичними вправами та зворотним зв'язком від експертів.",
                price = "1999 грн",
                benefits = listOf(
                    "Щоденні відео-уроки",
                    "Практичні завдання",
                    "Персональний фідбек",
                    "Сертифікат про проходження"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Автоматична кавоварка преміум-класу",
                description = "Готує 15 видів кави одним натисканням. Вбудований млинок, система самоочищення.",
                price = "12999 грн",
                benefits = listOf(
                    "15 програм приготування",
                    "Свіжий помел перед кожною чашкою",
                    "Автоматичне очищення",
                    "Тихий режим роботи"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Річний абонемент у фітнес-клуб",
                description = "Безлімітний доступ до всіх зон клубу плюс персональні тренування з сертифікованим тренером.",
                price = "15000 грн/рік",
                benefits = listOf(
                    "Безлімітний доступ",
                    "4 персональні тренування на місяць",
                    "Спа-зона та басейн",
                    "Мобільний додаток для відстеження прогресу"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Електронна книга з підсвіткою",
                description = "Місткість до 8000 книг, водонепроникний корпус, екран без відблисків.",
                price = "4499 грн",
                benefits = listOf(
                    "Читання без навантаження на очі",
                    "Робота до 6 тижнів без підзарядки",
                    "Водонепроникний",
                    "Безкоштовний доступ до бібліотеки"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Підписка на стрімінговий сервіс",
                description = "Тисячі фільмів, серіалів та документалок без реклами. Перегляд на 4 пристроях одночасно.",
                price = "299 грн/місяць",
                benefits = listOf(
                    "Без реклами",
                    "4K якість",
                    "Офлайн перегляд",
                    "Ексклюзивний контент"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Розумна валіза з GPS",
                description = "Валіза з вбудованим GPS-трекером, USB-портом для зарядки та цифровими вагами.",
                price = "5999 грн",
                benefits = listOf(
                    "Ніколи не загубиться",
                    "Зарядка гаджетів у дорозі",
                    "Вбудовані ваги",
                    "Замок з TSA"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Набір для домашньої медитації",
                description = "Подушка для медитації, аромалампа, набір ефірних олій та річна підписка на медитації.",
                price = "2499 грн",
                benefits = listOf(
                    "Ергономічна подушка",
                    "5 ефірних олій",
                    "Річний доступ до медитацій",
                    "Інструкція для початківців"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Курс англійської мови онлайн",
                description = "Інтенсивний курс з носіями мови, персональний план навчання, сертифікат рівня.",
                price = "8999 грн",
                benefits = listOf(
                    "Заняття з носіями мови",
                    "Гнучкий графік",
                    "Персональний план",
                    "Міжнародний сертифікат"
                ),
                isAbsurd = false
            ),
            Product(
                name = "Робот-пилосос преміум",
                description = "Інтелектуальна навігація, автоматичне спорожнення контейнера, керування через додаток.",
                price = "18999 грн",
                benefits = listOf(
                    "Картографування приміщення",
                    "Самоочищення 60 днів",
                    "Тихий режим",
                    "Підтримка Alexa та Google Home"
                ),
                isAbsurd = false
            )
        )
    }

    fun getAbsurdProducts(): List<Product> {
        return listOf(
            Product(
                name = "Зубна щітка з AI-аналітикою",
                description = "Аналізує техніку чищення, будує 3D-карту зубів і надсилає звіти стоматологу.",
                price = "2999 грн",
                benefits = listOf(
                    "AI-аналіз кожного руху",
                    "3D-сканування порожнини рота",
                    "Автоматичні звіти лікарю",
                    "Рекомендації в реальному часі"
                ),
                isAbsurd = true
            ),
            Product(
                name = "Костюм часткової невидимості",
                description = "Використовує технологію світловідбиття. Робить вас на 37% менш помітним в офісі.",
                price = "15999 грн",
                benefits = listOf(
                    "37% менше уваги",
                    "Ідеально для інтровертів",
                    "Стильний дизайн",
                    "Можна прати в машинці"
                ),
                isAbsurd = true
            ),
            Product(
                name = "Розумні шкарпетки з підігрівом",
                description = "Самопідігріваються при температурі нижче 15°C. Синхронізуються з погодним додатком.",
                price = "1999 грн",
                benefits = listOf(
                    "Автоматичний підігрів",
                    "Прогноз погоди на шкарпетках",
                    "Bluetooth підключення",
                    "Нагадування надіти теплі шкарпетки"
                ),
                isAbsurd = true
            ),
            Product(
                name = "Подушка з функцією запису снів",
                description = "Записує ваші сни за допомогою нейроінтерфейсу та конвертує їх у текст.",
                price = "9999 грн",
                benefits = listOf(
                    "Запис снів у текст",
                    "Аналіз сновидінь",
                    "Створення фільмів зі снів",
                    "Будильник в улюбленому сні"
                ),
                isAbsurd = true
            ),
            Product(
                name = "Окуляри для бачення позитиву",
                description = "Фільтрують негативний контент у реальному світі. Сумні обличчя стають веселими.",
                price = "7999 грн",
                benefits = listOf(
                    "100% позитивна реальність",
                    "Фільтр негативних новин",
                    "Автоматичне покращення настрою",
                    "Режим рожевих окулярів"
                ),
                isAbsurd = true
            )
        )
    }

    fun getCustomerProfiles(): List<CustomerProfile> {
        return listOf(
            CustomerProfile(
                type = "Зайнятий бізнесмен",
                characteristics = listOf(
                    "Цінує час понад усе",
                    "Приймає рішення швидко",
                    "Шукає ефективність"
                ),
                objections = listOf(
                    "У мене немає часу на це",
                    "Це справді необхідно?",
                    "Яка економія часу?"
                ),
                interests = listOf(
                    "Продуктивність",
                    "Статус",
                    "Інновації"
                )
            ),
            CustomerProfile(
                type = "Обережний покупець",
                characteristics = listOf(
                    "Довго обдумує рішення",
                    "Читає всі відгуки",
                    "Боїться помилитися"
                ),
                objections = listOf(
                    "Мені потрібно подумати",
                    "А якщо мені не підійде?",
                    "Які гарантії?"
                ),
                interests = listOf(
                    "Безпека",
                    "Гарантії",
                    "Відгуки інших"
                )
            ),
            CustomerProfile(
                type = "Скептик",
                characteristics = listOf(
                    "Не довіряє рекламі",
                    "Шукає підвохи",
                    "Задає провокативні питання"
                ),
                objections = listOf(
                    "Це занадто добре, щоб бути правдою",
                    "Покажіть докази",
                    "Чому це коштує стільки?"
                ),
                interests = listOf(
                    "Факти",
                    "Статистика",
                    "Незалежні огляди"
                )
            ),
            CustomerProfile(
                type = "Емоційний покупець",
                characteristics = listOf(
                    "Приймає рішення серцем",
                    "Цінує історії та емоції",
                    "Легко захоплюється"
                ),
                objections = listOf(
                    "Чи дійсно мені це потрібно?",
                    "Що скажуть інші?",
                    "Це буде модно?"
                ),
                interests = listOf(
                    "Емоційний зв'язок",
                    "Бренд",
                    "Естетика"
                )
            ),
            CustomerProfile(
                type = "Шукач вигоди",
                characteristics = listOf(
                    "Завжди шукає знижки",
                    "Порівнює ціни",
                    "Торгується"
                ),
                objections = listOf(
                    "Це занадто дорого",
                    "У конкурентів дешевше",
                    "Яка знижка?"
                ),
                interests = listOf(
                    "Ціна",
                    "Акції",
                    "Бонуси"
                )
            ),
            CustomerProfile(
                type = "Технічний експерт",
                characteristics = listOf(
                    "Знає продукт краще за продавця",
                    "Ставить складні технічні питання",
                    "Цікавить деталі"
                ),
                objections = listOf(
                    "Які саме специфікації?",
                    "Яка версія процесора?",
                    "Порівняйте з моделлю X"
                ),
                interests = listOf(
                    "Технічні характеристики",
                    "Інновації",
                    "Продуктивність"
                )
            )
        )
    }

    fun getSalesPitchTasks(): List<ImprovisationTask.SalesPitch> {
        val products = getAllProducts()
        val customers = getCustomerProfiles()

        return products.flatMap { product ->
            customers.map { customer ->
                ImprovisationTask.SalesPitch(
                    product = product,
                    customer = customer,
                    pitchSeconds = if (product.isAbsurd) 90 else 60
                )
            }
        }.shuffled().take(30) // Обмежуємо кількість комбінацій
    }

    fun getRandomSalesPitchTask(): ImprovisationTask.SalesPitch {
        return ImprovisationTask.SalesPitch(
            product = getAllProducts().random(),
            customer = getCustomerProfiles().random(),
            pitchSeconds = 60
        )
    }
}
