# AI VoicePower Design Tokens

> Швидка довідка для всіх токенів Design System

## 🎨 Colors

### Backgrounds
```kotlin
BackgroundColors.primary          // #312E81 — основний фон
BackgroundColors.secondary        // #3730A3 — альтернативні секції
BackgroundColors.surface          // #4338CA — картки
BackgroundColors.surfaceElevated  // #5B52E0 — модалки
```

### Primary (Індиго)
```kotlin
PrimaryColors.light    // #818CF8 — іконки неактивні
PrimaryColors.default  // #6366F1 — основний
PrimaryColors.dark     // #4F46E5 — hover
PrimaryColors.darker   // #4338CA — pressed
```

### Secondary (Помаранчевий)
```kotlin
SecondaryColors.light    // #FB923C — highlight
SecondaryColors.default  // #F97316 — CTA
SecondaryColors.dark     // #EA580C — hover
```

### Accent (Бірюзовий)
```kotlin
AccentColors.light    // #5EEAD4
AccentColors.default  // #14B8A6 — прогрес, streak
AccentColors.dark     // #0D9488
```

### Semantic
```kotlin
SemanticColors.success       // #10B981
SemanticColors.successLight  // #34D399
SemanticColors.warning       // #F59E0B
SemanticColors.warningLight  // #FBBF24
SemanticColors.error         // #EF4444
SemanticColors.errorLight    // #F87171
SemanticColors.info          // #3B82F6
SemanticColors.infoLight     // #60A5FA
```

### Text
```kotlin
TextColors.primary      // #F9FAFB — заголовки, основний
TextColors.secondary    // #9CA3AF — описи, підписи
TextColors.muted        // #6B7280 — hints, placeholder
TextColors.onPrimary    // #FFFFFF — на primary кнопках
TextColors.onSecondary  // #FFFFFF — на secondary кнопках
```

### Borders
```kotlin
BorderColors.subtle   // 6% white — тонкі межі
BorderColors.default  // 10% white — стандартні
BorderColors.accent   // 30% primary — акцентні
```

### Glass Effect
```kotlin
GlassEffect.backgroundLight   // 5% white
GlassEffect.backgroundMedium  // 8% white
GlassEffect.backgroundStrong  // 10% white
GlassEffect.borderColor       // 10% white
```

## 📝 Typography

### Display (великі заголовки)
```kotlin
MaterialTheme.typography.displayLarge   // 32sp Bold
MaterialTheme.typography.displayMedium  // 28sp Bold
MaterialTheme.typography.displaySmall   // 24sp Bold
```

### Headline (заголовки екранів)
```kotlin
MaterialTheme.typography.headlineLarge   // 24sp SemiBold ⭐
MaterialTheme.typography.headlineMedium  // 20sp SemiBold ⭐
MaterialTheme.typography.headlineSmall   // 18sp SemiBold
```

### Title (заголовки карток)
```kotlin
MaterialTheme.typography.titleLarge   // 18sp Medium ⭐
MaterialTheme.typography.titleMedium  // 16sp Medium ⭐
MaterialTheme.typography.titleSmall   // 14sp Medium
```

### Body (основний текст)
```kotlin
MaterialTheme.typography.bodyLarge   // 16sp Normal ⭐
MaterialTheme.typography.bodyMedium  // 14sp Normal ⭐
MaterialTheme.typography.bodySmall   // 12sp Normal
```

### Label (кнопки, chips)
```kotlin
MaterialTheme.typography.labelLarge   // 14sp Medium ⭐
MaterialTheme.typography.labelMedium  // 12sp Medium
MaterialTheme.typography.labelSmall   // 10sp Medium
```

## 📏 Spacing

```kotlin
Spacing.xxs   // 2dp  — мінімальні відступи
Spacing.xs    // 4dp  — між іконкою та текстом
Spacing.sm    // 8dp  — між елементами списку, між картками
Spacing.md    // 16dp — стандартний padding ⭐
Spacing.lg    // 24dp — між секціями ⭐
Spacing.xl    // 32dp — великі відступи
Spacing.xxl   // 48dp — padding екранів
Spacing.xxxl  // 64dp — особливі випадки
```

### Semantic Spacing
```kotlin
Spacing.screenHorizontal  // 16dp — від країв екрану
Spacing.screenVertical    // 16dp — зверху/знизу екрану
Spacing.sectionSpacing    // 24dp — між секціями
Spacing.cardPadding       // 16dp — всередині картки
Spacing.cardSpacing       // 8dp  — між картками
Spacing.listItemSpacing   // 8dp  — між елементами списку
Spacing.bottomNavHeight   // 80dp — висота bottom navigation
```

## 🔲 Corner Radius

```kotlin
CornerRadius.xs    // 4dp   — tags
CornerRadius.sm    // 8dp   — chips, малі кнопки
CornerRadius.md    // 12dp  — кнопки, inputs ⭐
CornerRadius.lg    // 16dp  — картки ⭐
CornerRadius.xl    // 24dp  — bottom sheets
CornerRadius.xxl   // 32dp  — модальні вікна
CornerRadius.full  // 100dp — круглі елементи
```

### Custom Shapes
```kotlin
CustomShapes.modal       // 32dp rounded
CustomShapes.pill        // 100dp rounded
CustomShapes.bottomSheet // тільки верх rounded
CustomShapes.circle      // 50% rounded
```

