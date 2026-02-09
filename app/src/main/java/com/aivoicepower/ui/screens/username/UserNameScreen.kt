package com.aivoicepower.ui.screens.username

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun UserNameScreen(
    onContinue: () -> Unit,
    viewModel: UserNameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isNavigating) {
        if (state.isNavigating) {
            onContinue()
        }
    }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "username_anim")

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "ring_rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    GradientBackground(content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Animated Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    // Outer rotating glow ring
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .rotate(ringRotation)
                            .background(
                                Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFF667EEA).copy(alpha = glowAlpha),
                                        Color(0xFFA78BFA).copy(alpha = 0.05f),
                                        Color(0xFFFBBF24).copy(alpha = glowAlpha),
                                        Color(0xFF764BA2).copy(alpha = 0.05f),
                                        Color(0xFF667EEA).copy(alpha = glowAlpha)
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // Main gradient circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF667EEA),
                                        Color(0xFF764BA2),
                                        Color(0xFF9333EA)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "Як до вас звертатись?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Будемо використовувати ваше ім'я\nдля персоналізації досвіду",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Name Input
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onEvent(UserNameEvent.NameChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    placeholder = {
                        Text(
                            text = "Введіть ваше ім'я",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (state.name.isNotBlank()) {
                                viewModel.onEvent(UserNameEvent.Continue)
                            }
                        }
                    ),
                    isError = state.error != null
                )

                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color(0xFFFEF2F2),
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.weight(1.5f))

                // Continue Button
                Button(
                    onClick = { viewModel.onEvent(UserNameEvent.Continue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color.White.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = state.name.isNotBlank() && !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF667EEA),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Продовжити",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    })
}
