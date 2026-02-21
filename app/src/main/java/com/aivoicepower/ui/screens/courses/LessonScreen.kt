package com.aivoicepower.ui.screens.courses

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.ui.components.AnalysisLimitBottomSheet
import com.aivoicepower.ui.components.AnalysisLimitInfo
import com.aivoicepower.audio.LocalSoundManager
import com.aivoicepower.audio.SoundEffect
import com.aivoicepower.ui.screens.courses.components.CompletedPhaseContent
import com.aivoicepower.ui.screens.courses.components.ExercisePhaseContent
import com.aivoicepower.ui.screens.courses.components.TheoryPhaseContent
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.utils.constants.FreeTierLimits
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("UNUSED_PARAMETER")
@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    viewModel: LessonViewModel = hiltViewModel(),
    rewardedAdManager: RewardedAdManager,
    onNavigateBack: () -> Unit,
    onNavigateToNextLesson: ((courseId: String, lessonId: String) -> Unit)? = null,
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    var hasAudioPermission by remember { mutableStateOf(false) }
    var backPressedTime by remember { mutableStateOf(0L) }
    // Show loading screen before the lesson starts (before Theory)
    var showLoadingScreen by remember { mutableStateOf(true) }

    // Auto-dismiss loading screen after delay
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.lesson != null) {
            delay(5500L)
            showLoadingScreen = false
        }
    }

    // Double-back to exit protection
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            // Actually go back
            onNavigateBack()
        } else {
            // Show toast
            backPressedTime = currentTime
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Для виходу натисніть Назад ще раз",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // Request permission ONLY when entering Exercise phase
    LaunchedEffect(state.currentPhase) {
        if (state.currentPhase == LessonPhase.Exercise && !hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Show toast message
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = androidx.compose.material3.SnackbarDuration.Long
                )
                viewModel.clearToastMessage()
            }
        }
    }

    // Analysis Limit Bottom Sheet
    if (state.showAnalysisLimitSheet) {
        AnalysisLimitBottomSheet(
            limitInfo = AnalysisLimitInfo(
                usedAnalyses = FreeTierLimits.FREE_ANALYSES_PER_DAY,
                maxFreeAnalyses = FreeTierLimits.FREE_ANALYSES_PER_DAY,
                remainingAdAnalyses = state.remainingAdAnalyses,
                isAdLoaded = state.isAdLoaded
            ),
            onWatchAd = {
                viewModel.onEvent(LessonEvent.WatchAdForAnalysis)
                if (activity != null) {
                    rewardedAdManager.showAd(
                        activity = activity,
                        onRewarded = {
                            viewModel.proceedWithAnalysisAfterAd()
                        },
                        onFailed = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                }
            },
            onPremium = {
                viewModel.onEvent(LessonEvent.DismissAnalysisLimitSheet)
                onNavigateToPremium()
            },
            onContinueWithout = {
                viewModel.onEvent(LessonEvent.ContinueWithoutAnalysis)
            },
            onDismiss = {
                viewModel.onEvent(LessonEvent.DismissAnalysisLimitSheet)
            }
        )
    }

    when {
        state.isLoading || showLoadingScreen -> {
            LessonLoadingScreen(
                lessonTitle = state.lesson?.title ?: "Урок",
                courseName = state.courseName.ifEmpty { "" },
                courseEmoji = state.courseIconEmoji,
                lessonDescription = state.lesson?.description ?: "",
                exerciseCount = state.lesson?.exercises?.size ?: 0,
                estimatedMinutes = state.lesson?.estimatedMinutes ?: 0,
            )
        }

        state.error != null -> {
            GradientBackground(
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error!!,
                            color = Color.White,
                            style = AppTypography.bodyLarge
                        )
                    }
                }
            )
        }

        state.lesson != null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = state.currentPhase,
                    transitionSpec = {
                        (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 3 })
                            .togetherWith(fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 3 })
                    },
                    label = "lessonPhaseTransition"
                ) { phase ->
                    when (phase) {
                        LessonPhase.Theory -> {
                            TheoryPhaseContent(
                                lesson = state.lesson!!,
                                onStartExercises = {
                                    viewModel.onEvent(LessonEvent.StartExercisesClicked)
                                },
                                onNavigateBack = onNavigateBack
                            )
                        }

                        LessonPhase.Exercise -> {
                            ExercisePhaseContent(
                                lesson = state.lesson!!,
                                currentExerciseIndex = state.currentExerciseIndex,
                                exerciseState = state.exerciseStates.getOrNull(state.currentExerciseIndex),
                                exerciseStates = state.exerciseStates,
                                totalExercises = state.exerciseStates.size,
                                isPlaying = state.isPlaying,
                                onEvent = viewModel::onEvent,
                                remainingAnalyses = if (!state.isPremium) state.remainingAnalyses else null,
                                onContinueAfterNoAnalysis = { viewModel.continueAfterNoAnalysis() }
                            )
                        }

                        LessonPhase.Completed -> {
                            val soundManager = LocalSoundManager.current
                            LaunchedEffect(Unit) {
                                soundManager.play(SoundEffect.LESSON_COMPLETED)
                            }
                            CompletedPhaseContent(
                                lesson = state.lesson!!,
                                nextLesson = state.nextLesson,
                                isLastLessonInCourse = state.isLastLessonInCourse,
                                courseName = state.courseName,
                                courseBadge = state.courseCompletionBadge,
                                onFinish = {
                                    viewModel.onEvent(LessonEvent.FinishLessonClicked)
                                    onNavigateBack()
                                },
                                onNextLesson = state.nextLesson?.let { nextLesson ->
                                    {
                                        viewModel.onEvent(LessonEvent.NextLessonClicked)
                                        onNavigateToNextLesson?.invoke(courseId, nextLesson.id)
                                    }
                                }
                            )
                        }
                    }
                }

                // Snackbar
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 100.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF667EEA),
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// === Lesson Loading Screen ===

