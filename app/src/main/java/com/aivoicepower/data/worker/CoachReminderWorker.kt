package com.aivoicepower.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import com.aivoicepower.data.local.datastore.UserPreferences
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class CoachReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val userProgressDao: UserProgressDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()

            if (!prefs.isReminderEnabled) return Result.success()

            val progress = userProgressDao.getUserProgressOnce()
            val message = pickMessage(prefs, progress) ?: return Result.success()

            NotificationHelper.showCoachNotification(applicationContext, message.first, message.second)

            Result.success()
        } catch (e: Exception) {
            Result.success() // Don't retry notifications
        }
    }

    /** Returns null when the user already trained today — no need to nag. */
    private fun pickMessage(
        prefs: UserPreferences,
        progress: UserProgressEntity?
    ): Pair<String, String>? {
        val name = prefs.userName?.trim().orEmpty()

        // "Ім'я, текст" або "Текст" — тексти пишемо з малої літери
        fun p(text: String): String =
            if (name.isNotEmpty()) "$name, $text" else text.replaceFirstChar { it.uppercase() }

        // Новий користувач — ще жодної вправи
        if (progress == null || progress.totalExercises == 0) {
            return listOf(
                "Твоя перша вправа чекає 🎤" to p("запиши 30 секунд — і AI покаже, як звучить твій голос збоку."),
                "5 хвилин до кращої дикції" to p("почни з простої скоромовки. AI-тренер одразу дасть оцінку й поради."),
                "Цікаво, як ти звучиш?" to p("зроби перший запис — отримаєш розбір темпу, дикції та інтонації."),
                "Почнімо з легкого" to p("перший день курсу займає кілька хвилин. Спробуй прямо зараз!")
            ).random()
        }

        val daysAway = daysSince(progress.lastActivityDate)
        if (daysAway == 0) return null // вже тренувався сьогодні

        val streak = progress.currentStreak
        val total = progress.totalExercises

        val pool = mutableListOf<Pair<String, String>>()

        when {
            // Був учора — серія під загрозою
            daysAway == 1 && streak >= 2 -> pool += listOf(
                "🔥 Серія: $streak ${dayWord(streak)}" to p("не дай їй згаснути — одна вправа сьогодні, і вогник горить далі."),
                "Не зупиняйся на $streak" to p("ти тренуєшся вже $streak ${dayWord(streak)} поспіль. Сьогодні — ще один крок."),
                "Твоя серія чекає" to p("5 хвилин сьогодні збережуть $streak ${dayWord(streak)} прогресу. Варто!")
            )
            daysAway == 1 -> pool += listOf(
                "Вчора було добре 👏" to p("сьогодні закріпимо результат. Одна вправа — і почнеться серія!"),
                "Другий день — найважливіший" to p("саме він перетворює спробу на звичку. Заходь на 5 хвилин."),
                "Продовжимо?" to p("голос тренується як м'яз — регулярність важливіша за тривалість.")
            )
            // 2–3 дні пропуску
            daysAway in 2..3 -> pool += listOf(
                "Скучили за тобою 🙂" to p("кілька днів перерви — дрібниця. Повернись з легкої розминки."),
                "Голос любить регулярність" to p("коротка вправа сьогодні — і ти знову в ритмі."),
                "Розім'ємо голос?" to p("3 хвилини артикуляційної розминки — і день почнеться впевненіше.")
            )
            // 4–7 днів
            daysAway in 4..7 -> pool += listOf(
                "Повертайся, ми чекаємо 👋" to p("прогрес збережено: $total ${exerciseWord(total)} і всі рівні навичок. Продовжимо?"),
                "Тиждень без тренувань" to p("навички ще свіжі — найкращий час повернутися. Почни з однієї вправи."),
                "Новий старт — сьогодні" to p("без тиску: одна скоромовка, і ти знову в грі.")
            )
            // Понад тиждень
            else -> pool += listOf(
                "Давно не чули твій голос 🎙" to p("заходь на 5 хвилин — подивимось, як ти звучиш зараз."),
                "Почати знову ніколи не пізно" to p("перерва — це нормально. Головне — повернутися. Давай сьогодні?"),
                "Перевір свій рівень" to p("запиши коротку промову — AI покаже, що змінилось з минулого разу.")
            )
        }

        // Досягнення
        if (total >= 10) {
            pool += "Вже $total ${exerciseWord(total)}! 💪" to
                p("кожна наближає тебе до впевненого мовлення. Додамо ще одну?")
        }
        if (progress.longestStreak >= 5 && streak < progress.longestStreak && daysAway > 1) {
            pool += "Твій рекорд — ${progress.longestStreak} ${dayWord(progress.longestStreak)}" to
                p("ти вже знаєш, що можеш. Час побити власний рекорд!")
        }

        // Загальні — фічі застосунку
        pool += listOf(
            "Скоромовка дня 👅" to p("«Їхав Гриць через криницю» — скажеш тричі швидко й чисто?"),
            "Тренуй співбесіду з AI" to p("в розділі Impro чекає віртуальний інтерв'юер. Перевір себе!"),
            "AI-коуч на зв'язку" to p("постав запитання про голос чи виступ — отримаєш пораду за секунди."),
            "Менше «еее» і «ну»" to p("вправа «Без запинок» допоможе говорити впевнено. Спробуй сьогодні!")
        )

        return pool.random()
    }

    private fun daysSince(timestamp: Long?): Int {
        if (timestamp == null) return Int.MAX_VALUE
        val then = startOfDay(timestamp)
        val today = startOfDay(System.currentTimeMillis())
        return TimeUnit.MILLISECONDS.toDays(today - then).toInt().coerceAtLeast(0)
    }

    private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 12) // полудень — щоб перехід на літній час не зсував день
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun dayWord(n: Int) = plural(n, "день", "дні", "днів")
    private fun exerciseWord(n: Int) = plural(n, "вправа", "вправи", "вправ")

    private fun plural(n: Int, one: String, few: String, many: String): String {
        val mod100 = n % 100
        val mod10 = n % 10
        return when {
            mod100 in 11..14 -> many
            mod10 == 1 -> one
            mod10 in 2..4 -> few
            else -> many
        }
    }
}
