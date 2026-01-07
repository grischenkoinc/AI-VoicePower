# AI VoicePower Design System

> Production-ready Design System based on Design Bible v1.0

## 📁 Структура

```
ui/theme/
├── Color.kt        — Всі кольори (backgrounds, primary, secondary, semantic, text, borders)
├── Type.kt         — Типографіка (шрифт, text styles)
├── Shape.kt        — Corner radius system
├── Spacing.kt      — Система відступів
├── Elevation.kt    — Elevation levels та shadows
├── Animation.kt    — Duration, easing curves
├── Gradient.kt     — Градієнти
├── Theme.kt        — Головна тема MaterialTheme
├── Modifiers.kt    — Custom extension functions
└── README.md       — Ця документація
```

## 🎨 Швидкий старт

### 1. Підключення теми

```kotlin
import com.aivoicepower.ui.theme.AIVoicePowerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIVoicePowerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
```

### 2. Використання кольорів

```kotlin
import com.aivoicepower.ui.theme.*

// Material 3 colors
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.primary
)

// Semantic colors (через extensions)
Icon(
    imageVector = Icons.Default.CheckCircle,
    tint = MaterialTheme.colorScheme.success
)

// Direct colors
Text(
    text = "Secondary text",
    color = TextColors.secondary
)
```

### 3. Використання типографіки

```kotlin
Text(
    text = "Заголовок",
    style = MaterialTheme.typography.headlineLarge
)

Text(
    text = "Основний текст",
    style = MaterialTheme.typography.bodyLarge
)
```

### 4. Використання spacing

```kotlin
Column(
    modifier = Modifier.padding(Spacing.md),
    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
) {
    // Content
}

// Або через shortcuts
Box(modifier = Modifier.screenPadding()) {
    // Content
}
```

### 5. Використання градієнтів

```kotlin
import com.aivoicepower.ui.theme.Gradients

Button(
    onClick = { },
    modifier = Modifier.gradientBackground(
        gradient = Gradients.primary,
        shape = RoundedCornerShape(CornerRadius.md)
    )
) {
    Text("Кнопка з градієнтом")
}
```

### 6. Glassmorphism

```kotlin
Box(
    modifier = Modifier
        .size(200.dp)
        .glassEffect(
            strength = GlassStrength.MEDIUM,
            shape = RoundedCornerShape(CornerRadius.lg)
        )
) {
    // Content
}
```

## 🎯 Основні компоненти

### Кольори