## 🎭 Elevation & Shadows

### Elevation Levels
```kotlin
Elevation.level0  // 0dp  — flat
Elevation.level1  // 2dp  — subtle lift
Elevation.level2  // 4dp  — cards ⭐
Elevation.level3  // 8dp  — raised cards, FAB
Elevation.level4  // 16dp — dropdowns, menus
Elevation.level5  // 24dp — modals, dialogs
```

### Shadow Presets
```kotlin
Shadows.card           // Звичайна картка
Shadows.elevatedGlow   // Активна картка з glow
Shadows.primaryButton  // Primary кнопка
Shadows.ctaButton      // CTA кнопка
Shadows.subtle         // Малі елементи
Shadows.modal          // Модальні вікна
Shadows.fab            // FAB з glow
Shadows.bottomSheet    // Bottom sheet
```

## 🎬 Animation

### Duration
```kotlin
AnimationDuration.micro     // 100ms — hover, color change
AnimationDuration.short     // 200ms — кнопки, chips ⭐
AnimationDuration.medium    // 350ms — картки, expand ⭐
AnimationDuration.long      // 500ms — екрани
AnimationDuration.emphasis  // 800ms — досягнення
```

### Easing
```kotlin
AnimationEasing.standard    // Загальні transitions ⭐
AnimationEasing.decelerate  // Елементи що з'являються
AnimationEasing.accelerate  // Елементи що зникають
AnimationEasing.bouncy      // Playful interactions
AnimationEasing.smooth      // Тривалі анімації, loops
AnimationEasing.snappy      // Immediate feedback
```

### Stagger
```kotlin
AnimationStagger.listItem            // 50ms між елементами списку
AnimationStagger.gridItem            // 75ms між елементами grid
AnimationStagger.achievementSequence // 200ms між кроками
```

## 🌈 Gradients

```kotlin
Gradients.primary              // Індиго → Фіолетовий (кнопки)
Gradients.primaryHorizontal    // Горизонтальний
Gradients.primaryVertical      // Вертикальний

Gradients.secondary            // Помаранчевий → Жовтий (CTA) ⭐
Gradients.secondaryHorizontal
Gradients.secondaryVertical

Gradients.success              // Зелений (досягнення)
Gradients.premium              // Золотий (premium badge)
Gradients.premiumRadial        // Радіальний premium

Gradients.backgroundSubtle     // Фон екранів (тонкий)
Gradients.surface              // Картки з градієнтом

Gradients.glow                 // Glow ефекти
Gradients.shimmer              // Skeleton loading
Gradients.recordButton         // Кнопка запису
```

## 🛠️ Modifiers (Extensions)

### Glass Effect
```kotlin
Modifier.glassEffect(
    strength = GlassStrength.MEDIUM,  // LIGHT, MEDIUM, STRONG
    shape = RoundedCornerShape(CornerRadius.lg),
    border = true
)
```

### Gradient Background
```kotlin
Modifier.gradientBackground(
    gradient = Gradients.primary,
    shape = RoundedCornerShape(CornerRadius.md)
)
```

### Shadow Preset
```kotlin
Modifier.shadowPreset(
    preset = ShadowPreset.CARD,  // CARD, ELEVATED, BUTTON_PRIMARY, тощо
    shape = RoundedCornerShape(CornerRadius.lg)
)
```

### Spacing Shortcuts
```kotlin
Modifier.screenPadding()   // 16dp horizontal
Modifier.cardPadding()     // 16dp all sides
Modifier.sectionSpacing()  // 24dp vertical
```

### Corner Radius Shortcuts
```kotlin
Modifier.cornerRadiusXs()   // 4dp
Modifier.cornerRadiusSm()   // 8dp
Modifier.cornerRadiusMd()   // 12dp
Modifier.cornerRadiusLg()   // 16dp ⭐
Modifier.cornerRadiusXl()   // 24dp
Modifier.cornerRadiusFull() // Circle
```

### Border Shortcuts
```kotlin
Modifier.borderSubtle()  // 6% white
Modifier.borderDefault() // 10% white
Modifier.borderAccent()  // 30% primary, 2dp
```

## 🎯 Найчастіше використовувані

### Кольори
- `PrimaryColors.default` (#6366F1) — основні кнопки, акценти
- `SecondaryColors.default` (#F97316) — CTA
- `BackgroundColors.surface` (#4338CA) — картки
- `TextColors.primary` (#F9FAFB) — основний текст
- `TextColors.secondary` (#9CA3AF) — вторинний текст
- `SemanticColors.success` (#10B981) — успіх

### Spacing
- `Spacing.md` (16dp) — базовий padding ⭐
- `Spacing.lg` (24dp) — між секціями ⭐
- `Spacing.sm` (8dp) — між елементами

### Corner Radius
- `CornerRadius.md` (12dp) — кнопки, inputs ⭐
- `CornerRadius.lg` (16dp) — картки ⭐

### Градієнти
- `Gradients.primary` — основні CTA
- `Gradients.secondary` — важливі дії (Почати, Записати) ⭐

### Анімації
- `AnimationDuration.short` (200ms) — кнопки ⭐
- `AnimationDuration.medium` (350ms) — картки ⭐
- `AnimationEasing.standard` — загальні transitions ⭐

---

**Легенда:** ⭐ — найчастіше використовувані токени
