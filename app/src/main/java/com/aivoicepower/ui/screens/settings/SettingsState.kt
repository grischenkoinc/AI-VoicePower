package com.aivoicepower.ui.screens.settings

data class SettingsState(
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhotoUrl: String? = null,
    val isAuthenticated: Boolean = false,
    val isPremium: Boolean = false,
    val isReminderEnabled: Boolean = false,
    val dailyGoalMinutes: Int = 15,
    val isSoundEnabled: Boolean = true,
    val isSyncEnabled: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val showClearDataDialog: Boolean = false,
    val showDailyGoalPicker: Boolean = false,
    val isLoading: Boolean = false
)
