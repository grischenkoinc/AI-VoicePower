package com.aivoicepower.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Extension для створення DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Модель налаштувань користувача
 */
data class UserPreferences(
    // Онбординг та діагностика
    val hasCompletedOnboarding: Boolean = false,
    val hasCompletedDiagnostic: Boolean = false,
    val onboardingCompletedAt: Long? = null,

    // Ціль користувача
    val userGoal: String = "GENERAL", // UserGoal enum as string
    val dailyTrainingMinutes: Int = 15,

    // Преміум
    val isPremium: Boolean = false,
    val premiumExpiresAt: Long? = null,

    // Налаштування застосунку
    val isDarkTheme: Boolean = false,
    val useSystemTheme: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "09:00", // HH:mm format

    // Статистика сесії
    val lastActiveDate: String? = null, // "2024-12-15" format
    val currentStreak: Int = 0,
    val todayMinutes: Int = 0,
    val todayExercises: Int = 0,

    // UI стан
    val lastSelectedTab: Int = 0,
    val lastOpenedCourseId: String? = null,

    // Free tier limits
    val freeMessagesToday: Int = 0,
    val freeImprovisationsToday: Int = 0,
    val lastLimitResetDate: String? = null
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val context: Context
) {

    // ===== KEYS =====
    private object PreferencesKeys {
        // Онбординг
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val HAS_COMPLETED_DIAGNOSTIC = booleanPreferencesKey("has_completed_diagnostic")
        val ONBOARDING_COMPLETED_AT = longPreferencesKey("onboarding_completed_at")

        // User goal
        val USER_GOAL = stringPreferencesKey("user_goal")
        val DAILY_TRAINING_MINUTES = intPreferencesKey("daily_training_minutes")

        // Premium
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val PREMIUM_EXPIRES_AT = longPreferencesKey("premium_expires_at")

        // App settings
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")

        // Session stats
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val TODAY_MINUTES = intPreferencesKey("today_minutes")
        val TODAY_EXERCISES = intPreferencesKey("today_exercises")

        // UI state
        val LAST_SELECTED_TAB = intPreferencesKey("last_selected_tab")
        val LAST_OPENED_COURSE_ID = stringPreferencesKey("last_opened_course_id")

        // Free tier limits
        val FREE_MESSAGES_TODAY = intPreferencesKey("free_messages_today")
        val FREE_IMPROVISATIONS_TODAY = intPreferencesKey("free_improvisations_today")
        val LAST_LIMIT_RESET_DATE = stringPreferencesKey("last_limit_reset_date")
    }

    // ===== READ =====

    /**
     * Flow всіх налаштувань
     */
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapToUserPreferences(preferences)
        }

    /**
     * Швидка перевірка чи пройдено онбординг
     */
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false }

    /**
     * Швидка перевірка чи пройдено діагностику
     */
    val hasCompletedDiagnostic: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] ?: false }

    /**
     * Швидка перевірка преміум статусу
     */
    val isPremium: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.IS_PREMIUM] ?: false }

    /**
     * Поточний streak
     */
    val currentStreak: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.CURRENT_STREAK] ?: 0 }

    /**
     * Тема застосунку
     */
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            if (preferences[PreferencesKeys.USE_SYSTEM_THEME] == true) {
                // TODO: Повернути системну тему
                false
            } else {
                preferences[PreferencesKeys.IS_DARK_THEME] ?: false
            }
        }

    // ===== WRITE: Онбординг =====

    suspend fun setOnboardingCompleted(completed: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
            if (completed) {
                preferences[PreferencesKeys.ONBOARDING_COMPLETED_AT] = System.currentTimeMillis()
            }
        }
    }

    suspend fun setDiagnosticCompleted(completed: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] = completed
        }
    }

    // ===== WRITE: User Goal =====

    suspend fun setUserGoal(goal: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_GOAL] = goal
        }
    }

    suspend fun setDailyTrainingMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_TRAINING_MINUTES] = minutes
        }
    }

    // ===== WRITE: Premium =====

    suspend fun setPremiumStatus(isPremium: Boolean, expiresAt: Long? = null) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
            if (expiresAt != null) {
                preferences[PreferencesKeys.PREMIUM_EXPIRES_AT] = expiresAt
            }
        }
    }

    // ===== WRITE: App Settings =====

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
            preferences[PreferencesKeys.USE_SYSTEM_THEME] = false
        }
    }

    suspend fun setUseSystemTheme(useSystem: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_SYSTEM_THEME] = useSystem
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = time
        }
    }

    // ===== WRITE: Session Stats =====

    suspend fun updateSessionStats(date: String, minutes: Int, exercises: Int) {
        context.dataStore.edit { preferences ->
            val lastDate = preferences[PreferencesKeys.LAST_ACTIVE_DATE]

            if (lastDate == date) {
                // Той самий день — додаємо
                preferences[PreferencesKeys.TODAY_MINUTES] =
                    (preferences[PreferencesKeys.TODAY_MINUTES] ?: 0) + minutes
                preferences[PreferencesKeys.TODAY_EXERCISES] =
                    (preferences[PreferencesKeys.TODAY_EXERCISES] ?: 0) + exercises
            } else {
                // Новий день — скидаємо
                preferences[PreferencesKeys.LAST_ACTIVE_DATE] = date
                preferences[PreferencesKeys.TODAY_MINUTES] = minutes
                preferences[PreferencesKeys.TODAY_EXERCISES] = exercises
            }
        }
    }

    suspend fun updateStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] = streak
        }
    }

    // ===== WRITE: UI State =====

    suspend fun setLastSelectedTab(tabIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_TAB] = tabIndex
        }
    }

    suspend fun setLastOpenedCourse(courseId: String?) {
        context.dataStore.edit { preferences ->
            if (courseId != null) {
                preferences[PreferencesKeys.LAST_OPENED_COURSE_ID] = courseId
            } else {
                preferences.remove(PreferencesKeys.LAST_OPENED_COURSE_ID)
            }
        }
    }

    // ===== WRITE: Free Tier Limits =====

    suspend fun incrementFreeMessages(): Int {
        var newCount = 0
        context.dataStore.edit { preferences ->
            val today = getCurrentDateString()
            val lastReset = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]

            if (lastReset != today) {
                // Новий день — скидаємо ліміти
                preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE] = today
                preferences[PreferencesKeys.FREE_MESSAGES_TODAY] = 1
                preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 0
                newCount = 1
            } else {
                newCount = (preferences[PreferencesKeys.FREE_MESSAGES_TODAY] ?: 0) + 1
                preferences[PreferencesKeys.FREE_MESSAGES_TODAY] = newCount
            }
        }
        return newCount
    }

    suspend fun incrementFreeImprovisations(): Int {
        var newCount = 0
        context.dataStore.edit { preferences ->
            val today = getCurrentDateString()
            val lastReset = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]

            if (lastReset != today) {
                // Новий день — скидаємо ліміти
                preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE] = today
                preferences[PreferencesKeys.FREE_MESSAGES_TODAY] = 0
                preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 1
                newCount = 1
            } else {
                newCount = (preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] ?: 0) + 1
                preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = newCount
            }
        }
        return newCount
    }

    suspend fun getFreeMessagesRemaining(limit: Int = 10): Int {
        var remaining = limit
        context.dataStore.data.collect { preferences ->
            val today = getCurrentDateString()
            val lastReset = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]

            remaining = if (lastReset != today) {
                limit // Новий день — повний ліміт
            } else {
                limit - (preferences[PreferencesKeys.FREE_MESSAGES_TODAY] ?: 0)
            }
        }
        return remaining.coerceAtLeast(0)
    }

    // ===== CLEAR =====

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearSessionStats() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.LAST_ACTIVE_DATE)
            preferences.remove(PreferencesKeys.TODAY_MINUTES)
            preferences.remove(PreferencesKeys.TODAY_EXERCISES)
        }
    }

    // ===== HELPERS =====

    private fun mapToUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
            hasCompletedDiagnostic = preferences[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] ?: false,
            onboardingCompletedAt = preferences[PreferencesKeys.ONBOARDING_COMPLETED_AT],
            userGoal = preferences[PreferencesKeys.USER_GOAL] ?: "GENERAL",
            dailyTrainingMinutes = preferences[PreferencesKeys.DAILY_TRAINING_MINUTES] ?: 15,
            isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false,
            premiumExpiresAt = preferences[PreferencesKeys.PREMIUM_EXPIRES_AT],
            isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false,
            useSystemTheme = preferences[PreferencesKeys.USE_SYSTEM_THEME] ?: true,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            reminderTime = preferences[PreferencesKeys.REMINDER_TIME] ?: "09:00",
            lastActiveDate = preferences[PreferencesKeys.LAST_ACTIVE_DATE],
            currentStreak = preferences[PreferencesKeys.CURRENT_STREAK] ?: 0,
            todayMinutes = preferences[PreferencesKeys.TODAY_MINUTES] ?: 0,
            todayExercises = preferences[PreferencesKeys.TODAY_EXERCISES] ?: 0,
            lastSelectedTab = preferences[PreferencesKeys.LAST_SELECTED_TAB] ?: 0,
            lastOpenedCourseId = preferences[PreferencesKeys.LAST_OPENED_COURSE_ID],
            freeMessagesToday = preferences[PreferencesKeys.FREE_MESSAGES_TODAY] ?: 0,
            freeImprovisationsToday = preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] ?: 0,
            lastLimitResetDate = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]
        )
    }

    private fun getCurrentDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
