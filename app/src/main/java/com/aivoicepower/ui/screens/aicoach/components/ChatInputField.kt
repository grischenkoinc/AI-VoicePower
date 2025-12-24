package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onVoiceInputClick: () -> Unit,
    onUploadClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    // Animation for microphone when listening
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val micColor by animateColorAsState(
        targetValue = if (isListening) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "mic_color"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Attach button
            IconButton(
                onClick = onUploadClick,
                enabled = enabled && !isListening
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Завантажити аудіо",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Text field
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        if (isListening) "Говоріть..." else "Запитайте про мовлення..."
                    )
                },
                enabled = enabled && !isListening,
                maxLines = 4,
                singleLine = false
            )

            // Voice input button
            IconButton(
                onClick = onVoiceInputClick,
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isListening) "Зупинити" else "Голосовий ввід",
                    tint = micColor,
                    modifier = if (isListening) {
                        Modifier.graphicsLayer {
                            scaleX = micScale
                            scaleY = micScale
                        }
                    } else {
                        Modifier
                    }
                )
            }

            // Send button
            IconButton(
                onClick = onSendClick,
                enabled = enabled && text.isNotBlank() && !isLoading && !isListening
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Надіслати",
                        tint = if (text.isNotBlank() && !isListening) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
