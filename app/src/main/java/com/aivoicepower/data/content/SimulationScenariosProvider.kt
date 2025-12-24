package com.aivoicepower.data.content

import com.aivoicepower.ui.screens.aicoach.SimulationScenario
import com.aivoicepower.ui.screens.aicoach.SimulationStep

object SimulationScenariosProvider {

    fun getAllScenarios(): List<SimulationScenario> {
        return listOf(
            getJobInterviewScenario(),
            getSalesPitchScenario(),
            getPresentationScenario(),
            getNegotiationScenario()
        )
    }

    private fun getJobInterviewScenario() = SimulationScenario(
        id = "job_interview",
        title = "Співбесіда",
        description = "Практика відповідей на типові питання HR",
        steps = listOf(
            SimulationStep(
                stepNumber = 1,
                aiPrompt = "Розкажіть про себе",
                userHint = "2-3 хвилини про досвід та навички"
            ),
            SimulationStep(
                stepNumber = 2,
                aiPrompt = "Чому ви хочете працювати у нашій компанії?",
                userHint = "Покажи знання про компанію"
            ),
            SimulationStep(
                stepNumber = 3,
                aiPrompt = "Яка ваша найбільша слабкість?",
                userHint = "Будь чесним, але покажи як працюєш над цим"
            ),
            SimulationStep(
                stepNumber = 4,
                aiPrompt = "Опишіть складну ситуацію, яку ви подолали",
                userHint = "Використовуй STAR метод"
            ),
            SimulationStep(
                stepNumber = 5,
                aiPrompt = "Чи є у вас питання до нас?",
                userHint = "Покажи зацікавленість"
            )
        )
    )

    private fun getSalesPitchScenario() = SimulationScenario(
        id = "sales_pitch",
        title = "Продаж товару",
        description = "Практика презентації та роботи із запереченнями",
        steps = listOf(
            SimulationStep(
                stepNumber = 1,
                aiPrompt = "Презентуйте ваш продукт",
                userHint = "Почни з проблеми клієнта"
            ),
            SimulationStep(
                stepNumber = 2,
                aiPrompt = "Це занадто дорого для мене",
                userHint = "Покажи цінність, не знижуй ціну"
            ),
            SimulationStep(
                stepNumber = 3,
                aiPrompt = "Чому я маю купити саме у вас?",
                userHint = "Унікальна пропозиція"
            ),
            SimulationStep(
                stepNumber = 4,
                aiPrompt = "Мені треба подумати",
                userHint = "Створи терміновість"
            )
        )
    )

    private fun getPresentationScenario() = SimulationScenario(
        id = "presentation",
        title = "Презентація",
        description = "Структура виступу та робота з питаннями",
        steps = listOf(
            SimulationStep(
                stepNumber = 1,
                aiPrompt = "Почніть презентацію",
                userHint = "Сильний початок — hook"
            ),
            SimulationStep(
                stepNumber = 2,
                aiPrompt = "Розкрийте основні пункти",
                userHint = "3 ключові меседжі"
            ),
            SimulationStep(
                stepNumber = 3,
                aiPrompt = "У мене є питання: як це працює на практиці?",
                userHint = "Конкретні приклади"
            ),
            SimulationStep(
                stepNumber = 4,
                aiPrompt = "Підсумуйте презентацію",
                userHint = "Call to action"
            )
        )
    )

    private fun getNegotiationScenario() = SimulationScenario(
        id = "negotiation",
        title = "Переговори",
        description = "Практика аргументації та компромісів",
        steps = listOf(
            SimulationStep(
                stepNumber = 1,
                aiPrompt = "Озвучте вашу початкову позицію",
                userHint = "Висока anchor point"
            ),
            SimulationStep(
                stepNumber = 2,
                aiPrompt = "Ваші умови нереалістичні",
                userHint = "Обґрунтуй, запитай їх позицію"
            ),
            SimulationStep(
                stepNumber = 3,
                aiPrompt = "Ми можемо зустрітися посередині",
                userHint = "Win-win пропозиція"
            ),
            SimulationStep(
                stepNumber = 4,
                aiPrompt = "Домовимося на фінальних умовах",
                userHint = "Чітке підтвердження"
            )
        )
    )
}
