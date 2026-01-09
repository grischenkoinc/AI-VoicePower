package com.aivoicepower.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.components.*
import com.aivoicepower.ui.theme.*

@Composable
fun ComponentsShowcaseScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667EEA), // #667eea
                        Color(0xFF764BA2)  // #764ba2
                    )
                )
            )
    ) {
        // Geometric shapes background
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = (-50).dp)
                .size(300.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                .blur(40.dp)
        )

        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = 500.dp)
                .size(200.dp)
                .background(Color(0xFFF59E0B).copy(alpha = 0.1f), CircleShape)
                .blur(40.dp)
        )

        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 250.dp)
                .size(150.dp)
                .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape)
                .blur(40.dp)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.xl)
        ) {
            // Top Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress Circle
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1/4",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                // Lesson Badge
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.padding(horizontal = Spacing.sm)
                ) {
                    Text(
                        text = "УРОК 1",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    )
                }
            }

            // 3D Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg)
            ) {
                // Track with fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ProgressBarColors.trackStart,
                                    ProgressBarColors.trackEnd
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadowPreset(ShadowPreset.PROGRESS_TRACK, RoundedCornerShape(20.dp))
                ) {
                    // Fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .fillMaxHeight()
                            .background(
                                brush = Gradients.progressFill,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .shadowPreset(ShadowPreset.SUCCESS_RING, RoundedCornerShape(20.dp))
                    )
                }

                // Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Крок 1 з 4",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Теорія",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Theory Card (темна шапка)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                shape = RoundedCornerShape(CornerRadius.xxl),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    // Dark header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardHeaderBackground(RoundedCornerShape(topStart = CornerRadius.xxl, topEnd = CornerRadius.xxl))
                            .padding(Spacing.lg)
                    ) {
                        Column {
                            // Section tag
                            Surface(
                                color = PrimaryColors.default,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(bottom = Spacing.md)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    Text(text = "\uD83D\uDCD6", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        text = "ТЕОРІЯ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                            }

                            // Title
                            Text(
                                text = "Основи артикуляції",
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = Spacing.md)
                            )

                            // Level pill
                            Surface(
                                color = SecondaryColors.default,
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "\u26A1", style = MaterialTheme.typography.labelLarge)
                                    Text(
                                        text = "Рівень 3",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextColors.onSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Light body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardBodyBackground(RoundedCornerShape(bottomStart = CornerRadius.xxl, bottomEnd = CornerRadius.xxl))
                            .padding(Spacing.lg)
                    ) {
                        Column {
                            Text(
                                text = "Що таке артикуляція?",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextColors.primary,
                                modifier = Modifier.padding(bottom = Spacing.sm)
                            )

                            Text(
                                text = "Артикуляція — це робота органів мовлення (губ, язика, щелеп) під час вимови звуків. Це основа чіткого мовлення.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextColors.secondary,
                                modifier = Modifier.padding(bottom = Spacing.md)
                            )

                            // Highlight box
                            Surface(
                                color = SecondaryColors.default.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(CornerRadius.md),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Spacing.md)
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.md)
                                ) {
                                    Text(
                                        text = "\uD83D\uDCA1 КЛЮЧОВИЙ ІНСАЙТ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SecondaryColors.dark,
                                        modifier = Modifier.padding(bottom = Spacing.xs)
                                    )
                                    Text(
                                        text = "Чітка дикція = впевненість у спілкуванні",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextColors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Practice Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                shape = RoundedCornerShape(CornerRadius.xxl),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    // Dark header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardHeaderBackground(RoundedCornerShape(topStart = CornerRadius.xxl, topEnd = CornerRadius.xxl))
                            .padding(Spacing.lg)
                    ) {
                        Column {
                            // Practice tag
                            Surface(
                                color = SecondaryColors.default,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(bottom = Spacing.md)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    Text(text = "\uD83D\uDD25", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        text = "ПРАКТИКА • 1/5",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                            }

                            // Title
                            Text(
                                text = "Посмішка → Трубочка",
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White
                            )
                        }
                    }

                    // Light body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardBodyBackground(RoundedCornerShape(bottomStart = CornerRadius.xxl, bottomEnd = CornerRadius.xxl))
                            .padding(Spacing.lg)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Exercise visual
                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(CornerRadius.lg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Spacing.lg)
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "\uD83D\uDE04", style = MaterialTheme.typography.displayLarge)
                                            Text(
                                                text = "Широка посмішка",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextColors.muted,
                                                modifier = Modifier.padding(top = Spacing.xs, bottom = Spacing.xs)
                                            )
                                            Surface(
                                                color = PrimaryColors.default,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = "2 сек",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "→",
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = TextColors.muted
                                        )

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "\uD83D\uDE17", style = MaterialTheme.typography.displayLarge)
                                            Text(
                                                text = "Губи трубочкою",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextColors.muted,
                                                modifier = Modifier.padding(top = Spacing.xs, bottom = Spacing.xs)
                                            )
                                            Surface(
                                                color = PrimaryColors.default,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = "2 сек",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Divider(
                                        modifier = Modifier.padding(vertical = Spacing.lg),
                                        color = Color(0xFFD1D5DB)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(
                                                    color = PrimaryColors.default.copy(alpha = 0.12f),
                                                    shape = CircleShape
                                                )
                                                .padding(2.dp)
                                                .background(
                                                    color = Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "\uD83D\uDD04", style = MaterialTheme.typography.headlineMedium)
                                        }

                                        Column {
                                            Text(
                                                text = "Повтори",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextColors.muted
                                            )
                                            Text(
                                                text = "10 разів",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = PrimaryColors.default
                                            )
                                        }
                                    }
                                }
                            }

                            // Record Button
                            var recording by remember { mutableStateOf(false) }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RecordButton(
                                    isRecording = recording,
                                    onClick = { recording = !recording }
                                )

                                Text(
                                    text = if (recording) "Йде запис..." else "Натисни для запису",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextColors.muted,
                                    modifier = Modifier.padding(top = Spacing.md)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                SecondaryButton(
                    text = "← Назад",
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(CornerRadius.lg)
                ) {
                    Text(
                        text = "Далі →",
                        color = Color(0xFF667EEA)
                    )
                }
            }
        }
    }
}
