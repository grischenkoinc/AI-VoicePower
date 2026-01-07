# AI VoicePower Design System — Quick Reference

> Швидка шпаргалка для щоденного використання

## 🎨 Найчастіші кольори

```kotlin
// Backgrounds
BackgroundColors.primary     // #312E81 — основний фон
BackgroundColors.surface     // #4338CA — картки

// Primary
PrimaryColors.default        // #6366F1 — кнопки, акценти

// Secondary (CTA)
SecondaryColors.default      // #F97316 — важливі дії

// Text
TextColors.primary          // #F9FAFB — основний
TextColors.secondary        // #9CA3AF — вторинний

// Semantic
SemanticColors.success      // #10B981 — успіх
SemanticColors.error        // #EF4444 — помилка
```

## 📝 Найчастіші text styles

```kotlin
MaterialTheme.typography.headlineLarge   // 24sp — заголовки екранів
MaterialTheme.typography.titleLarge      // 18sp — назви карток
MaterialTheme.typography.bodyLarge       // 16sp — основний текст
MaterialTheme.typography.bodyMedium      // 14sp — вторинний текст
MaterialTheme.typography.labelLarge      // 14sp — кнопки
```

## 📏 Найчастіші spacing

```kotlin
Spacing.sm   // 8dp  — між елементами
Spacing.md   // 16dp — базовий padding (⭐ most common)
Spacing.lg   // 24dp — між секціями

// Shortcuts
Modifier.screenPadding()  // 16dp horizontal
Modifier.cardPadding()    // 16dp all sides
```

## 🔲 Найчастіші corner radius

```kotlin
CornerRadius.md   // 12dp — кнопки, inputs (⭐ most common)
CornerRadius.lg   // 16dp — картки (⭐ most common)

// Shortcuts
Modifier.cornerRadiusMd()
Modifier.cornerRadiusLg()
```

## 🌈 Градієнти (використовувати рідко!)

```kotlin
Gradients.primary     // Індиго → Фіолетовий
Gradients.secondary   // Помаранчевий → Жовтий (⭐ для головних CTA)
Gradients.success     // Зелений (досягнення)
```

## 🎬 Анімації

```kotlin
AnimationDuration.short   // 200ms — кнопки (⭐ most common)
AnimationDuration.medium  // 350ms — картки (⭐ most common)

AnimationEasing.standard  // Загальні transitions (⭐ default)
AnimationEasing.bouncy    // Playful interactions
```

## ⚡ Top 5 patterns

### 1. Primary Button
```kotlin
Button(
    onClick = { },
    modifier = Modifier.height(48.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = PrimaryColors.default
    ),
    shape = RoundedCornerShape(CornerRadius.md)
) {
    Text("Кнопка", style = MaterialTheme.typography.labelLarge)
}
```

### 2. CTA Button (з градієнтом)
```kotlin
Box(
    modifier = Modifier
        .gradientBackground(Gradients.secondary, RoundedCornerShape(CornerRadius.md))
        .clickable { }
        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
) {
    Text("Почати урок", color = TextColors.onSecondary)
}
```

### 3. Card
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .cornerRadiusLg()
) {
    Column(modifier = Modifier.cardPadding()) {
        Text("Заголовок", style = MaterialTheme.typography.titleLarge)
        Text("Опис", style = MaterialTheme.typography.bodyMedium, color = TextColors.secondary)
    }
}
```

### 4. Screen Layout
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .background(BackgroundColors.primary)
        .screenPadding(),
    verticalArrangement = Arrangement.spacedBy(Spacing.sectionSpacing)
) {
    // Sections
}
```

### 5. Progress Bar
```kotlin
LinearProgressIndicator(
    progress = 0.7f,
    modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .cornerRadius(CornerRadius.full),
    color = PrimaryColors.default
)
```

## ❌ Top 5 mistakes

1. ❌ Хардкодити кольори: `Color(0xFF6366F1)` → ✅ `PrimaryColors.default`
2. ❌ Хардкодити spacing: `16.dp` → ✅ `Spacing.md`
3. ❌ Градієнти всюди → ✅ Тільки для головних CTA
4. ❌ Забувати про text styles → ✅ `MaterialTheme.typography.*`
5. ❌ Хардкодити animation duration → ✅ `AnimationDuration.*`

## 🔥 Hotkeys (mental shortcuts)

- **Padding екрану?** → `Modifier.screenPadding()` (16dp)
- **Між секціями?** → `Spacing.lg` (24dp)
- **Картка?** → `CornerRadius.lg` (16dp) + `Modifier.cardPadding()`
- **Кнопка?** → `CornerRadius.md` (12dp) + `height(48.dp)`
- **CTA?** → `Gradients.secondary` (помаранчевий→жовтий)
- **Анімація кнопки?** → `AnimationDuration.short` (200ms)

## 🎯 Decision tree

### Який колір використати?
```
Основний контент? → PrimaryColors.default
CTA дія? → SecondaryColors.default
Текст? → TextColors.primary / secondary
Успіх? → SemanticColors.success
Помилка? → SemanticColors.error
```

### Який spacing?
```
Екран? → Spacing.md (16dp) або screenPadding()
Між секціями? → Spacing.lg (24dp)
Між елементами? → Spacing.sm (8dp)
Всередині картки? → Spacing.md (16dp) або cardPadding()
```

### Який corner radius?
```
Кнопка/Input? → CornerRadius.md (12dp)
Картка? → CornerRadius.lg (16dp)
Chip/Tag? → CornerRadius.sm (8dp)
Modal? → CornerRadius.xxl (32dp)
```

### Градієнт чи solid color?
```
Головна CTA ("Почати урок")? → Градієнт (Gradients.secondary)
Звичайна кнопка? → Solid (PrimaryColors.default)
Преміум бейдж? → Градієнт (Gradients.premium)
Решта? → Solid
```

---

**💡 Tip:** Коли сумніваєшся — використовуй solid colors і `Spacing.md`. Це працює в 80% випадків.
