package com.aivoicepower.ui.screens.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordDialog(
    email: String,
    onEmailChanged: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    isSent: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF2D2640),
        title = {
            Text(
                text = if (isSent) "Лист надіслано!" else "Відновлення паролю",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            if (isSent) {
                Text(
                    text = "Перевірте вашу пошту $email для відновлення паролю",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Введіть вашу електронну пошту і ми надішлемо посилання для відновлення паролю",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChanged,
                        label = { Text("Електронна пошта") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFF8B5CF6),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            cursorColor = Color(0xFF8B5CF6),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (isSent) {
                TextButton(onClick = onDismiss) {
                    Text("OK", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
                }
            } else {
                TextButton(onClick = onSend) {
                    Text("Надіслати", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isSent) {
                TextButton(onClick = onDismiss) {
                    Text("Скасувати", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    )
}
