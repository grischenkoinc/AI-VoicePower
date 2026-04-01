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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.home.ActivityType
import com.aivoicepower.domain.model.home.CurrentCourse
import com.aivoicepower.domain.model.home.DailyTip
import com.aivoicepower.domain.model.home.PlanActivity
import com.aivoicepower.domain.model.home.QuickAction
import com.aivoicepower.domain.model.home.Skill
import com.aivoicepower.domain.model.home.TodayPlan
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import com.aivoicepower.BuildConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
    onNavigateToPromptIteration: (() -> Unit)? = null,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header
            HomeHeader(
                userName = state.userName,
                greeting = state.greeting,
                currentStreak = state.currentStreak,
                onSettings = onNavigateToSettings,
                modifier = Modifier.staggeredEntry(index = 0, staggerDelay = 60)
            )

            // DEBUG: Prompt iteration button (debug builds only)
            if (BuildConfig.DEBUG && onNavigateToPromptIteration != null) {
                androidx.compose.material3.Button(
                    onClick = onNavigateToPromptIteration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🔧 Ітерація промптів", fontSize = 13.sp, color = Color.White)
                }
            }

            // 2. Daily Goal
            state.todayPlan?.let { plan ->
                DailyGoalCard(
                    plan = plan,
                    coachMessage = state.coachMessage,
                    onCoachClick = onNavigateToAICoach,
                    onActivityClick = { activity ->
                        when (activity.type) {
                            ActivityType.WARMUP -> onNavigateToQuickWarmup()
                            ActivityType.LESSON -> {
                                // Route: "course/{courseId}/lesson/{lessonId}"
                                val parts = activity.navigationRoute.split("/")
                                if (parts.size >= 4) {
                                    onNavigateToLesson(parts[1], parts[3])
                                }
                            }
                            ActivityType.IMPROVISATION -> onNavigateToImprovisation()
                            else -> {}
                        }
                    },
                    modifier = Modifier.staggeredEntry(index = 1, staggerDelay = 60)
                )
            }

            // 3. Tip of the Day
            state.dailyTip?.let { tip ->
                MotivationCard(
                    tip = tip,
                    modifier = Modifier.staggeredEntry(index = 2, staggerDelay = 60)
                )
            }

            // 4. Daily Limits (free users only)
            if (!state.isPremium) {
                com.aivoicepower.ui.screens.home.components.DailyLimitsCard(
                    remainingAnalyses = state.remainingAnalyses,
                    maxAnalyses = state.maxFreeAnalyses,
                    remainingImprovAnalyses = state.remainingImprovAnalyses,
                    maxImprovAnalyses = state.maxFreeImprovAnalyses,
                    remainingAiMessages = state.remainingAiMessages,
                    maxAiMessages = state.maxFreeAiMessages,
                    modifier = Modifier.staggeredEntry(index = 3, staggerDelay = 60)
                )
            }

            // 5. Continue Course (unchanged)
            state.currentCourse?.let { course ->
                ContinueCourseSection(
                    course = course,
                    onCourseClick = {
                        onNavigateToLesson(course.courseId, course.nextLessonId)
                    },
                    modifier = Modifier.staggeredEntry(index = 4, staggerDelay = 60)
                )
            }

            // 6. Skills
            SkillsSection(
                skills = state.skills,
                onDetailClick = onNavigateToAnalytics,
                modifier = Modifier.staggeredEntry(index = 5, staggerDelay = 60)
            )

            // 7. Quick Actions (unchanged)
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
                modifier = Modifier.staggeredEntry(index = 6, staggerDelay = 60)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ======================== 1. HEADER ========================

@Composable
private fun HomeHeader(
    userName: String?,
    greeting: String,
    currentStreak: Int,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = greeting,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = userName ?: "Користувач",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak badge
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFFF6B35).copy(alpha = 0.18f), Color(0xFFFF4500).copy(alpha = 0.12f))),
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, Color(0xFFFF6B35).copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 14.sp)
                    Text(
                        text = "$currentStreak",
                        color = Color(0xFFFBBF24),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Settings button with 3D effect
            var settingsPressed by remember { mutableStateOf(false) }
            val settingsScale by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (settingsPressed) 0.82f else 1f,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.35f,
                    stiffness = 300f
                ),
                label = "settingsScale"
            )
            val settingsRotation by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (settingsPressed) 90f else 0f,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.4f,
                    stiffness = 200f
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
                        RoundedCornerShape(12.dp)
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
                Text(text = "⚙️", fontSize = 22.sp)
            }
        }
    }
}

