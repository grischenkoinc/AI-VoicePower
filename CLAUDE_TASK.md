Виправлення LessonScreen відповідно до Design_Example_react.md та фідбеку. Видаляємо TopStatusRow з основного контенту (він має бути в окремому шарі поверх скролу), фіксуємо ProgressBar вгорі, фіксуємо BottomNavRow внизу, виправляємо HighlightBox (left border замість рамки навколо), виправляємо PracticeCard header gradient, додаємо hover ефект до TipRow. Оновити ui/screens/lesson/LessonScreen.kt:
kotlinpackage com.aivoicepower.ui.screens.lesson

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivoicepower.ui.theme.AIVoicePowerTheme
import com.aivoicepower.ui.theme.components.*

@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (String) -> Unit,
    viewModel: LessonViewModel = viewModel()
) {
    var isRecording by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    GradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Content (scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 140.dp, bottom = 80.dp) // Space for fixed header + footer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Theory Card
                    MainCard(
                        header = {
                            SectionTag(
                                emoji = "📖",
                                text = "Теорія",
                                isPractice = false
                            )
                            
                            BigTitle(text = "Основи артикуляції")
                            
                            LevelPill(
                                emoji = "⚡",
                                level = 3
                            )
                        },
                        content = {
                            ContentText(
                                title = "Що таке артикуляція?",
                                text = "Артикуляція — це робота органів мовлення (губ, язика, щелеп) під час вимови звуків. Це основа чіткого мовлення."
                            )
                            
                            HighlightBox(
                                title = "💡 Ключовий інсайт",
                                content = "Чітка дикція = впевненість у спілкуванні"
                            )
                            
                            ContentText(
                                text = "Люди з гарною артикуляцією справляють враження компетентних професіоналів. Регулярні тренування приносять відчутний результат."
                            )
                            
                            NumberedTips(
                                tips = listOf(
                                    "Розтягни губи широко — зуби мають бути видно",
                                    "Витягни губи вперед трубочкою максимально",
                                    "Виконуй без пауз між повтореннями"
                                )
                            )
                        }
                    )
                    
                    // Practice Card
                    PracticeCard(
                        header = {
                            SectionTag(
                                emoji = "🔥",
                                text = "Практика • 1/5",
                                isPractice = true
                            )
                            
                            BigTitle(text = "Посмішка → Трубочка")
                        },
                        content = {
                            ExerciseVisual {
                                VisualRow(
                                    items = listOf(
                                        VisualItem(
                                            emoji = "😄",
                                            label = "Широка посмішка",
                                            time = "2 сек"
                                        ),
                                        VisualItem(
                                            emoji = "😗",
                                            label = "Губи трубочкою",
                                            time = "2 сек"
                                        )
                                    )
                                )
                                
                                VisualDivider()
                                
                                RepeatRow(repetitions = 10)
                            }
                            
                            // Record Section
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    RecordButton(
                                        isRecording = isRecording,
                                        onClick = { isRecording = !isRecording }
                                    )
                                    
                                    androidx.compose.material3.Text(
                                        text = if (isRecording) "Йде запис..." else "Натисни для запису",
                                        style = com.aivoicepower.ui.theme.AppTypography.bodyMedium,
                                        color = com.aivoicepower.ui.theme.TextColors.onLightMuted,
                                        fontSize = 14.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Fixed Progress Header (top)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProgressBar3D(
                    progress = 0.25f,
                    currentStep = 1,
                    totalSteps = 4,
                    stepLabel = "Теорія"
                )
            }
            
            // Fixed Bottom Navigation
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                BottomNavRow(
                    onPrevious = onNavigateBack,
                    onNext = { /* Navigate to next step */ }
                )
            }
        }
    }
}

@Composable
private fun BigTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Text(
        text = text,
        style = com.aivoicepower.ui.theme.AppTypography.displayLarge,
        color = com.aivoicepower.ui.theme.TextColors.onDarkPrimary,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1.5).sp,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun LessonScreenPreview() {
    AIVoicePowerTheme {
        LessonScreen(
            courseId = "course_1",
            lessonId = "lesson_1",
            onNavigateBack = {},
            onNavigateToResults = {}
        )
    }
}
Оновити ui/theme/components/Content.kt (виправити HighlightBox - left border замість рамки):
kotlin@Composable
fun HighlightBox(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        // Orange left border (3dp)
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(IntrinsicSize.Min)
                .background(Color(0xFFF59E0B))
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Gradients.highlightBox, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            
            Text(
                text = content,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}
Оновити ui/theme/components/Content.kt (додати clickable hover ефект до TipRow):
kotlin@Composable
fun TipRow(
    number: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { /* Tip clicked */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = if (isPressed) 8.dp else 4.dp,
                    shape = CircleShape,
                    spotColor = Color(0x40A78BFA)
                )
                .background(
                    brush = Gradients.tagPrimary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = AppTypography.labelMedium,
                color = TextColors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
Оновити imports в Content.kt (додати потрібні для TipRow):
kotlinimport androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.shadow
Компіляція та перевірка: ./gradlew clean, ./gradlew :app:compileDebugKotlin, ./gradlew assembleDebug, ./gradlew installDebug. Що виправлено: видалено TopStatusRow (1/4 та Урок 1 зникнули), ProgressBar тепер фіксований вгорі екрану (видно при скролі), BottomNavRow фіксований внизу екрану (не рухається при скролі), HighlightBox тепер з orange left border замість рамки навколо (як в Design_Example), PracticeCard використовує правильний gradient (cardHeaderPractice), TipRow тепер реагує на натискання з shadow pulse ефектом, main content тепер має padding для фіксованих елементів. Відкрий застосунок і перевір зміни!