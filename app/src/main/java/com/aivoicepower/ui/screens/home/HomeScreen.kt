package com.aivoicepower.ui.screens.home

import android.app.Activity
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.home.CurrentCourse
import com.aivoicepower.domain.model.home.QuickAction
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import java.util.Calendar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    onNavigateToImprovisation: () -> Unit,
    onNavigateToAICoach: () -> Unit,
    onNavigateToWarmup: () -> Unit,
    onNavigateToTongueTwisters: () -> Unit,
    onNavigateToWeakestSkill: () -> Unit,
    onNavigateToQuickWarmup: () -> Unit,
    onNavigateToRecordingHistory: () -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }

    // Double-back to exit app
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "Натисніть ще раз для виходу", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        // Show spinner while data loads, then all content appears together with stagger
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = Color.White.copy(alpha = 0.6f),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
            return@Box
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Header
                HomeHeader(
                    userName = state.userName,
                    greeting = state.greeting,
                    onSettings = onNavigateToSettings,
                    modifier = Modifier.staggeredEntry(index = 0, staggerDelay = 60)
                )

                // Streak Card
                StreakCard(
                    currentStreak = state.currentStreak,
                    weekProgress = state.weekProgress,
                    modifier = Modifier.staggeredEntry(index = 1, staggerDelay = 60)
                )

                // Motivation Card
                state.dailyTip?.let { tip ->
                    MotivationCard(
                        tip = tip,
                        modifier = Modifier.staggeredEntry(index = 2, staggerDelay = 60)
                    )
                }

                // Daily Limits Card (for free users)
                if (!state.isPremium) {
                    com.aivoicepower.ui.screens.home.components.DailyLimitsCard(
                        remainingAnalyses = state.remainingAnalyses,
                        maxAnalyses = state.maxFreeAnalyses,
                        remainingImprovAnalyses = state.remainingImprovAnalyses,
                        maxImprovAnalyses = state.maxFreeImprovAnalyses,
                        modifier = Modifier.staggeredEntry(index = 3, staggerDelay = 60)
                    )
                }

                // Daily Goal + Coach message
                state.todayPlan?.let { plan ->
                    DailyGoalCard(
                        plan = plan,
                        coachMessage = state.coachMessage,
                        onCoachClick = onNavigateToAICoach,
                        modifier = Modifier.staggeredEntry(index = 4, staggerDelay = 60)
                    )
                }

                // Skills Section
                SkillsSection(
                    skills = state.skills,
                    modifier = Modifier.staggeredEntry(index = 5, staggerDelay = 60)
                )

                // Continue Course
                state.currentCourse?.let { course ->
                    ContinueCourseSection(
                        course = course,
                        onCourseClick = {
                            onNavigateToLesson(
                                course.courseId,
                                course.nextLessonId
                            )
                        },
                        modifier = Modifier.staggeredEntry(index = 6, staggerDelay = 60)
                    )
                }

                // Quick Actions
                QuickActionsSection(
                    actions = state.quickActions,
                    onActionClick = { action ->
                        when (action.id) {
                            "tongue_twisters" -> onNavigateToTongueTwisters()
                            "weakest_skill" -> onNavigateToWeakestSkill()
                            "quick_warmup" -> onNavigateToQuickWarmup()
                            "recording_history" -> onNavigateToRecordingHistory()
                            else -> {}
                        }
                    },
                    modifier = Modifier.staggeredEntry(index = 7, staggerDelay = 60)
                )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String?,
    greeting: String,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Greeting text
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = greeting,
                    style = AppTypography.labelMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = userName ?: "Користувач",
                    style = AppTypography.displayLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )
            }

        // Settings button
        var settingsPressed by remember { mutableStateOf(false) }
        val settingsScale by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (settingsPressed) 0.78f else 1f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = 0.35f,
                stiffness = 300f
            ),
            label = "settingsScale"
        )
        val settingsRotation by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (settingsPressed) 45f else 0f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = 0.35f,
                stiffness = 300f
            ),
            label = "settingsRotation"
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    scaleX = settingsScale
                    scaleY = settingsScale
                    rotationZ = settingsRotation
                }
                .background(
                    Color.White.copy(alpha = 0.15f),
                    CircleShape
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            settingsPressed = true
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            tryAwaitRelease()
                            settingsPressed = false
                            onSettings()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚙️", fontSize = 18.sp)
        }
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    weekProgress: com.aivoicepower.domain.model.home.WeekProgress?,
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
                    text = if (currentStreak == 0) "Перший день" else "$currentStreak ${getDaysWord(currentStreak)}",
                    style = AppTypography.displayLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }
        }

        // Week circles
        if (weekProgress != null && weekProgress.days.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                val today = remember {
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(Calendar.getInstance().time)
                }

                weekProgress.days.forEach { day ->
                    DayCircle(
                        label = day.dayName,
                        filled = day.isCompleted,
                        isToday = day.date == today
                    )
                }
            }
        }
    }
}

