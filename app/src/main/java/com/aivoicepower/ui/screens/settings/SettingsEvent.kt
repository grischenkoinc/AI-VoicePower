package com.aivoicepower.ui.screens.settings

sealed class SettingsEvent {
    object NavigateToPremium : SettingsEvent()
    object ToggleReminder : SettingsEvent()
    data class SetDailyGoal(val minutes: Int) : SettingsEvent()
    object ShowDailyGoalPicker : SettingsEvent()
    object ToggleSound : SettingsEvent()
    object ToggleSync : SettingsEvent()
    object LogoutClicked : SettingsEvent()
    object ConfirmLogout : SettingsEvent()
    object DeleteAccountClicked : SettingsEvent()
    object ConfirmDeleteAccount : SettingsEvent()
    object ClearDataClicked : SettingsEvent()
    object ConfirmClearData : SettingsEvent()
    object DismissDialog : SettingsEvent()
}
