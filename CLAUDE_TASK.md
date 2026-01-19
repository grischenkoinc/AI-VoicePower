Оновлення LessonScreen компонентів під преміум дизайн. Взяти візуальне оформлення зі зразка ui/screens/lesson/LessonScreen.kt і застосувати до реальних компонентів ui/screens/courses/components/. ВАЖЛИВО: Зберегти ВСЮ структуру, логіку, параметри, onEvent виклики — міняти ТІЛЬКИ візуал! Оновити TheoryPhaseContent.kt, ExercisePhaseContent.kt, CompletedPhaseContent.kt, ExerciseCard.kt. Код для TheoryPhaseContent.kt:
```kotlin
package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.components.*

@Composable
fun TheoryPhaseContent(
    lesson: Lesson,
    onStartExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    GradientBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theory Card з premium дизайном
            lesson.theory?.let { theory ->
                MainCard(
                    header = {
                        SectionTag(
                            emoji = "📖",
                            text = "Теорія",
                            isPractice = false
                        )
                        
                        BigTitle(text = lesson.title)
                        
                        LevelPill(
                            emoji = "📚",
                            level = lesson.dayNumber
                        )
                    },
                    content = {
                        ContentText(
                            text = theory.text
                        )
                        
                        // Поради як numbered tips
                        if (theory.tips.isNotEmpty()) {
                            HighlightBox(
                                title = "💡 Поради",
                                content = "" // Empty content, tips below
                            )
                            
                            NumberedTips(tips = theory.tips)
                        }
                    }
                )
            }
            
            // Exercises Preview Card
            MainCard(
                header = {
                    SectionTag(
                        emoji = "🔥",
                        text = "Вправи • ${lesson.exercises.size}",
                        isPractice = true
                    )
                    
                    BigTitle(text = "План тренування")
                },
                content = {
                    ContentText(
                        title = "Тривалість",
                        text = "~${lesson.estimatedMinutes} хвилин практики"
                    )
                    
                    // Exercise list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lesson.exercises.forEachIndexed { index, exercise ->
                            ContentText(
                                text = "${index + 1}. ${exercise.title}"
                            )
                        }
                    }
                }
            )
            
            // Start Button
            PrimaryButton(
                text = "Почати вправи",
                onClick = onStartExercises,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

Код для ExercisePhaseContent.kt:
```kotlin
package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.screens.courses.LessonEvent
import com.aivoicepower.ui.theme.components.*