private fun getDaysWord(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "день"
        count % 10 in 2..4 && (count % 100 < 10 || count % 100 >= 20) -> "дні"
        else -> "днів"
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
    tip: com.aivoicepower.domain.model.home.DailyTip,
    modifier: Modifier = Modifier
) {
    val isAuthorQuote = tip.title != "Порада дня"

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
                Text(
                    text = if (isAuthorQuote) "\uD83D\uDCAC" else "\uD83D\uDCA1",
                    fontSize = 32.sp
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!isAuthorQuote) {
                    Text(
                        text = "ПОРАДА ДНЯ",
                        style = AppTypography.labelMedium,
                        color = Color(0xFFD97706),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = if (isAuthorQuote) "\u00AB${tip.content}\u00BB" else tip.content,
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
                if (isAuthorQuote) {
                    Text(
                        text = "— ${tip.title}",
                        style = AppTypography.bodySmall,
                        color = Color(0xFFB45309),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyGoalCard(
    plan: com.aivoicepower.domain.model.home.TodayPlan,
    coachMessage: String? = null,
    onCoachClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val completedTasks = plan.activities.count { it.isCompleted }
    val totalTasks = plan.activities.size
    val remainingMinutes = plan.activities.filter { !it.isCompleted }.sumOf { it.estimatedMinutes }

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
            // Progress Ring з реальними даними
            ProgressRingSegmented(
                completedTasks = completedTasks,
                totalTasks = totalTasks
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
                        text = "$completedTasks з $totalTasks",
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
                        text = "~$remainingMinutes хв",
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
            plan.activities.forEach { activity ->
                TaskRow(
                    title = activity.title,
                    meta = buildString {
                        append("${activity.estimatedMinutes} хв")
                        activity.subtitle?.let { append(" • $it") }
                        if (activity.isCompleted) append(" • Завершено")
                    },
                    completed = activity.isCompleted
                )
            }
        }

        // Coach message
        coachMessage?.let { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                Color(0xFF6366F1).copy(alpha = 0.12f)
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF8B5CF6).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onCoachClick() })
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "\uD83C\uDFCB\uFE0F", fontSize = 18.sp)
                Text(
                    text = message,
                    color = Color(0xFF5B21B6),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
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
    val view = LocalView.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (completed) Color(0xFFF5F5F7).copy(alpha = 0.5f)
                else Color(0xFFF5F5F7),
                RoundedCornerShape(12.dp)
            )
            .scaleOnPress(pressedScale = 0.98f)
            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) }
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
    skills: List<com.aivoicepower.domain.model.home.Skill>,
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
            skills.forEach { skill ->
                SkillCard(skill = skill)
            }
        }
    }
}

