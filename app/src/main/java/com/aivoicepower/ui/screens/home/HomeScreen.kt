package com.aivoicepower.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.home.QuickAction
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToImprovisation: () -> Unit,
    onNavigateToAICoach: () -> Unit,
    onNavigateToWarmup: () -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Header
                HomeHeader(onSettings = onNavigateToSettings)

                // Streak Card
                StreakCard()

                // Motivation Card
                MotivationCard()

                // Daily Goal
                DailyGoalCard()

                // Skills Section
                SkillsSection()

                // Continue Course
                ContinueCourseSection(onNavigateToCourse = onNavigateToCourse)

                // Quick Actions
                QuickActionsSection(
                    actions = state.quickActions,
                    onActionClick = { action ->
                        when (action.route) {
                            "warmup" -> onNavigateToWarmup()
                            "random_topic" -> onNavigateToImprovisation()
                            "ai_coach" -> onNavigateToAICoach()
                            "tongue_twisters" -> onNavigateToWarmup()
                            else -> {}
                        }
                    }
                )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Визначаємо час доби
    val calendar = remember { Calendar.getInstance() }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 0..5 -> "Доброї ночі!"
        in 6..11 -> "Доброго ранку!"
        in 12..17 -> "Доброго дня!"
        in 18..22 -> "Доброго вечора!"
        else -> "Доброї ночі!"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = greeting,
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Євгеній",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

        // Settings button з gradient
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                    ),
                    CircleShape
                )
                .clickable { onSettings() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚙️", fontSize = 20.sp)
        }
    }
}

@Composable
private fun StreakCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.3f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x73667EEA), // 45% opacity
                        Color(0x59764BA2)  // 35% opacity
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fire icon box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.5f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔥", fontSize = 28.sp)
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "STREAK",
                    style = AppTypography.labelSmall,
                    color = TextColors.onDarkSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "7 днів",
                    style = AppTypography.displayLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }
        }

        // Week circles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        ) {
            DayCircle("Пн", filled = true)
            DayCircle("Вт", filled = true)
            DayCircle("Ср", filled = true)
            DayCircle("Чт", filled = true)
            DayCircle("Пт", filled = true)
            DayCircle("Сб", filled = true)
            DayCircle("Нд", filled = true, isToday = true)
        }
    }
}

@Composable
private fun DayCircle(
    label: String,
    filled: Boolean,
    isToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .shadow(
                elevation = if (isToday) 10.dp else 6.dp,
                shape = CircleShape,
                spotColor = when {
                    isToday -> Color(0xFFFBBF24).copy(alpha = 0.6f)
                    filled -> Color(0xFF22C55E).copy(alpha = 0.5f)
                    else -> Color.Black.copy(alpha = 0.15f)
                }
            )
            .background(
                when {
                    isToday -> Brush.linearGradient(
                        colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B)),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                    filled -> Brush.linearGradient(
                        colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A)),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                    else -> Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                },
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = when {
                isToday -> Color(0xFF1A1A1A)
                filled -> Color.White
                else -> TextColors.onDarkSecondary
            },
            fontSize = 9.sp,
            fontWeight = if (isToday) FontWeight.Black else FontWeight.ExtraBold,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun MotivationCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFBEB), // Світло-жовтий
                        Color(0xFFFEF3C7)  // Золотистий
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💡", fontSize = 32.sp)
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Порада дня",
                    style = AppTypography.labelMedium,
                    color = Color(0xFFD97706), // Темно-помаранчевий
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Перші 20 секунд виступу визначають 80% враження аудиторії",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E), // Темно-коричневий
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun DailyGoalCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .drawBehind {
                // Top highlight для 3D ефекту
                drawRect(
                    color = Color.White.copy(alpha = 0.4f),
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width, 3f)
                )
            }
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header БЕЗ процента
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Денна ціль",
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Progress ring + stats
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress Ring з 3 сегментами
            ProgressRingSegmented(
                completedTasks = 1,
                totalTasks = 3
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Завершено",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "1 з 3",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Залишилось",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "~15 хв",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tasks list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskRow(
                title = "Ранкова розминка",
                meta = "5 хв • Завершено",
                completed = true
            )
            TaskRow(
                title = "Урок 3: Темп мовлення",
                meta = "12 хв • Чітке мовлення",
                completed = false
            )
            TaskRow(
                title = "Імпровізація дня",
                meta = "3 хв • Challenge",
                completed = false
            )
        }
    }
}