@Composable
fun ExercisePhaseContent(
    lesson: Lesson,
    currentExerciseIndex: Int,
    exerciseState: ExerciseState?,
    totalExercises: Int,
    isPlaying: Boolean,
    onEvent: (LessonEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (exerciseState == null) return
    
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                // Fixed Progress Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProgressBar3D(
                        progress = (currentExerciseIndex + 1) / totalExercises.toFloat(),
                        currentStep = currentExerciseIndex + 1,
                        totalSteps = totalExercises,
                        stepLabel = "Вправа"
                    )
                }
                
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Exercise Card з premium дизайном
                    PracticeCard(
                        header = {
                            SectionTag(
                                emoji = getExerciseEmoji(exerciseState.exercise.type),
                                text = "${currentExerciseIndex + 1}/$totalExercises",
                                isPractice = true
                            )
                            
                            BigTitle(text = exerciseState.exercise.title)
                        },
                        content = {
                            // Exercise Card (окремий компонент)
                            ExerciseCard(exerciseState = exerciseState)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Recording Controls
                            RecordingControls(
                                exerciseState = exerciseState,
                                isPlaying = isPlaying,
                                onStartRecording = { onEvent(LessonEvent.StartRecordingClicked) },
                                onStopRecording = { onEvent(LessonEvent.StopRecordingClicked) },
                                onPlayRecording = { onEvent(LessonEvent.PlayRecordingClicked) },
                                onStopPlayback = { onEvent(LessonEvent.StopPlaybackClicked) },
                                onReRecord = { onEvent(LessonEvent.ReRecordClicked) },
                                onComplete = { onEvent(LessonEvent.CompleteExerciseClicked) }
                            )
                        }
                    )
                    
                    // Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentExerciseIndex > 0) {
                            SecondaryButton(
                                text = "Попередня",
                                onClick = { onEvent(LessonEvent.PreviousExerciseClicked) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        SecondaryButton(
                            text = "Пропустити",
                            onClick = { onEvent(LessonEvent.SkipExerciseClicked) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// Helper для емодзі типів вправ
private fun getExerciseEmoji(type: com.aivoicepower.domain.model.exercise.ExerciseType): String {
    return when (type) {
        com.aivoicepower.domain.model.exercise.ExerciseType.ARTICULATION -> "🗣️"
        com.aivoicepower.domain.model.exercise.ExerciseType.TONGUE_TWISTER -> "🔥"
        com.aivoicepower.domain.model.exercise.ExerciseType.READING -> "📖"
        com.aivoicepower.domain.model.exercise.ExerciseType.EMOTION_READING -> "🎭"
        com.aivoicepower.domain.model.exercise.ExerciseType.FREE_SPEECH -> "💬"
        com.aivoicepower.domain.model.exercise.ExerciseType.RETELLING -> "📝"
        com.aivoicepower.domain.model.exercise.ExerciseType.DIALOGUE -> "💭"
        com.aivoicepower.domain.model.exercise.ExerciseType.PITCH -> "🎵"
        com.aivoicepower.domain.model.exercise.ExerciseType.QA -> "❓"
        com.aivoicepower.domain.model.exercise.ExerciseType.TONGUE_TWISTER_BATTLE -> "⚔️"
        com.aivoicepower.domain.model.exercise.ExerciseType.MINIMAL_PAIRS -> "👂"
        com.aivoicepower.domain.model.exercise.ExerciseType.CONTRAST_SOUNDS -> "🔊"
        com.aivoicepower.domain.model.exercise.ExerciseType.SLOW_MOTION -> "🐌"
        com.aivoicepower.domain.model.exercise.ExerciseType.BREATHING -> "🌬️"
    }
}
```

Код для CompletedPhaseContent.kt:
```kotlin
package com.aivoicepower.ui.screens.courses.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*

@Composable
fun CompletedPhaseContent(
    lesson: Lesson,
    nextLesson: Lesson? = null,
    onFinish: () -> Unit,
    onNextLesson: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Celebration animation
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    GradientBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Celebration Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .shadow(
                        elevation = 24.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.5f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎉", fontSize = 64.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Урок пройдено!",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-1.5).sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "День ${lesson.dayNumber}: ${lesson.title}",
                style = AppTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Stats Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .background(
                        Color.White.copy(alpha = 0.95f),
                        RoundedCornerShape(32.dp)
                    )
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Чудова робота!",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Text(
                    text = "Ти виконав ${lesson.exercises.size} вправи. Прогрес збережено.",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (nextLesson != null && onNextLesson != null) {
                    PrimaryButton(
                        text = "До наступного уроку: ${nextLesson.title}",
                        onClick = onNextLesson,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                SecondaryButton(
                    text = "Повернутися до курсу",
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
```

Код для ExerciseCard.kt — оновити емодзі та стиль:
```kotlin
package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.ExerciseContentDisplay

@Composable
fun ExerciseCard(
    exerciseState: ExerciseState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Instruction Box
        if (exerciseState.exercise.instruction.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                    )
                    .background(Color(0xFFFFFBEB), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "💡", fontSize = 20.sp)
                    Text(
                        text = "Інструкція",
                        style = AppTypography.titleSmall,
                        color = Color(0xFF92400E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = exerciseState.exercise.instruction,
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Exercise Content
        ExerciseContentDisplay(
            content = exerciseState.exercise.content
        )
        
        // Duration
        if (exerciseState.exercise.durationSeconds > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⏱️", fontSize = 18.sp)
                Text(
                    text = "Тривалість: ${exerciseState.exercise.durationSeconds} сек",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
```

Компіляція: ./gradlew clean assembleDebug && adb uninstall com.aivoicepower && ./gradlew installDebug. Що оновлено: TheoryPhaseContent з GradientBackground + MainCard + NumberedTips + HighlightBox, ExercisePhaseContent з ProgressBar3D + PracticeCard + premium навігація, CompletedPhaseContent з celebration анімацією + glass card + gradient buttons, ExerciseCard з жовтим instruction box + правильними емодзі для всіх 14 типів вправ. ВСЯ логіка, параметри, onEvent збережені — змінено ТІЛЬКИ візуал!