**Backgrounds:**
- `BackgroundColors.primary` — основний фон (#312E81)
- `BackgroundColors.surface` — картки (#4338CA)
- `BackgroundColors.surfaceElevated` — модалки (#5B52E0)

**Primary (Індиго):**
- `PrimaryColors.default` — основний (#6366F1)
- `PrimaryColors.dark` — hover (#4F46E5)

**Secondary (Помаранчевий):**
- `SecondaryColors.default` — CTA (#F97316)

**Semantic:**
- `SemanticColors.success` — успіх (#10B981)
- `SemanticColors.warning` — попередження (#F59E0B)
- `SemanticColors.error` — помилка (#EF4444)

### Типографіка

**Display** (великі заголовки):
- `displayLarge` — 32sp Bold

**Headline** (заголовки екранів):
- `headlineLarge` — 24sp SemiBold
- `headlineMedium` — 20sp SemiBold

**Title** (заголовки карток):
- `titleLarge` — 18sp Medium
- `titleMedium` — 16sp Medium

**Body** (основний текст):
- `bodyLarge` — 16sp Normal
- `bodyMedium` — 14sp Normal
- `bodySmall` — 12sp Normal

**Label** (кнопки, chips):
- `labelLarge` — 14sp Medium
- `labelMedium` — 12sp Medium

### Spacing

- `xxs` — 2dp (мінімальні)
- `xs` — 4dp (тісна група)
- `sm` — 8dp (стандартні малі)
- `md` — 16dp (стандартні) ⭐
- `lg` — 24dp (між секціями)
- `xl` — 32dp (великі)
- `xxl` — 48dp (екрани)

### Corner Radius

- `xs` — 4dp (tags)
- `sm` — 8dp (chips)
- `md` — 12dp (кнопки, inputs) ⭐
- `lg` — 16dp (картки) ⭐
- `xl` — 24dp (bottom sheets)
- `xxl` — 32dp (модалки)
- `full` — 100dp (круглі)

### Градієнти

- `Gradients.primary` — індиго → фіолетовий
- `Gradients.secondary` — помаранчевий → жовтий (CTA)
- `Gradients.success` — зелений (досягнення)
- `Gradients.premium` — золотий (premium badge)

### Анімації

**Duration:**
- `micro` — 100ms (hover, color change)
- `short` — 200ms (кнопки, chips)
- `medium` — 350ms (картки, expand)
- `long` — 500ms (екрани)
- `emphasis` — 800ms (досягнення)

**Easing:**
- `standard` — загальні transitions
- `decelerate` — елементи що з'являються
- `accelerate` — елементи що зникають
- `bouncy` — playful interactions
- `snappy` — immediate feedback

## 💡 Best Practices

### Кольори

✅ **Робити:**
- Використовуй `MaterialTheme.colorScheme.*` де можливо
- Використовуй semantic colors через extensions (`colorScheme.success`)
- Використовуй `TextColors.secondary` для вторинного тексту

❌ **Не робити:**
- Хардкодити кольори напряму (`Color(0xFF123456)`)
- Використовувати градієнти на всіх кнопках
- Ігнорувати semantic colors

### Типографіка

✅ **Робити:**
- Використовуй `MaterialTheme.typography.*`
- `headlineLarge` для заголовків екранів
- `bodyLarge` для основного тексту

❌ **Не робити:**
- Створювати custom TextStyle без причини
- Хардкодити fontSize напряму

### Spacing

✅ **Робити:**
- Використовуй `Spacing.*` для відступів
- Використовуй shortcuts (`Modifier.screenPadding()`)
- `Spacing.md` як базовий відступ

❌ **Не робити:**
- Хардкодити відступи (`16.dp` напряму)
- Використовувати різні відступи для схожих елементів

### Анімації

✅ **Робити:**
- Використовуй `AnimationDuration.*` та `AnimationEasing.*`
- Button press: 100ms snappy
- Appear: 250ms decelerate

❌ **Не робити:**
- Хардкодити duration напряму
- Забувати про accessibility (reduced motion)

## 📦 Приклади компонентів

### Primary Button

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .shadowPreset(ShadowPreset.BUTTON_PRIMARY, RoundedCornerShape(CornerRadius.md)),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColors.default,
            contentColor = TextColors.onPrimary
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
```

### CTA Button з градієнтом

```kotlin
@Composable
fun CtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .gradientBackground(
                gradient = Gradients.secondary,
                shape = RoundedCornerShape(CornerRadius.md)
            )
            .shadowPreset(ShadowPreset.BUTTON_CTA, RoundedCornerShape(CornerRadius.md))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = TextColors.onSecondary
        )
    }
}
```

### Course Card

```kotlin
@Composable
fun CourseCard(
    title: String,
    description: String,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .cornerRadiusLg()
            .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        )
    ) {
        Column(
            modifier = Modifier.cardPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextColors.primary
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.secondary
            )
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .cornerRadius(CornerRadius.full),
                color = PrimaryColors.default,
                trackColor = BackgroundColors.primary
            )
        }
    }
}
```

### Glass Effect Card

```kotlin
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassEffect(
                strength = GlassStrength.MEDIUM,
                shape = RoundedCornerShape(CornerRadius.xl),
                border = true
            )
            .padding(Spacing.lg)
    ) {
        content()
    }
}
```

## 🚀 Наступні кроки

### TODO для повноцінної імплементації:

1. **Додати Inter Font:**
   - Завантажити Inter Regular, Medium, SemiBold, Bold
   - Додати в `res/font/`
   - Оновити `Type.kt` для використання Inter

2. **Multi-layer Shadows:**
   - Створити `Modifier.multiLayerShadow()`
   - Використовувати `drawBehind` з Canvas для справжніх multi-layer shadows

3. **Accessibility:**
   - Додати підтримку reduced motion
   - Перевірити contrast ratios

4. **Animation Extensions:**
   - Створити готові animation composables
   - Додати більше presets

5. **Component Library:**
   - Створити `/components/` директорію
   - Імплементувати всі базові компоненти (buttons, cards, тощо)

## 📚 Документація

Повна документація в **Design Bible v1.0** (`Design_Bible_v1_0.md`)

## 🤝 Contributing

При додаванні нових кольорів/spacing/тощо:
1. Оновити відповідний файл
2. Додати приклад використання
3. Оновити цей README

---

**Версія:** 1.0  
**Дата:** Січень 2026  
**Автор:** AI VoicePower Team
