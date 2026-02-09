package com.aivoicepower.ui.screens.auth

sealed class AuthEvent {
    object ToggleMode : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class ConfirmPasswordChanged(val password: String) : AuthEvent()
    data class DisplayNameChanged(val name: String) : AuthEvent()
    object TogglePasswordVisibility : AuthEvent()
    object ToggleConfirmPasswordVisibility : AuthEvent()
    object SubmitClicked : AuthEvent()
    data class GoogleSignInResult(val idToken: String) : AuthEvent()
    object GoogleSignInFailed : AuthEvent()
    object ForgotPasswordClicked : AuthEvent()
    object DismissForgotPassword : AuthEvent()
    data class ForgotPasswordEmailChanged(val email: String) : AuthEvent()
    object SendResetEmail : AuthEvent()
    object SkipClicked : AuthEvent()
    object ClearError : AuthEvent()
}
