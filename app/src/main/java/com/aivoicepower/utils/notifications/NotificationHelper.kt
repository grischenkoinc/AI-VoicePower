package com.aivoicepower.utils.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aivoicepower.MainActivity
import com.aivoicepower.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID_REMINDERS = "reminders"
        const val CHANNEL_ID_ACHIEVEMENTS = "achievements"
        const val CHANNEL_ID_STREAK = "streak"

        const val NOTIFICATION_ID_DAILY_REMINDER = 1001
        const val NOTIFICATION_ID_STREAK_REMINDER = 1002
        const val NOTIFICATION_ID_ACHIEVEMENT = 1003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            // Daily reminders channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Щоденні нагадування",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Нагадування про тренування голосу"
            }

            // Achievements channel
            val achievementChannel = NotificationChannel(
                CHANNEL_ID_ACHIEVEMENTS,
                "Досягнення",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сповіщення про нові досягнення"
            }

            // Streak channel
            val streakChannel = NotificationChannel(
                CHANNEL_ID_STREAK,
                "Серія тренувань",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Нагадування про підтримку серії"
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(achievementChannel)
            notificationManager.createNotificationChannel(streakChannel)
        }
    }

    /**
     * Show daily reminder notification
     */
    fun showDailyReminder() {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Час тренувати голос!")
            .setContentText("Кілька хвилин практики кожен день — і результат не змусить себе чекати")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY_REMINDER, notification)
    }

    /**
     * Show streak reminder notification
     */
    fun showStreakReminder(currentStreak: Int) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val message = if (currentStreak > 0) {
            "Ваша серія: $currentStreak днів. Не втрачайте прогрес!"
        } else {
            "Почніть нову серію тренувань сьогодні!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_STREAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Збережіть серію тренувань!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STREAK_REMINDER, notification)
    }

    /**
     * Show achievement unlocked notification
     */
    fun showAchievementUnlocked(title: String, description: String) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "achievements")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Нове досягнення: $title")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
    }

    /**
     * Check if notification permission is granted
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    /**
     * Cancel specific notification
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
