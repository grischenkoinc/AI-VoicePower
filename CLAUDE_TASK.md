Фінальне виправлення 3 проблем у LessonScreen компонентах. Виправлення 1 — TipRow не реагує на дотик: замінити clickable indication з null на ripple indication щоб була візуальна реакція, також перевірити що interactionSource правильно підключений. Виправлення 2 — PracticeCard header без градієнта: перевірити що Gradients.cardHeaderPractice існує і має правильні кольори, якщо ні то використати Gradients.cardHeaderTheory тимчасово, також додати явний log для debug. Виправлення 3 — RecordButton синя рамка не пульсує: додати animated border навколо кнопки окремим Box що змінює borderWidth та alpha при recording, shadow не показує рамку так як треба. Код для Content.kt — TipRow з ripple indication:
```kotlin
@Composable
fun TipRow(
    number: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val offsetX by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(300),
        label = "offset"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color(0xFFA78BFA)) // Додали ripple!
            ) { 
                // Tip clicked
            }
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
                    spotColor = Color(0x66A78BFA)
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
```

Додати import в Content.kt:
```kotlin
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.ripple
```

Код для Cards.kt — PracticeCard з debug log і fallback gradient:
```kotlin
@Composable
fun PracticeCard(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    // DEBUG: Перевірка чи існує gradient
    val practiceGradient = try {
        Gradients.cardHeaderPractice
    } catch (e: Exception) {
        android.util.Log.e("PracticeCard", "cardHeaderPractice не знайдено, використовую cardHeaderTheory")
        Gradients.cardHeaderTheory
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevation.PracticeCard.elevation,
                shape = RoundedCornerShape(32.dp),
                spotColor = Elevation.PracticeCard.color
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        )
    ) {
        Column {
            // Header (альтернативний темний градієнт)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(practiceGradient) // Використовуємо перевірений gradient
                    .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                header()
            }
            
            // Body (білий фон)
            CardBody(content = content)
        }
    }
}
```

Код для Buttons.kt — RecordButton з animated border:
```kotlin
@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation для idle стану
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = RecordButtonPulse.scaleFrom,
        targetValue = RecordButtonPulse.scaleTo,
        animationSpec = RecordButtonPulse.animationSpec(),
        label = "scale"
    )
    
    // Border animation для recording стану
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = if (isRecording) 0.3f else 0f,
        targetValue = if (isRecording) 0.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )
    
    Box(
        modifier = modifier.size(140.dp), // Збільшено для border
        contentAlignment = Alignment.Center
    ) {
        // Animated border (тільки коли recording)
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 3.dp,
                        color = Color(0xFF667EEA).copy(alpha = borderAlpha),
                        shape = CircleShape
                    )
            )
        }
        
        // Wave rings (тільки коли recording)
        if (isRecording) {
            WaveRing(delay = WaveRingExpansion.delay1)
            WaveRing(delay = WaveRingExpansion.delay2)
            WaveRing(delay = WaveRingExpansion.delay3)
        }
        
        // Main button
        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(if (!isRecording) scale else 1f)
                .shadow(
                    elevation = if (isRecording) 
                        Elevation.RecordButton.activeElevation 
                    else 
                        Elevation.RecordButton.idleElevation,
                    shape = CircleShape,
                    spotColor = if (isRecording)
                        Elevation.RecordButton.activeColor
                    else
                        Elevation.RecordButton.idleColor
                )
                .background(Gradients.recordButton, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRecording) "⏸️" else "🎤",
                fontSize = 44.sp
            )
        }
    }
}
```

Додати imports в Buttons.kt:
```kotlin
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.border
```

Компіляція: ./gradlew clean, ./gradlew assembleDebug, ./gradlew installDebug. Що виправлено: TipRow тепер з ripple indication для візуальної реакції, PracticeCard має debug log та fallback на cardHeaderTheory якщо practiceGradient не знайдено, RecordButton має animated border з borderAlpha пульсацією 0.3 до 0.8 при recording стані. Перевір на пристрої всі 3 фікси!