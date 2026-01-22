Закріпити header "Урок X" в TheoryPhaseContent. Header має бути fixed поверх контенту, gradient фон має скролитись під ним БЕЗ проміжку. Код:
```kotlin
@Composable
fun TheoryPhaseContent(
    lesson: Lesson,
    onStartExercises: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
        // GradientBackground з контентом (все скролиться)
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Spacer для висоти header (щоб контент не перекривався)
                Spacer(modifier = Modifier.height(88.dp))
                
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Theory Card
                    lesson.theory?.let { theory ->
                        MainCard(
                            header = {
                                SectionTag(
                                    emoji = "📖",
                                    text = "Теорія",
                                    isPractice = false
                                )
                                
                                BigTitle(text = lesson.title)
                            },
                            content = {
                                ContentText(text = theory.text)
                                
                                if (theory.tips.isNotEmpty()) {
                                    NumberedTips(tips = theory.tips)
                                }
                            }
                        )
                    }
                    
                    // Navigation
                    BottomNavRow(
                        onPrevious = onNavigateBack,
                        onNext = onStartExercises
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        
        // Fixed Header ПОВЕРХ (z-index вище через порядок у Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Урок ${lesson.dayNumber}: ${lesson.title}",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }
    }
}
```

Компіляція: ./gradlew assembleDebug && adb uninstall com.aivoicepower && ./gradlew installDebug. Що зроблено: Header "Урок X" закріплений через align(Alignment.TopCenter) поверх GradientBackground, Spacer(88.dp) зсуває контент щоб не перекривався, gradient фон скролиться ПІД header без проміжку, header має RoundedCornerShape знизу. Як на скріні!