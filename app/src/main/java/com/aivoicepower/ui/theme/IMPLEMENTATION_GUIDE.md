# AI VoicePower Design System — Implementation Guide

> Гайд для розробників по впровадженню Design System

## ✅ Що вже готово

Design System повністю імплементований і готовий до використання:

### 📁 Файли
- ✅ `Color.kt` — всі кольори (8 категорій, 50+ токенів)
- ✅ `Type.kt` — типографіка (11 text styles)
- ✅ `Shape.kt` — corner radius system (7 розмірів)
- ✅ `Spacing.kt` — система відступів (8 розмірів + semantic aliases)
- ✅ `Elevation.kt` — тіні та elevation (6 levels, 8 shadow presets)
- ✅ `Animation.kt` — параметри анімацій (5 duration, 6 easing curves)
- ✅ `Gradient.kt` — 10+ градієнтів
- ✅ `Theme.kt` — MaterialTheme wrapper з extensions
- ✅ `Modifiers.kt` — custom extensions (20+ shortcuts)
- ✅ `README.md` — повна документація
- ✅ `TOKENS.md` — швидка довідка
- ✅ `IMPLEMENTATION_GUIDE.md` — цей файл

### 🎨 Токени
- **Кольори:** 50+ (backgrounds, primary, secondary, semantic, text, borders, glass)
- **Типографіка:** 11 text styles (display, headline, title, body, label)
- **Spacing:** 8 розмірів + 10 semantic aliases
- **Corner Radius:** 7 розмірів + custom shapes
- **Shadows:** 8 presets (card, button, modal, тощо)
- **Градієнти:** 10+ (primary, secondary, success, premium, тощо)
- **Анімації:** 5 duration + 6 easing curves

## 🚀 Швидкий старт

### 1. Перший запуск

Наразі Design System готовий, але ще не підключений до застосунку. Коли створиш `MainActivity.kt`, використовуй:

```kotlin
package com.aivoicepower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
                    // Твій контент тут
                    MainScreen()
                }
            }
        }
    }
}
```

### 2. Імпорти

У кожному файлі де використовуєш Design System:

```kotlin
import com.aivoicepower.ui.theme.*
```

Або окремо:
```kotlin
import com.aivoicepower.ui.theme.Spacing
import com.aivoicepower.ui.theme.CornerRadius
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.Gradients
```

### 3. Перший компонент

```kotlin
@Composable
fun MyFirstComponent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .cornerRadiusLg()
            .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg)),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        )
    ) {
        Column(modifier = Modifier.cardPadding()) {
            Text(
                text = "Заголовок",
                style = MaterialTheme.typography.titleLarge,
                color = TextColors.primary
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = "Опис",
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.secondary
            )
        }
    }
}
```

## 📚 Patterns & Best Practices

### Pattern 1: Кнопки

**Primary Button (стандартна):**
```kotlin
Button(
    onClick = { },
    modifier = Modifier
        .height(48.dp)
        .shadowPreset(ShadowPreset.BUTTON_PRIMARY, RoundedCornerShape(CornerRadius.md)),
    colors = ButtonDefaults.buttonColors(
        containerColor = PrimaryColors.default,
        contentColor = TextColors.onPrimary
    ),
    shape = RoundedCornerShape(CornerRadius.md)
) {
    Text("Кнопка", style = MaterialTheme.typography.labelLarge)
}
```

**CTA Button (з градієнтом):**
```kotlin
Box(
    modifier = Modifier
        .height(48.dp)
        .gradientBackground(Gradients.secondary, RoundedCornerShape(CornerRadius.md))
        .shadowPreset(ShadowPreset.BUTTON_CTA, RoundedCornerShape(CornerRadius.md))
        .clickable { }
        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    contentAlignment = Alignment.Center
) {
    Text(
        "Почати урок",
        style = MaterialTheme.typography.labelLarge,
        color = TextColors.onSecondary
    )
}
```

