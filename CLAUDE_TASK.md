Круглий Radar Chart + виправлення EmotionalTextPrompt скрізь. Оновити DiagnosticResultScreen.kt — замінити шестикутник на круглі рівні (концентричні кола), залишити 6 осей. Оновити DiagnosticScreen.kt — знайти ВСІ місця де використовується EmotionalTextPrompt і встановити heightIn max = 270dp (було 180dp), змінити зелений колір на темніший. Код для DiagnosticResultScreen.kt:
```kotlin
@Composable
private fun RadarChart(
    metrics: List<RadarMetric>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas з КРУГЛИМ радаром
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.85f
            val angleStep = 360f / metrics.size
            
            // Background CIRCLES замість шестикутників
            for (i in 1..5) {
                val circleRadius = radius * i / 5
                drawCircle(
                    color = Color(0xFFE5E7EB),
                    radius = circleRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }
            
            // Axes (6 ліній до країв)
            metrics.forEachIndexed { index, _ ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val endX = center.x + (radius * cos(angle)).toFloat()
                val endY = center.y + (radius * sin(angle)).toFloat()
                
                drawLine(
                    color = Color(0xFFE5E7EB),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }
            
            // Data polygon (з'єднує точки)
            val dataPath = Path()
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()
                
                if (index == 0) dataPath.moveTo(x, y)
                else dataPath.lineTo(x, y)
            }
            dataPath.close()
            
            drawPath(path = dataPath, color = Color(0xFF667EEA).copy(alpha = 0.3f))
            drawPath(path = dataPath, color = Color(0xFF667EEA), style = Stroke(width = 3f))
            
            // Points на осях
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()
                
                drawCircle(color = Color(0xFF667EEA), radius = 6f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 3f, center = Offset(x, y))
            }
        }
        
        // Мітки з компенсацією розміру
        val labelDistance = 118.dp
        val angleStep = 360f / metrics.size
        
        metrics.forEachIndexed { index, metric ->
            val angleDegrees = angleStep * index - 90
            val angleRadians = Math.toRadians(angleDegrees.toDouble())
            
            val baseOffsetX = (labelDistance.value * cos(angleRadians)).dp
            val baseOffsetY = (labelDistance.value * sin(angleRadians)).dp
            
            val labelWidth = if (metric.label == "Без паразитів") 75.dp else 65.dp
            val labelHeight = if (metric.label == "Без паразитів") 40.dp else 30.dp
            
            val correctionX = when {
                angleDegrees in -30f..30f -> 0.dp
                angleDegrees in 30f..90f -> -(labelWidth / 2)
                angleDegrees in 90f..150f -> -(labelWidth / 2)
                angleDegrees in 150f..210f -> 0.dp
                angleDegrees in 210f..270f -> (labelWidth / 2)
                else -> (labelWidth / 2)
            }
            
            val correctionY = when {
                angleDegrees in -90f..-30f -> (labelHeight / 2)
                angleDegrees in -30f..30f -> 0.dp
                angleDegrees in 30f..90f -> -(labelHeight / 2)
                angleDegrees in 90f..150f -> -(labelHeight / 2)
                angleDegrees in 150f..210f -> -(labelHeight / 2)
                else -> 0.dp
            }
            
            RadarLabel(
                label = metric.label,
                value = metric.value,
                isLong = metric.label == "Без паразитів",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = baseOffsetX + correctionX,
                        y = baseOffsetY + correctionY
                    )
            )
        }
    }
}

@Composable
private fun RadarLabel(
    label: String,
    value: Int,
    isLong: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(if (isLong) 75.dp else 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextColors.onLightSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = if (isLong) 2 else 1,
            lineHeight = if (isLong) 12.sp else 10.sp,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "$value",
            style = AppTypography.bodyMedium,
            color = Color(0xFF667EEA),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
```

Код для DiagnosticScreen.kt — виправити УСЮДИ:
```kotlin
// Знайти EmotionalTextPrompt composable і оновити:
@Composable
private fun EmotionalTextPrompt(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 270.dp) // ВИПРАВЛЕНО: було 180dp, +50%
            .background(Color(0xFFF8F9FA), RoundedCornerShape(20.dp))
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Читайте текст з відповідними емоціями:",
            style = AppTypography.labelSmall,
            color = TextColors.onLightSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LegendItem(color = Color(0xFF0D9668), label = "Радість") // ТЕМНІШИЙ зелений
            LegendItem(color = Color(0xFF6366F1), label = "Сум")
            LegendItem(color = Color(0xFFF59E0B), label = "Впевненість")
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF0D9668), fontWeight = FontWeight.Bold)) { // ТЕМНІШИЙ
                    append("Сьогодні чудовий день! Я радий бути тут і ділитися своїми думками. Кожна мить наповнена можливостями та новими відкриттями!")
                }
                append(" ")
                withStyle(SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                    append("Іноді буває важко, і це абсолютно нормально. Але я не здаюсь і продовжую йти вперед, навіть коли здається складно. Кожен крок — це досвід.")
                }
                append(" ")
                withStyle(SpanStyle(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)) {
                    append("Я впевнений у собі! Я знаю свої сильні сторони і вірю у власні можливості. Я досягну всіх своїх цілей, бо маю чітке бачення!")
                }
            },
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = TextColors.onLightSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ТАКОЖ знайти де EmotionalTextPrompt ВИКЛИКАЄТЬСЯ і перевірити:
// Має бути без параметра heightIn, бо він вже є в самому composable
```

Додати команду пошуку для перевірки:
```bash
# Знайти ВСІ виклики EmotionalTextPrompt
grep -n "EmotionalTextPrompt" app/src/main/java/com/aivoicepower/ui/screens/diagnostic/DiagnosticScreen.kt
```

Компіляція: ./gradlew clean assembleDebug && adb uninstall com.aivoicepower && ./gradlew installDebug. Що виправлено: Radar Chart тепер з КРУГЛИМИ рівнями (концентричні кола) замість шестикутника, 6 осей залишились, EmotionalTextPrompt heightIn = 270dp (+50%), зелений колір #0D9668 (темніший), перевірено ВСІ місця використання. Тепер має бути правильно!