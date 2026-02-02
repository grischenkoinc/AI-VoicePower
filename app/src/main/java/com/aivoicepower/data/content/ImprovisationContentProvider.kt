package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.ImprovisationTask
import com.aivoicepower.domain.model.content.SimulationStep
import com.aivoicepower.domain.model.course.Difficulty
import com.aivoicepower.domain.model.exercise.ImprovisationExercise
import com.aivoicepower.domain.model.user.SkillType

/**
 * Provider for all improvisation exercises
 *
 * Converts simulations and improvisation tasks into ImprovisationExercise
 * objects that implement BaseExercise for unified analytics.
 */
object ImprovisationContentProvider {

    fun getAllExercises(): List<ImprovisationExercise> {
        return listOf(
            getJobInterviewExercise(),
            getPresentationExercise(),
            getNegotiationExercise(),
            // Add other improvisations as needed
        )
    }

    fun getExerciseById(id: String): ImprovisationExercise? {
        return getAllExercises().find { it.id == id }
    }

    // ===== JOB INTERVIEW =====

    fun getJobInterviewExercise(): ImprovisationExercise {
        return ImprovisationExercise(
            id = "improv_job_interview",
            title = "Співбесіда",
            description = "Практика відповідей на типові питання HR. 5 кроків для підготовки до реальної співбесіди.",
            durationSeconds = 600, // 10 minutes (5 steps * ~2 min each)
            targetMetrics = listOf(
                SkillType.STRUCTURE,      // Структурована відповідь (STAR)
                SkillType.CONFIDENCE,     // Впевненість у відповідях
                SkillType.INTONATION,     // Виразність
                SkillType.FILLER_WORDS    // Чисте мовлення без паразитів
            ),
            requiresRecording = true,
            task = ImprovisationTask.JobInterview(
                steps = listOf(
                    SimulationStep(
                        stepNumber = 1,
                        question = "Розкажіть про себе",
                        hint = "2-3 хвилини про досвід та навички"
                    ),
                    SimulationStep(
                        stepNumber = 2,
                        question = "Чому ви хочете працювати у нашій компанії?",
                        hint = "Покажи знання про компанію"
                    ),
                    SimulationStep(
                        stepNumber = 3,
                        question = "Яка ваша найбільша слабкість?",
                        hint = "Будь чесним, але покажи як працюєш над цим"
                    ),
                    SimulationStep(
                        stepNumber = 4,
                        question = "Опишіть складну ситуацію, яку ви подолали",
                        hint = "Використовуй STAR метод"
                    ),
                    SimulationStep(
                        stepNumber = 5,
                        question = "Чи є у вас питання до нас?",
                        hint = "Покажи зацікавленість"
                    )
                ),
                difficulty = Difficulty.INTERMEDIATE
            ),
            preparationSeconds = 30,
            allowRetry = true,
            difficulty = "intermediate"
        )
    }

    // ===== PRESENTATION =====

    fun getPresentationExercise(): ImprovisationExercise {
        return ImprovisationExercise(
            id = "improv_presentation",
            title = "Презентація",
            description = "Структурований виступ з Hook, ключовими меседжами та call to action. 4 кроки ефективної презентації.",
            durationSeconds = 480, // 8 minutes (4 steps * ~2 min each)
            targetMetrics = listOf(
                SkillType.STRUCTURE,      // Чітка структура презентації
                SkillType.CONFIDENCE,     // Впевненість у виступі
                SkillType.INTONATION,     // Виразність та драматизм
                SkillType.TEMPO           // Контроль темпу мовлення
            ),
            requiresRecording = true,
            task = ImprovisationTask.Presentation(
                steps = listOf(
                    SimulationStep(
                        stepNumber = 1,
                        question = "Почніть презентацію",
                        hint = "Сильний початок — hook"
                    ),
                    SimulationStep(
                        stepNumber = 2,
                        question = "Розкрийте основні пункти",
                        hint = "3 ключові меседжі"
                    ),
                    SimulationStep(
                        stepNumber = 3,
                        question = "У мене є питання: як це працює на практиці?",
                        hint = "Конкретні приклади"
                    ),
                    SimulationStep(
                        stepNumber = 4,
                        question = "Підсумуйте презентацію",
                        hint = "Call to action"
                    )
                ),
                difficulty = Difficulty.INTERMEDIATE
            ),
            preparationSeconds = 60,
            allowRetry = true,
            difficulty = "intermediate"
        )
    }

    // ===== NEGOTIATION =====

    fun getNegotiationExercise(): ImprovisationExercise {
        return ImprovisationExercise(
            id = "improv_negotiation",
            title = "Переговори",
            description = "Практика аргументації та пошуку компромісів. 4 кроки успішних переговорів.",
            durationSeconds = 480, // 8 minutes (4 steps * ~2 min each)
            targetMetrics = listOf(
                SkillType.STRUCTURE,      // Логічна аргументація
                SkillType.CONFIDENCE,     // Впевненість у позиції
                SkillType.INTONATION,     // Переконливість
                SkillType.FILLER_WORDS    // Чисте професійне мовлення
            ),
            requiresRecording = true,
            task = ImprovisationTask.Negotiation(
                steps = listOf(
                    SimulationStep(
                        stepNumber = 1,
                        question = "Озвучте вашу початкову позицію",
                        hint = "Висока anchor point"
                    ),
                    SimulationStep(
                        stepNumber = 2,
                        question = "Ваші умови нереалістичні",
                        hint = "Обґрунтуй, запитай їх позицію"
                    ),
                    SimulationStep(
                        stepNumber = 3,
                        question = "Ми можемо зустрітися посередині",
                        hint = "Win-win пропозиція"
                    ),
                    SimulationStep(
                        stepNumber = 4,
                        question = "Домовимося на фінальних умовах",
                        hint = "Чітке підтвердження"
                    )
                ),
                difficulty = Difficulty.ADVANCED
            ),
            preparationSeconds = 45,
            allowRetry = true,
            difficulty = "advanced"
        )
    }

    // ===== HELPER FUNCTIONS =====

    /**
     * Get all exercises that improve a specific skill
     */
    fun getExercisesBySkill(skillType: SkillType): List<ImprovisationExercise> {
        return getAllExercises().filter { exercise ->
            exercise.targetMetrics.contains(skillType)
        }
    }

    /**
     * Get exercises by difficulty
     */
    fun getExercisesByDifficulty(difficulty: String): List<ImprovisationExercise> {
        return getAllExercises().filter { it.difficulty == difficulty }
    }

    /**
     * Get total count of exercises
     */
    fun getExerciseCount(): Int {
        return getAllExercises().size
    }
}