### Pattern 2: Картки

**Звичайна картка:**
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .cornerRadiusLg()
        .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg)),
    colors = CardDefaults.cardColors(containerColor = BackgroundColors.surface)
) {
    Column(modifier = Modifier.cardPadding()) {
        // Content
    }
}
```

**Glass effect картка:**
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .glassEffect(GlassStrength.MEDIUM, RoundedCornerShape(CornerRadius.xl))
        .padding(Spacing.lg)
) {
    // Content
}
```

### Pattern 3: Списки

**LazyColumn з spacing:**
```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .screenPadding(),
    verticalArrangement = Arrangement.spacedBy(Spacing.cardSpacing)
) {
    items(courses) { course ->
        CourseCard(course)
    }
}
```

### Pattern 4: Анімації

**Button press:**
```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isPressed by interactionSource.collectIsPressedAsState()

val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = tween(
        durationMillis = if (isPressed) AnimationDuration.micro else AnimationDuration.short,
        easing = if (isPressed) AnimationEasing.snappy else AnimationEasing.bouncy
    )
)

Button(
    onClick = { },
    modifier = Modifier.scale(scale),
    interactionSource = interactionSource
) {
    Text("Кнопка")
}
```

### Pattern 5: Прогрес бар

```kotlin
LinearProgressIndicator(
    progress = 0.7f,
    modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .cornerRadius(CornerRadius.full),
    color = PrimaryColors.default,
    trackColor = BackgroundColors.primary
)
```

## 🎯 Common Tasks

### Завдання 1: Створити екран

```kotlin
@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColors.primary)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionSpacing)
    ) {
        // Header
        Text(
            text = "Заголовок екрану",
            style = MaterialTheme.typography.headlineLarge,
            color = TextColors.primary
        )
        
        // Content section
        MyContentSection()
        
        // CTA
        CtaButton("Почати") { }
    }
}
```

### Завдання 2: Створити картку курсу

```kotlin
@Composable
fun CourseCard(
    title: String,
    description: String,
    progress: Float,
    isPremium: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .cornerRadiusLg()
            .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BackgroundColors.surface)
    ) {
        Column(
            modifier = Modifier.cardPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextColors.primary
                )
                
                if (isPremium) {
                    PremiumBadge()
                }
            }
            
            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.secondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Progress
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

### Завдання 3: Створити Bottom Navigation

```kotlin
@Composable
fun AppBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(Spacing.bottomNavHeight),
        containerColor = BackgroundColors.secondary
    ) {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) 
                            item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColors.default,
                    selectedTextColor = PrimaryColors.default,
                    unselectedIconColor = IconColors.secondary,
                    unselectedTextColor = TextColors.secondary,
                    indicatorColor = PrimaryColors.default.copy(alpha = 0.1f)
                )
            )
        }
    }
}
```

## ⚠️ Common Mistakes

### ❌ Помилка 1: Хардкодити кольори
```kotlin
// НЕ РОБИТИ:
Text(text = "Hello", color = Color(0xFF6366F1))

// ПРАВИЛЬНО:
Text(text = "Hello", color = PrimaryColors.default)
// АБО
Text(text = "Hello", color = MaterialTheme.colorScheme.primary)
```

### ❌ Помилка 2: Хардкодити spacing
```kotlin
// НЕ РОБИТИ:
Column(modifier = Modifier.padding(16.dp))

// ПРАВИЛЬНО:
Column(modifier = Modifier.padding(Spacing.md))
// АБО
Column(modifier = Modifier.screenPadding())
```

### ❌ Помилка 3: Використовувати градієнти всюди
```kotlin
// НЕ РОБИТИ (занадто багато):
PrimaryButton(gradient = Gradients.primary)
SecondaryButton(gradient = Gradients.secondary)
TertiaryButton(gradient = Gradients.success)

