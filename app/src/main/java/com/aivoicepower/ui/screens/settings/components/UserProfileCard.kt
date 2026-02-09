package com.aivoicepower.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserProfileCard(
    userName: String?,
    userEmail: String?,
    isAuthenticated: Boolean,
    onLoginClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF764BA2).copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(20.dp)
            )
            .clickable { if (isAuthenticated) onEditProfileClick() else onLoginClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar - 52dp gradient circle
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                )
                .background(primaryGradient, CircleShape)
                .border(
                    1.5.dp,
                    Color.White.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isAuthenticated && userName != null) {
                Text(
                    text = userName.take(1).uppercase(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isAuthenticated) (userName ?: "Користувач") else "Гість",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isAuthenticated) (userEmail ?: "") else "Увійдіть для синхронізації",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // When not authenticated - gradient CTA button; when authenticated - chevron
        if (!isAuthenticated) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(primaryGradient, RoundedCornerShape(12.dp))
                    .clickable(onClick = onLoginClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Увійти",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
