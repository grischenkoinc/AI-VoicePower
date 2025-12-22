package com.aivoicepower.utils.notifications

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val DAILY_REMINDER_WORK = "daily_reminder_work"
        const val STREAK_REMINDER_WORK = "streak_reminder_work"
    }

    /**
     * Schedule daily reminder notification
     * @param hour Hour of day (0-23)
     * @param minute Minute (0-59)
     */
    fun scheduleDailyReminder(hour: Int = 9, minute: Int = 0) {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time is in the past, schedule for tomorrow
        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(DAILY_REMINDER_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    /**
     * Schedule streak reminder notification (evening reminder if no activity today)
     */
    fun scheduleStreakReminder() {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // 8 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time is in the past, schedule for tomorrow
        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(STREAK_REMINDER_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            STREAK_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    /**
     * Cancel daily reminder
     */
    fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK)
    }

    /**
     * Cancel streak reminder
     */
    fun cancelStreakReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(STREAK_REMINDER_WORK)
    }

    /**
     * Cancel all reminders
     */
    fun cancelAllReminders() {
        cancelDailyReminder()
        cancelStreakReminder()
    }

    /**
     * Update reminder time
     */
    fun updateReminderTime(timeString: String) {
        val parts = timeString.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toIntOrNull() ?: 9
            val minute = parts[1].toIntOrNull() ?: 0
            scheduleDailyReminder(hour, minute)
        }
    }
}