private val lessonTips = listOf(
    "\uD83D\uDCA1 Говоріть чітко та впевнено — це головний ключ до успіху",
    "\uD83C\uDFAF Зосередьтесь на вимові кожного звуку, не поспішайте",
    "\uD83C\uDF1F Регулярна практика дає кращі результати, ніж довгі рідкі заняття",
    "\uD83D\uDE80 Записуйте себе і слухайте — це найшвидший шлях до покращення",
    "\uD83D\uDCAA Навіть 5 хвилин щоденної практики змінюють вашу мову",
    "\uD83C\uDFA4 Дихайте глибоко перед кожною вправою — це допоможе розслабитись",
    "\uD83E\uDDE0 AI аналізує вашу інтонацію, темп і чіткість вимови",
    "\uD83D\uDD25 Скоромовки — найефективніша вправа для дикції",
    "\uD83C\uDF3F Паузи у мовленні — це ознака впевненості, а не слабкості",
    "\uD83C\uDFBC Інтонація робить мову живою — експериментуйте з нею"
)

@Composable
private fun LessonLoadingScreen(
    lessonTitle: String,
    courseName: String,
    courseEmoji: String,
    lessonDescription: String,
    exerciseCount: Int,
    estimatedMinutes: Int
) {
    val tip = remember { lessonTips.random() }

    // Entry animations
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    val contentOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "contentOffset"
    )

    // Breathing glow
    val infiniteTransition = rememberInfiniteTransition(label = "loadGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffsetY
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.25f))

            // Course emoji with glow
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = glowScale
                            scaleY = glowScale
                            alpha = glowAlpha
                        }
                        .shadow(24.dp, CircleShape, spotColor = Color(0xFF667EEA))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667EEA).copy(alpha = 0.5f),
                                    Color(0xFF764BA2).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
                Text(text = courseEmoji, fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Course name label
            if (courseName.isNotBlank()) {
                Text(
                    text = courseName.uppercase(),
                    style = AppTypography.labelMedium,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Lesson title — maximum contrast
            Text(
                text = lessonTitle,
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.8).sp,
                lineHeight = 40.sp,
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Transparent
                    )
            )

            // Lesson description
            if (lessonDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = lessonDescription,
                    style = AppTypography.bodyLarge,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp,
                    maxLines = 3
                )
            }

            // Stats row
            if (exerciseCount > 0 || estimatedMinutes > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (exerciseCount > 0) {
                        StatChip(text = "\uD83D\uDCDD $exerciseCount вправ")
                    }
                    if (estimatedMinutes > 0) {
                        StatChip(text = "\u23F1 ~${estimatedMinutes} хв")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.35f))

            // Tip
            Text(
                text = "\uD83D\uDCA1 $tip",
                style = AppTypography.bodyMedium,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Smooth progress bar — fills from 0 to 1 over the full display time
            var progressTarget by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                progressTarget = 1f
            }
            val progressAnim by animateFloatAsState(
                targetValue = progressTarget,
                animationSpec = tween(
                    durationMillis = 5200, // slightly less than 5500ms display time
                    easing = FastOutSlowInEasing
                ),
                label = "loadProgress"
            )

            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White.copy(alpha = 0.7f),
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