@Composable
private fun SkillCard(
    skill: com.aivoicepower.domain.model.home.Skill,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    // Cache parsed colors — avoid re-parsing hex on every recomposition
    val gradientColors = remember(skill.gradientColors) {
        skill.gradientColors.map { hexColor ->
            Color(android.graphics.Color.parseColor(hexColor))
        }
    }

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
            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) }
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
            Text(text = skill.emoji, fontSize = 28.sp)
        }

        Text(
            text = skill.name,
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
                    .fillMaxWidth(skill.percentage / 100f)
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
            text = "${skill.percentage}% • ↗ ${skill.growth}",
            style = AppTypography.bodySmall,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        if (skill.statusLabel.isNotEmpty()) {
            Text(
                text = skill.statusLabel,
                style = AppTypography.labelSmall,
                color = com.aivoicepower.utils.SkillLevelUtils.getSkillLabelColor(skill.percentage),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ContinueCourseSection(
    course: com.aivoicepower.domain.model.home.CurrentCourse,
    onCourseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Продовжити навчання",
            style = AppTypography.headlineMedium,
            color = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .background(Color.White, RoundedCornerShape(24.dp))
                .clickable(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onCourseClick() })
                .padding(0.dp)
        ) {
            // Header з градієнтом курсу
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(android.graphics.Color.parseColor(course.color)),
                                Color(android.graphics.Color.parseColor(course.color)).copy(alpha = 0.85f)
                            )
                        ),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                // Легкий темний градієнт зверху
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                )

                // Іконка курсу внизу справа
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = course.icon,
                        fontSize = 80.sp,
                        color = Color.White.copy(alpha = 0.80f)
                    )
                }

                // Назва курсу у верхньому лівому куті з 3D ефектом
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    // Глибока тінь для 3D ефекту (темний шар знизу)
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color.Black.copy(alpha = 0.35f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp,
                        modifier = Modifier.offset(x = 3.dp, y = 3.dp)
                    )

                    // Середній шар для глибини (світліша тінь)
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color.Black.copy(alpha = 0.15f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp,
                        modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                    )

                    // Основний текст з легким відтінком кольору курсу
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color.White.copy(alpha = 0.98f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp
                    )

                    // Кольоровий відтінок поверх основного тексту
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color(android.graphics.Color.parseColor(course.color)).copy(alpha = 0.12f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp
                    )

                    // Світловий highlight зверху для 3D глибини
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp,
                        modifier = Modifier.offset(x = (-1).dp, y = (-1).dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Урок N • N/NN
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Урок ${course.nextLessonNumber}",
                        style = AppTypography.titleMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "•",
                        color = TextColors.onLightSecondary,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "${course.nextLessonNumber}/${course.totalLessons}",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Назва уроку (велика, до 2 рядків)
                Text(
                    text = course.nextLessonTitle,
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 26.sp,
                    maxLines = 2,
                    letterSpacing = (-0.5).sp
                )

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(course.nextLessonNumber.toFloat() / course.totalLessons)
                            .height(8.dp)
                            .background(
                                Color(android.graphics.Color.parseColor(course.color)),
                                RoundedCornerShape(4.dp)
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
    val view = LocalView.current
    // Map action IDs to gradient colors
    val gradientColors = when (action.id) {
        "tongue_twisters" -> listOf(Color(0xFFC0C0C0), Color(0xFFE8E8E8)) // Сріблястий градієнт
        "weakest_skill" -> listOf(Color(0xFF8B5CF6), Color(0xFFA855F7)) // Фіолетовий градієнт
        "quick_warmup" -> listOf(Color(0xFF06B6D4), Color(0xFF0891B2)) // Бірюзово-синій градієнт
        "recording_history" -> listOf(Color(0xFF10B981), Color(0xFF14B8A6)) // Зелено-блакитний
        else -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp) // Збільшена висота для повного відображення назв
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .scaleOnPress(pressedScale = 0.95f)
            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onClick() }
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
            Text(
                text = action.icon,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = action.title,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}
