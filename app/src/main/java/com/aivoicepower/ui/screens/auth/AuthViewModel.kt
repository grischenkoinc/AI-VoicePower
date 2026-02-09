package com.aivoicepower.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.user.AuthUser
import com.aivoicepower.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.ToggleMode -> {
                _state.update {
                    it.copy(
                        isLogin = !it.isLogin,
                        error = null,
                        emailError = null,
                        passwordError = null,
                        confirmPasswordError = null,
                        nameError = null
                    )
                }
            }
            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, emailError = null, error = null) }
            }
            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, passwordError = null, error = null) }
            }
            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.password, confirmPasswordError = null, error = null) }
            }
            is AuthEvent.DisplayNameChanged -> {
                _state.update { it.copy(displayName = event.name, nameError = null, error = null) }
            }
            is AuthEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is AuthEvent.ToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is AuthEvent.SubmitClicked -> {
                if (_state.value.isLogin) login() else register()
            }
            is AuthEvent.GoogleSignInResult -> {
                signInWithGoogle(event.idToken)
            }
            is AuthEvent.GoogleSignInFailed -> {
                _state.update { it.copy(isGoogleLoading = false, error = "Не вдалося увійти через Google") }
            }
            is AuthEvent.ForgotPasswordClicked -> {
                _state.update { it.copy(showForgotPasswordDialog = true, forgotPasswordEmail = it.email, forgotPasswordSent = false) }
            }
            is AuthEvent.DismissForgotPassword -> {
                _state.update { it.copy(showForgotPasswordDialog = false) }
            }
            is AuthEvent.ForgotPasswordEmailChanged -> {
                _state.update { it.copy(forgotPasswordEmail = event.email) }
            }
            is AuthEvent.SendResetEmail -> {
                sendResetEmail()
            }
            is AuthEvent.SkipClicked -> {
                viewModelScope.launch {
                    userPreferencesDataStore.setAuthCompleted(true)
                    _state.update { it.copy(isNavigating = true) }
                }
            }
            is AuthEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun login() {
        val currentState = _state.value
        if (!validateLoginFields(currentState)) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(currentState.email.trim(), currentState.password)
            result.fold(
                onSuccess = { user -> onAuthSuccess(user) },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    private fun register() {
        val currentState = _state.value
        if (!validateRegisterFields(currentState)) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(
                email = currentState.email.trim(),
                password = currentState.password,
                displayName = currentState.displayName.trim()
            )
            result.fold(
                onSuccess = { user -> onAuthSuccess(user) },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    private fun signInWithGoogle(idToken: String) {
        _state.update { it.copy(isGoogleLoading = true, error = null) }
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { user -> onAuthSuccess(user) },
                onFailure = { e -> _state.update { it.copy(isGoogleLoading = false, error = e.message) } }
            )
        }
    }

    private suspend fun onAuthSuccess(user: AuthUser) {
        userPreferencesDataStore.setFirebaseUid(user.uid)
        user.email?.let { userPreferencesDataStore.setUserEmail(it) }
        user.displayName?.let { userPreferencesDataStore.setUserName(it) }
        user.photoUrl?.let { userPreferencesDataStore.setUserPhotoUrl(it) }
        userPreferencesDataStore.setAuthCompleted(true)
        _state.update { it.copy(isLoading = false, isGoogleLoading = false, isNavigating = true) }
    }

    private fun sendResetEmail() {
        val email = _state.value.forgotPasswordEmail.trim()
        if (email.isEmpty() || !isValidEmail(email)) return

        viewModelScope.launch {
            authRepository.resetPassword(email).fold(
                onSuccess = { _state.update { it.copy(forgotPasswordSent = true) } },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    private fun validateLoginFields(state: AuthScreenState): Boolean {
        var valid = true
        val emailError = when {
            state.email.isBlank() -> { valid = false; "Введіть електронну пошту" }
            !isValidEmail(state.email.trim()) -> { valid = false; "Невірний формат пошти" }
            else -> null
        }
        val passwordError = when {
            state.password.isEmpty() -> { valid = false; "Введіть пароль" }
            state.password.length < 6 -> { valid = false; "Мінімум 6 символів" }
            else -> null
        }
        _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
        return valid
    }

    private fun validateRegisterFields(state: AuthScreenState): Boolean {
        var valid = true
        val nameError = when {
            state.displayName.isBlank() -> { valid = false; "Введіть ваше ім'я" }
            else -> null
        }
        val emailError = when {
            state.email.isBlank() -> { valid = false; "Введіть електронну пошту" }
            !isValidEmail(state.email.trim()) -> { valid = false; "Невірний формат пошти" }
            else -> null
        }
        val passwordError = when {
            state.password.isEmpty() -> { valid = false; "Введіть пароль" }
            state.password.length < 6 -> { valid = false; "Мінімум 6 символів" }
            else -> null
        }
        val confirmPasswordError = when {
            state.confirmPassword.isEmpty() -> { valid = false; "Підтвердіть пароль" }
            state.password != state.confirmPassword -> { valid = false; "Паролі не збігаються" }
            else -> null
        }
        _state.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }
        return valid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
