package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors

@Composable
fun ChatInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onUploadClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val canSend = enabled && text.isNotBlank() && !isLoading

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Attach button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onUploadClick() },
                enabled = enabled,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Завантажити аудіо",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Compact text field
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Або напишіть...",
                    color = Color.White.copy(alpha = 0.3f),
                    style = TextStyle(fontSize = 13.sp)
                )
            },
            enabled = enabled,
            maxLines = 3,
            singleLine = false,
            textStyle = TextStyle(
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                disabledContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = PrimaryColors.light.copy(alpha = 0.4f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                disabledBorderColor = Color.White.copy(alpha = 0.06f),
                cursorColor = PrimaryColors.light
            ),
            shape = RoundedCornerShape(20.dp)
        )

        // Send button (visible when text present)
        if (text.isNotBlank()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (canSend) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.05f))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onSendClick() },
                    enabled = canSend,
                    modifier = Modifier.size(36.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryColors.light
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Надіслати",
                            tint = if (canSend) Color.White else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