// ======================== 2. DAILY GOAL CARD ========================

private enum class TaskStatus { DONE, ACTIVE, PENDING }

@Composable
private fun DailyGoalCard(
    plan: TodayPlan,
    coachMessage: String?,
    onCoachClick: () -> Unit,
    onActivityClick: (PlanActivity) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val completedTasks = plan.activities.count { it.isCompleted }
    val totalTasks = plan.activities.size
    val remainingMinutes = plan.activities.filter { !it.isCompleted }.sumOf { it.estimatedMinutes }
    val activeIndex = plan.activities.indexOfFirst { !it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        // ---- Gradient Header ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ПЛАН НА СЬОГОДНІ",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Денна ціль",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "$completedTasks з $totalTasks виконано · ~$remainingMinutes хв залишилось",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }

                ProgressRingSegmented(
                    completedTasks = completedTasks,
                    totalTasks = totalTasks,
                    ringSize = 52,
                    completedColor = Color(0xFF22C55E),
                    emptyColor = Color.White.copy(alpha = 0.2f),
                    textColor = Color.White
                )
            }
        }

        // ---- White Body (Timeline + Coach) ----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            // Timeline tasks
            plan.activities.forEachIndexed { index, activity ->
                val status = when {
                    activity.isCompleted -> TaskStatus.DONE
                    index == activeIndex -> TaskStatus.ACTIVE
                    else -> TaskStatus.PENDING
                }
                TimelineTaskRow(
                    activity = activity,
                    status = status,
                    isLast = index == plan.activities.lastIndex,
                    onActionClick = { onActivityClick(activity) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Coach
            coachMessage?.let { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAFAFC))
                        .drawBehind {
                            drawRect(
                                color = Color(0xFF8B5CF6),
                                topLeft = Offset(0f, 0f),
                                size = Size(4.dp.toPx(), size.height)
                            )
                        }
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onCoachClick()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp),
                                spotColor = Color(0xFF8B5CF6).copy(alpha = 0.3f)
                            )
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 18.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "AI-КОУЧ",
                            color = Color(0xFF8B5CF6),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message,
                            color = Color(0xFF4B5563),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            maxLines = 4
                        )
                    }

                    Text(
                        text = "›",
                        color = Color(0xFFC4B5FD),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineTaskRow(
    activity: PlanActivity,
    status: TaskStatus,
    isLast: Boolean,
    onActionClick: () -> Unit
) {
    val view = LocalView.current

    val lineColor = when (status) {
        TaskStatus.DONE -> Color(0xFF22C55E)
        TaskStatus.ACTIVE -> Color(0xFF667EEA)
        TaskStatus.PENDING -> Color(0xFFE5E7EB)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Left: icon + vertical line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = when (status) {
                            TaskStatus.DONE -> Color(0xFF22C55E).copy(alpha = 0.4f)
                            TaskStatus.ACTIVE -> Color(0xFF667EEA).copy(alpha = 0.4f)
                            TaskStatus.PENDING -> Color.Black.copy(alpha = 0.05f)
                        }
                    )
                    .background(
                        when (status) {
                            TaskStatus.DONE -> Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF16A34A)))
                            TaskStatus.ACTIVE -> Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
                            TaskStatus.PENDING -> Brush.linearGradient(listOf(Color(0xFFF1F3F5), Color(0xFFF1F3F5)))
                        },
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    TaskStatus.DONE -> Text("✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    TaskStatus.ACTIVE -> Text(
                        when (activity.type) {
                            ActivityType.WARMUP -> "🏃"
                            ActivityType.LESSON -> "📖"
                            ActivityType.IMPROVISATION -> "🎭"
                            else -> "📖"
                        },
                        fontSize = 16.sp
                    )
                    TaskStatus.PENDING -> Text("🕐", fontSize = 14.sp)
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .weight(1f)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Right: content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 20.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = activity.title,
                color = when (status) {
                    TaskStatus.DONE -> Color(0xFFB0B7C3)
                    TaskStatus.ACTIVE -> Color(0xFF111827)
                    TaskStatus.PENDING -> Color(0xFFB0B7C3)
                },
                fontSize = 15.sp,
                fontWeight = if (status == TaskStatus.ACTIVE) FontWeight.Bold else FontWeight.SemiBold,
                textDecoration = if (status == TaskStatus.DONE) TextDecoration.LineThrough else TextDecoration.None
            )

            Text(
                text = when (status) {
                    TaskStatus.DONE -> "${activity.estimatedMinutes} хв · Завершено"
                    TaskStatus.ACTIVE -> "${activity.estimatedMinutes} хв · ${activity.subtitle ?: ""}"
                    TaskStatus.PENDING -> "${activity.estimatedMinutes} хв · ${activity.subtitle ?: ""}"
                },
                color = when (status) {
                    TaskStatus.DONE -> Color(0xFFD1D5DB)
                    TaskStatus.ACTIVE -> Color(0xFF667EEA)
                    TaskStatus.PENDING -> Color(0xFFD4D9E1)
                },
                fontSize = 12.sp,
                fontWeight = if (status == TaskStatus.ACTIVE) FontWeight.SemiBold else FontWeight.Medium
            )

            // "Розпочати" button (only on active task)
            if (status == TaskStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        )
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onActionClick()
                        }
                        .padding(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Розпочати",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(8.dp, 10.dp)) {
                                val path = Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(size.width, size.height / 2)
                                    lineTo(0f, size.height)
                                    close()
                                }
                                drawPath(path, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressRingSegmented(
    completedTasks: Int,
    totalTasks: Int,
    ringSize: Int = 80,
    completedColor: Color = Color(0xFF667EEA),
    emptyColor: Color = Color(0xFFE5E5EA),
    textColor: Color = Color(0xFF667EEA),
    modifier: Modifier = Modifier
) {
    val sizeDp = ringSize.dp
    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val strokeWidth = (ringSize * 0.1f).dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            drawCircle(
                color = emptyColor,
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
            )

            if (totalTasks > 0) {
                val segmentAngle = 360f / totalTasks
                val gapAngle = 8f

                for (i in 0 until totalTasks) {
                    val startAngle = -90f + i * segmentAngle + gapAngle / 2
                    val sweepAngle = segmentAngle - gapAngle
                    val segmentColor = if (i < completedTasks) completedColor else emptyColor

                    drawArc(
                        color = segmentColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Text(
            text = "$completedTasks/$totalTasks",
            color = textColor,
            fontSize = (ringSize * 0.225f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

// ======================== 3. MOTIVATION CARD ========================

@Composable
private fun MotivationCard(
    tip: DailyTip,
    modifier: Modifier = Modifier
) {
    val isAuthorQuote = tip.title != "Порада дня"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF8B5CF6).copy(alpha = 0.10f), Color(0xFF6366F1).copy(alpha = 0.06f))
                ),
                RoundedCornerShape(16.dp)
            )
            .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                )
                .background(
                    Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isAuthorQuote) "💬" else "💡",
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (isAuthorQuote) "ЦИТАТА" else "ПОРАДА ДНЯ",
                color = Color(0xFFFBBF24),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = if (isAuthorQuote) "«${tip.content}»" else tip.content,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
            if (isAuthorQuote) {
                Text(
                    text = "— ${tip.title}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ======================== 6. SKILLS SECTION ========================

private val skillDotColors = listOf(
    Color(0xFF667EEA),
    Color(0xFF06B6D4),
    Color(0xFFEC4899),
    Color(0xFFF59E0B),
    Color(0xFF22C55E)
)

@Composable
private fun SkillsSection(
    skills: List<Skill>,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.2f)
            )
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E1B4B).copy(alpha = 0.85f), Color(0xFF312E81).copy(alpha = 0.75f))
                ),
                RoundedCornerShape(20.dp)
            )
            .border(1.dp, Color(0xFF667EEA).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Навички",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            var detailPressed by remember { mutableStateOf(false) }
            val detailScale by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (detailPressed) 0.88f else 1f,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.4f,
                    stiffness = 400f
                ),
                label = "detailScale"
            )
            Text(
                text = "Детальніше →",
                color = Color.White.copy(alpha = if (detailPressed) 0.8f else 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = detailScale
                        scaleY = detailScale
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                detailPressed = true
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                tryAwaitRelease()
                                detailPressed = false
                                onDetailClick()
                            }
                        )
                    }
            )
        }

        if (skills.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadarChart(
                    skills = skills,
                    modifier = Modifier.size(140.dp, 130.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skills.forEachIndexed { index, skill ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        skillDotColors.getOrElse(index) { Color.White },
                                        CircleShape
                                    )
                            )
                            Text(
                                text = skill.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${skill.percentage}%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = skill.growth,
                                color = Color(0xFF22C55E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadarChart(
    skills: List<Skill>,
    modifier: Modifier = Modifier
) {
    val n = skills.size
    if (n < 3) return

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val maxRadius = minOf(cx, cy) - 12.dp.toPx()

        val angles = (0 until n).map { i ->
            -PI / 2 + 2 * PI * i / n
        }

        // Grid levels (3)
        for (level in 1..3) {
            val r = maxRadius * level / 3
            val gridPath = Path()
            for (i in 0 until n) {
                val x = cx + (r * cos(angles[i])).toFloat()
                val y = cy + (r * sin(angles[i])).toFloat()
                if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
            }
            gridPath.close()
            drawPath(
                gridPath,
                color = Color.White.copy(alpha = 0.04f + 0.02f * level),
                style = Stroke(1.dp.toPx())
            )
        }

        // Axes
        for (i in 0 until n) {
            val x = cx + (maxRadius * cos(angles[i])).toFloat()
            val y = cy + (maxRadius * sin(angles[i])).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(cx, cy),
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Data polygon
        val dataPoints = skills.mapIndexed { i, skill ->
            val r = maxRadius * (skill.percentage / 100f).coerceIn(0.05f, 1f)
            Offset(
                cx + (r * cos(angles[i])).toFloat(),
                cy + (r * sin(angles[i])).toFloat()
            )
        }
        val dataPath = Path()
        dataPoints.forEachIndexed { i, point ->
            if (i == 0) dataPath.moveTo(point.x, point.y) else dataPath.lineTo(point.x, point.y)
        }
        dataPath.close()

        drawPath(
            dataPath,
            brush = Brush.linearGradient(
                listOf(
                    Color(0xFF667EEA).copy(alpha = 0.3f),
                    Color(0xFF764BA2).copy(alpha = 0.12f)
                )
            )
        )
        drawPath(
            dataPath,
            color = Color(0xFF667EEA),
            style = Stroke(1.5.dp.toPx())
        )

        // Data points
        val colors = listOf(
            Color(0xFF667EEA),
            Color(0xFF06B6D4),
            Color(0xFFEC4899),
            Color(0xFFF59E0B),
            Color(0xFF22C55E)
        )
        dataPoints.forEachIndexed { i, point ->
            val dotColor = colors.getOrElse(i) { Color.White }
            drawCircle(Color.White, radius = 4.5.dp.toPx(), center = point)
            drawCircle(dotColor, radius = 3.dp.toPx(), center = point)
        }
    }
}

// ======================== 5. CONTINUE COURSE (UNCHANGED) ========================

@Composable
private fun ContinueCourseSection(
    course: CurrentCourse,
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

                // Play button overlay (below course name)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(22.dp, 26.dp)) {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, size.height / 2)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(path, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.TopStart
                ) {
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
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color.White.copy(alpha = 0.98f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp
                    )
                    Text(
                        text = course.courseName,
                        style = AppTypography.titleLarge,
                        color = Color(android.graphics.Color.parseColor(course.color)).copy(alpha = 0.12f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 38.sp
                    )
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

// ======================== 7. QUICK ACTIONS (UNCHANGED) ========================

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
    val gradientColors = when (action.id) {
        "tongue_twisters" -> listOf(Color(0xFFC0C0C0), Color(0xFFE8E8E8))
        "weakest_skill" -> listOf(Color(0xFF8B5CF6), Color(0xFFA855F7))
        "quick_warmup" -> listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
        "recording_history" -> listOf(Color(0xFF10B981), Color(0xFF14B8A6))
        else -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)
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
