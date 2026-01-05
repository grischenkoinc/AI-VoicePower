package com.aivoicepower.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val isPremium: Boolean = false,
    val currentStreak: Int = 0,
    val todayMinutes: Int = 0,
    val hasCompletedOnboarding: Boolean = false,
    val hasCompletedDiagnostic: Boolean = false,
    val userName: String? = null,
    val userGoal: String? = null,
    val dailyGoalMinutes: Int = 15,
    val freeImprovisationsToday: Int = 0
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val TODAY_MINUTES = intPreferencesKey("today_minutes")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val HAS_COMPLETED_DIAGNOSTIC = booleanPreferencesKey("has_completed_diagnostic")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_GOAL = stringPreferencesKey("user_goal")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")
        val LAST_ACTIVITY_DATE = stringPreferencesKey("last_activity_date")
        val FREE_IMPROVISATIONS_TODAY = intPreferencesKey("free_improvisations_today")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                isPremium = true, // TEMP: Premium unlocked for testing
                currentStreak = preferences[PreferencesKeys.CURRENT_STREAK] ?: 0,
                todayMinutes = preferences[PreferencesKeys.TODAY_MINUTES] ?: 0,
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
                hasCompletedDiagnostic = preferences[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] ?: false,
                userName = preferences[PreferencesKeys.USER_NAME],
                userGoal = preferences[PreferencesKeys.USER_GOAL],
                dailyGoalMinutes = preferences[PreferencesKeys.DAILY_GOAL_MINUTES] ?: 15,
                freeImprovisationsToday = preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] ?: 0
            )
        }

    // Direct Flow for isPremium (used by PaywallViewModel)
    val isPremium: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            true // TEMP: Premium unlocked for testing
        }

    // Direct Flow for hasCompletedOnboarding (used by v2 ViewModels)
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
        }

    suspend fun updatePremiumStatus(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }

    // Alias for BillingRepository compatibility
    suspend fun setPremiumStatus(isPremium: Boolean, expiresAt: Long?) {
        updatePremiumStatus(isPremium)
    }

    suspend fun updateStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] = streak
        }
    }

    suspend fun updateTodayMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TODAY_MINUTES] = minutes
        }
    }

    suspend fun addMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TODAY_MINUTES] ?: 0
            preferences[PreferencesKeys.TODAY_MINUTES] = current + minutes
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun setDiagnosticCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] = completed
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun setUserGoal(goal: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_GOAL] = goal
        }
    }

    suspend fun setDailyGoalMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_GOAL_MINUTES] = minutes
        }
    }

    // Alias for v2 ViewModels compatibility
    suspend fun setDailyTrainingMinutes(minutes: Int) {
        setDailyGoalMinutes(minutes)
    }

    suspend fun resetDailyProgress() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TODAY_MINUTES] = 0
        }
    }

    suspend fun incrementFreeImprovisations() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] ?: 0
            preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = current + 1
        }
    }

    suspend fun resetFreeImprovisations() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 0
        }
    }
}
