package com.aivoicepower.utils.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check if notifications are enabled
            val preferences = userPreferencesDataStore.userPreferencesFlow.first()

            if (preferences.notificationsEnabled) {
                // Check if user hasn't practiced today
                val today = java.text.SimpleDateFormat(
                    "yyyy-MM-dd",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())

                if (preferences.lastActiveDate != today && preferences.currentStreak > 0) {
                    // User has a streak but hasn't practiced today - send reminder
                    notificationHelper.showStreakReminder(preferences.currentStreak)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