@Composable
private fun ProgressRingSegmented(
    completedTasks: Int,
    totalTasks: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle
            drawCircle(
                color = Color(0xFFE5E5EA),
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
            )

            // Segments
            val segmentAngle = 360f / totalTasks
            val gapAngle = 8f // Проміжок між сегментами

            for (i in 0 until totalTasks) {
                val startAngle = -90f + i * segmentAngle + gapAngle / 2
                val sweepAngle = segmentAngle - gapAngle

                val segmentColor = if (i < completedTasks) {
                    Color(0xFF667EEA) // Заповнений - синій
                } else {
                    Color(0xFFE5E5EA) // Порожній
                }

                drawArc(
                    color = segmentColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        // Center text
        Text(
            text = "$completedTasks/$totalTasks",
            style = AppTypography.titleLarge,
            color = Color(0xFF667EEA), // Синій
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun TaskRow(
    title: String,
    meta: String,
    completed: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (completed) Color(0xFFF5F5F7).copy(alpha = 0.5f)
                else Color(0xFFF5F5F7),
                RoundedCornerShape(12.dp)
            )
            .scaleOnPress(pressedScale = 0.98f)
            .clickable { }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (completed) Color(0xFF34C759)
                    else Color.Transparent,
                    CircleShape
                )
                .border(
                    2.5.dp,
                    if (completed) Color(0xFF34C759)
                    else Color(0xFFD1D1D6),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = meta,
                style = AppTypography.bodySmall,
                color = TextColors.onLightMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SkillsSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Навички",
                style = AppTypography.titleLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Всі →",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkillCard(
                emoji = "📢",
                name = "Дикція",
                percentage = 89,
                growth = "+5%",
                gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
            )
            SkillCard(
                emoji = "⚡",
                name = "Темп",
                percentage = 92,
                growth = "+3%",
                gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
            )
            SkillCard(
                emoji = "🎭",
                name = "Емоції",
                percentage = 85,
                growth = "+7%",
                gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
            )
        }
    }
}

@Composable
private fun SkillCard(
    emoji: String,
    name: String,
    percentage: Int,
    growth: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .glassBackground(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = GlassColors.background,
                borderColor = GlassColors.borderLight
            )
            .multiLayerShadow(
                elevation = 8.dp,
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clickable { }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(gradientColors),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
        }

        Text(
            text = name,
            style = AppTypography.bodyMedium,
            color = TextColors.onDarkPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.5.dp)
                .background(
                    Color.White.copy(alpha = 0.75f),
                    RoundedCornerShape(6.75.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                        ),
                        RoundedCornerShape(6.75.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(6.75.dp),
                        spotColor = Color(0xFF22C55E).copy(alpha = 0.5f)
                    )
            )
        }

        Text(
            text = "$percentage% • ↗ $growth",
            style = AppTypography.bodySmall,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContinueCourseSection(
    onNavigateToCourse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Продовжити",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .background(BackgroundColors.surface, RoundedCornerShape(20.dp))
                .clickable { onNavigateToCourse("course_1") }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Gradients.appBackground),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(GlassColors.background, CircleShape)
                        .border(2.dp, GlassColors.borderMedium, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "▶", color = Color.White, fontSize = 20.sp)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "КУРС 1",
                        style = AppTypography.labelSmall,
                        color = Color(0xFF667EEA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "9/15 уроків",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Чітке мовлення",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Наступний: Урок 10 • Інтонація та паузи",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFE5E5EA), RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                RoundedCornerShape(3.dp)
                            )
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(3.dp),
                                spotColor = Color(0xFF667EEA).copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Швидкий доступ",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.take(2).forEach { action ->
                    QuickActionCard(
                        action = action,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.drop(2).take(2).forEach { action ->
                    QuickActionCard(
                        action = action,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Map routes to gradient colors
    val gradientColors = when (action.route) {
        "warmup" -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
        "random_topic" -> listOf(Color(0xFFF59E0B), Color(0xFFF97316))
        "ai_coach" -> listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
        "tongue_twisters" -> listOf(Color(0xFF10B981), Color(0xFF14B8A6))
        else -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .scaleOnPress(pressedScale = 0.95f)
            .clickable { onClick() }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(gradientColors),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = action.icon, fontSize = 28.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = action.title,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
