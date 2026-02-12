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
    val freeImprovisationsToday: Int = 0,
    val lastTipUpdateTime: Long = 0, // Час останнього оновлення пораді
    val currentTipId: String? = null, // ID поточної пораді
    val lastDailyPlanDate: String? = null, // Дата останнього оновлення денного плану (yyyy-MM-dd)
    // AI Analysis limits
    val freeAnalysesToday: Int = 0,
    val freeAdAnalysesToday: Int = 0,
    val freeImprovAnalysesToday: Int = 0,
    val freeAdImprovToday: Int = 0,
    val lastLimitResetDate: String? = null
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
        val LAST_TIP_UPDATE_TIME = longPreferencesKey("last_tip_update_time")
        val CURRENT_TIP_ID = stringPreferencesKey("current_tip_id")
        val LAST_DAILY_PLAN_DATE = stringPreferencesKey("last_daily_plan_date")
        val FIREBASE_UID = stringPreferencesKey("firebase_uid")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        val HAS_COMPLETED_AUTH = booleanPreferencesKey("has_completed_auth")
        // AI Analysis limits
        val FREE_ANALYSES_TODAY = intPreferencesKey("free_analyses_today")
        val FREE_AD_ANALYSES_TODAY = intPreferencesKey("free_ad_analyses_today")
        val FREE_IMPROV_ANALYSES_TODAY = intPreferencesKey("free_improv_analyses_today")
        val FREE_AD_IMPROV_TODAY = intPreferencesKey("free_ad_improv_today")
        val LAST_LIMIT_RESET_DATE = stringPreferencesKey("last_limit_reset_date")
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
                isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false,
                currentStreak = preferences[PreferencesKeys.CURRENT_STREAK] ?: 0,
                todayMinutes = preferences[PreferencesKeys.TODAY_MINUTES] ?: 0,
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
                hasCompletedDiagnostic = preferences[PreferencesKeys.HAS_COMPLETED_DIAGNOSTIC] ?: false,
                userName = preferences[PreferencesKeys.USER_NAME],
                userGoal = preferences[PreferencesKeys.USER_GOAL],
                dailyGoalMinutes = preferences[PreferencesKeys.DAILY_GOAL_MINUTES] ?: 15,
                freeImprovisationsToday = preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] ?: 0,
                lastTipUpdateTime = preferences[PreferencesKeys.LAST_TIP_UPDATE_TIME] ?: 0,
                currentTipId = preferences[PreferencesKeys.CURRENT_TIP_ID],
                lastDailyPlanDate = preferences[PreferencesKeys.LAST_DAILY_PLAN_DATE],
                freeAnalysesToday = preferences[PreferencesKeys.FREE_ANALYSES_TODAY] ?: 0,
                freeAdAnalysesToday = preferences[PreferencesKeys.FREE_AD_ANALYSES_TODAY] ?: 0,
                freeImprovAnalysesToday = preferences[PreferencesKeys.FREE_IMPROV_ANALYSES_TODAY] ?: 0,
                freeAdImprovToday = preferences[PreferencesKeys.FREE_AD_IMPROV_TODAY] ?: 0,
                lastLimitResetDate = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]
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
            preferences[PreferencesKeys.IS_PREMIUM] ?: false
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
    @Suppress("UNUSED_PARAMETER")
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

    suspend fun updateDailyTip(tipId: String, updateTime: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_TIP_ID] = tipId
            preferences[PreferencesKeys.LAST_TIP_UPDATE_TIME] = updateTime
        }
    }

    suspend fun updateDailyPlanDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_DAILY_PLAN_DATE] = date
        }
    }

    suspend fun resetFreeImprovisations() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 0
        }
    }

    // ===== AI Analysis limits =====

    suspend fun incrementFreeAnalyses() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FREE_ANALYSES_TODAY] ?: 0
            preferences[PreferencesKeys.FREE_ANALYSES_TODAY] = current + 1
        }
    }

    suspend fun incrementFreeAdAnalyses() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FREE_AD_ANALYSES_TODAY] ?: 0
            preferences[PreferencesKeys.FREE_AD_ANALYSES_TODAY] = current + 1
        }
    }

    suspend fun incrementFreeImprovAnalyses() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FREE_IMPROV_ANALYSES_TODAY] ?: 0
            preferences[PreferencesKeys.FREE_IMPROV_ANALYSES_TODAY] = current + 1
        }
    }

    suspend fun incrementFreeAdImprov() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FREE_AD_IMPROV_TODAY] ?: 0
            preferences[PreferencesKeys.FREE_AD_IMPROV_TODAY] = current + 1
        }
    }

    suspend fun resetDailyAnalysisLimits() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FREE_ANALYSES_TODAY] = 0
            preferences[PreferencesKeys.FREE_AD_ANALYSES_TODAY] = 0
            preferences[PreferencesKeys.FREE_IMPROV_ANALYSES_TODAY] = 0
            preferences[PreferencesKeys.FREE_AD_IMPROV_TODAY] = 0
            preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 0
            preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE] = java.text.SimpleDateFormat(
                "yyyy-MM-dd", java.util.Locale.getDefault()
            ).format(java.util.Date())
        }
    }

    suspend fun checkAndResetDailyLimits() {
        val today = java.text.SimpleDateFormat(
            "yyyy-MM-dd", java.util.Locale.getDefault()
        ).format(java.util.Date())

        context.dataStore.edit { preferences ->
            val lastReset = preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE]
            if (lastReset != today) {
                preferences[PreferencesKeys.FREE_ANALYSES_TODAY] = 0
                preferences[PreferencesKeys.FREE_AD_ANALYSES_TODAY] = 0
                preferences[PreferencesKeys.FREE_IMPROV_ANALYSES_TODAY] = 0
                preferences[PreferencesKeys.FREE_AD_IMPROV_TODAY] = 0
                preferences[PreferencesKeys.FREE_IMPROVISATIONS_TODAY] = 0
                preferences[PreferencesKeys.LAST_LIMIT_RESET_DATE] = today
            }
        }
    }

    // ===== Auth-related preferences =====

    val hasCompletedAuth: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_AUTH] ?: false
        }

    val userEmail: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.USER_EMAIL] }

    val userPhotoUrl: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.USER_PHOTO_URL] }

    val firebaseUid: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.FIREBASE_UID] }

    suspend fun setAuthCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_AUTH] = completed
        }
    }

    suspend fun setFirebaseUid(uid: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIREBASE_UID] = uid
        }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }

    suspend fun setUserPhotoUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PHOTO_URL] = url
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.FIREBASE_UID)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_PHOTO_URL)
            preferences[PreferencesKeys.HAS_COMPLETED_AUTH] = false
        }
    }
}
