package com.aivoicepower.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.firebase.auth.GoogleSignInHelper
import com.aivoicepower.ui.screens.auth.components.AuthTextField
import com.aivoicepower.ui.screens.auth.components.ForgotPasswordDialog
import com.aivoicepower.ui.screens.auth.components.GoogleSignInButton
import com.aivoicepower.ui.theme.components.GradientBackground

@Suppress("UNUSED_PARAMETER")
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onSkip: () -> Unit,
    googleSignInHelper: GoogleSignInHelper,
    showSkipButton: Boolean = true,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Navigation effect
    LaunchedEffect(state.isNavigating) {
        if (state.isNavigating) {
            onAuthSuccess()
        }
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val tokenResult = googleSignInHelper.getIdTokenFromIntent(result.data)
            tokenResult.fold(
                onSuccess = { idToken ->
                    viewModel.onEvent(AuthEvent.GoogleSignInResult(idToken))
                },
                onFailure = {
                    viewModel.onEvent(AuthEvent.GoogleSignInFailed)
                }
            )
        } else {
            viewModel.onEvent(AuthEvent.GoogleSignInFailed)
        }
    }

    // Forgot password dialog
    if (state.showForgotPasswordDialog) {
        ForgotPasswordDialog(
            email = state.forgotPasswordEmail,
            onEmailChanged = { viewModel.onEvent(AuthEvent.ForgotPasswordEmailChanged(it)) },
            onSend = { viewModel.onEvent(AuthEvent.SendResetEmail) },
            onDismiss = { viewModel.onEvent(AuthEvent.DismissForgotPassword) },
            isSent = state.forgotPasswordSent
        )
    }

    GradientBackground(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo
            Text(text = "🎤", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AI VoicePower",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Покращуй свій голос з AI",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(24.dp)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tab toggle
                AuthModeToggle(
                    isLogin = state.isLogin,
                    onToggle = { viewModel.onEvent(AuthEvent.ToggleMode) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
                AnimatedVisibility(visible = state.error != null) {
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFEF4444).copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Email field
                AuthTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
                    label = "Електронна пошта",
                    leadingIcon = Icons.Default.Email,
                    error = state.emailError,
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                AuthTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
                    label = "Пароль",
                    leadingIcon = Icons.Default.Lock,
                    error = state.passwordError,
                    isPassword = true,
                    isPasswordVisible = state.isPasswordVisible,
                    onTogglePasswordVisibility = { viewModel.onEvent(AuthEvent.TogglePasswordVisibility) },
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                    imeAction = if (state.isLogin) {
                        androidx.compose.ui.text.input.ImeAction.Done
                    } else {
                        androidx.compose.ui.text.input.ImeAction.Next
                    }
                )

                // Register: Confirm password
                AnimatedVisibility(visible = !state.isLogin) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        AuthTextField(
                            value = state.confirmPassword,
                            onValueChange = { viewModel.onEvent(AuthEvent.ConfirmPasswordChanged(it)) },
                            label = "Підтвердіть пароль",
                            leadingIcon = Icons.Default.Lock,
                            error = state.confirmPasswordError,
                            isPassword = true,
                            isPasswordVisible = state.isConfirmPasswordVisible,
                            onTogglePasswordVisibility = { viewModel.onEvent(AuthEvent.ToggleConfirmPasswordVisibility) },
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        )
                    }
                }

                // Forgot password (login only)
                if (state.isLogin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Забули пароль?",
                        color = Color(0xFFA78BFA),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { viewModel.onEvent(AuthEvent.ForgotPasswordClicked) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !state.isLoading) {
                            viewModel.onEvent(AuthEvent.SubmitClicked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (state.isLogin) "Увійти" else "Зареєструватися",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "  або  ",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Google Sign-In button
                GoogleSignInButton(
                    onClick = {
                        googleSignInLauncher.launch(googleSignInHelper.getSignInIntent())
                    },
                    isLoading = state.isGoogleLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skip button (only during onboarding, not from settings)
            if (showSkipButton) {
                TextButton(
                    onClick = { viewModel.onEvent(AuthEvent.SkipClicked) }
                ) {
                    Text(
                        text = "Пропустити",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    })
}

@Composable
private fun AuthModeToggle(
    isLogin: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
    ) {
        // Login tab
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (isLogin) Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ) else Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    ),
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable { if (!isLogin) onToggle() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Увійти",
                color = if (isLogin) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = if (isLogin) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp
            )
        }

        // Register tab
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (!isLogin) Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ) else Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    ),
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable { if (isLogin) onToggle() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Реєстрація",
                color = if (!isLogin) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = if (!isLogin) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp
            )
        }
    }
}