// ПРАВИЛЬНО (вибірково):
CtaButton(gradient = Gradients.secondary) // Тільки для головних CTA!
PrimaryButton(color = PrimaryColors.default) // Решта без градієнтів
```

### ❌ Помилка 4: Забувати про animation duration
```kotlin
// НЕ РОБИТИ:
animateFloatAsState(targetValue = 1f, animationSpec = tween(200))

// ПРАВИЛЬНО:
animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(
        durationMillis = AnimationDuration.short,
        easing = AnimationEasing.standard
    )
)
```

## 📋 Чеклист для PR

Перед тим як створити PR з новим UI компонентом:

- [ ] Використовую `Spacing.*` замість хардкодених `dp`
- [ ] Використовую `CornerRadius.*` замість хардкодених `dp`
- [ ] Використовую кольори з `*Colors` objects або `MaterialTheme.colorScheme`
- [ ] Використовую `MaterialTheme.typography.*` для текстів
- [ ] Градієнти тільки там де потрібно (CTA, преміум елементи)
- [ ] Анімації використовують `AnimationDuration.*` та `AnimationEasing.*`
- [ ] Код чистий та читабельний
- [ ] Немає хардкодених значень

## 🆘 Troubleshooting

### Проблема: Тема не застосовується

**Рішення:** Перевір що `AIVoicePowerTheme` обгортає весь контент:
```kotlin
setContent {
    AIVoicePowerTheme {  // ⬅️ Повинна бути тут!
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}
```

### Проблема: Кольори не видно

**Рішення:** Перевір що використовуєш правильний контраст:
- На темному фоні: `TextColors.primary` (світлий текст)
- На світлих елементах: `TextColors.muted` (темніший текст)

### Проблема: Shadow не відображається

**Рішення:** Material 3 `elevation` має обмеження. Для складних тіней використовуй:
```kotlin
Modifier.shadowPreset(ShadowPreset.CARD, shape)
```

### Проблема: Gradient не працює на Button

**Рішення:** Button має свій background. Використовуй `Box` з `clickable`:
```kotlin
Box(
    modifier = Modifier
        .gradientBackground(Gradients.secondary, RoundedCornerShape(CornerRadius.md))
        .clickable { }
        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    contentAlignment = Alignment.Center
) {
    Text("Кнопка")
}
```

## 🔮 Майбутні покращення

### TODO для Design System:

1. **Inter Font:**
   - [ ] Завантажити Inter (Regular, Medium, SemiBold, Bold)
   - [ ] Додати в `res/font/`
   - [ ] Оновити `Type.kt`

2. **Multi-layer Shadows:**
   - [ ] Імплементувати `Modifier.multiLayerShadow()`
   - [ ] Використовувати Canvas API

3. **Accessibility:**
   - [ ] Додати підтримку reduced motion
   - [ ] Перевірити contrast ratios (WCAG AA)
   - [ ] Додати semantic descriptions

4. **Component Library:**
   - [ ] Створити готові компоненти в `/components/`
   - [ ] Buttons (Primary, Secondary, CTA, Text)
   - [ ] Cards (Course, Lesson, Achievement)
   - [ ] Progress (Linear, Circular, Ring)
   - [ ] Inputs (TextField, Search)
   - [ ] Dialogs (Alert, Confirmation, Premium)

5. **Animation Presets:**
   - [ ] Готові composables для анімацій
   - [ ] Enter/Exit transitions
   - [ ] Stagger animations
   - [ ] Achievement unlock sequence

## 📞 Контакти

Питання по Design System? Звертайся:
- Design Bible: `Design_Bible_v1_0.md`
- README: `ui/theme/README.md`
- Tokens: `ui/theme/TOKENS.md`
- Цей гайд: `ui/theme/IMPLEMENTATION_GUIDE.md`

---

**Версія:** 1.0  
**Дата:** Січень 2026  
**Статус:** ✅ Ready for Production
