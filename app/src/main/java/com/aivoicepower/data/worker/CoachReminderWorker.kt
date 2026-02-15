package com.aivoicepower.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

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
            val (title, message) = pickMessage(prefs, progress)

            NotificationHelper.showCoachNotification(applicationContext, title, message)

            Result.success()
        } catch (e: Exception) {
            Result.success() // Don't retry notifications
        }
    }

    private fun pickMessage(
        prefs: com.aivoicepower.data.local.datastore.UserPreferences,
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?
    ): Pair<String, String> {
        val name = prefs.userName ?: ""
        val greeting = if (name.isNotEmpty()) "$name, " else ""

        // New user
        if (progress == null || progress.totalExercises == 0) {
            return "Час почати!" to "${greeting}зроби свою першу вправу сьогодні. Це займе лише 5 хвилин!"
        }

        val streak = progress.currentStreak
        val totalExercises = progress.totalExercises

        // Pool of messages based on state
        val messages = mutableListOf<Pair<String, String>>()

        // Streak-based
        if (streak == 0) {
            messages.add(
                "Повернись до тренувань!" to
                    "${greeting}навіть 5 хвилин на день зберігають прогрес. Ти це можеш!"
            )
            messages.add(
                "Не здавайся!" to
                    "${greeting}перерва — це нормально. Головне — повернутися. Давай сьогодні!"
            )
        } else if (streak >= 3) {
            messages.add(
                "Тримай темп!" to
                    "${greeting}вже $streak днів поспіль! Не зупиняйся — серія росте."
            )
        }

        // Progress-based
        if (totalExercises >= 10) {
            messages.add(
                "Ти молодець!" to
                    "${greeting}вже $totalExercises вправ виконано. Кожна наближає до мети!"
            )
        }

        // General motivation
        messages.addAll(
            listOf(
                "Доброго ранку!" to "${greeting}час для тренування голосу. Розминка займе 5 хвилин!",
                "Час тренуватись!" to "${greeting}твій голос стає кращим з кожним днем. Продовжуй!",
                "Нагадування" to "${greeting}регулярність — ключ до успіху. Зроби хоча б одну вправу!"
            )
        )

        return messages.random()
    }
}
