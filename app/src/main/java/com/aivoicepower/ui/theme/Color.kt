package com.aivoicepower.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * AI VoicePower Color System v2.0
 *
 * Джерело: Design_Example_react.md
 * Стиль: Фіолетово-градієнтна преміум схема
 */

// ===== 1. BACKGROUND COLORS =====

object BackgroundColors {
    // Основний градієнт застосунку (#667EEA → #764BA2, 135°)
    val gradientStart = Color(0xFF667EEA)  // Індиго
    val gradientEnd = Color(0xFF764BA2)    // Фіолетовий

    val appGradient = Brush.linearGradient(
        colors = listOf(gradientStart, gradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Solid backgrounds (якщо потрібні)
    val surface = Color(0xFFFFFFFF)        // Білий для карток
    val surfaceAlt = Color(0xFFF9FAFB)     // Майже білий

    // Compatibility: Dark theme backgrounds
    val primary = Color(0xFF2D2640)        // Основний фон екранів
    val secondary = Color(0xFF1A1625)      // Чергування секцій
    val tertiary = Color(0xFF0F0D1A)       // Найтемніший фон
    val surfaceElevated = Color(0xFF5B52E0) // Модальні вікна
}

// ===== 2. CARD HEADER GRADIENTS =====

object CardHeaderColors {
    // Theory Card Header (#3D266A → #1F1F2E, 135°)
    val theoryStart = Color(0xFF3D266A)   // Темний фіолетовий
    val theoryEnd = Color(0xFF1F1F2E)     // Майже чорний

    val theoryGradient = Brush.linearGradient(
        colors = listOf(theoryStart, theoryEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Practice Card Header (#2D2640 → #1F1F2E, 135°)
    val practiceStart = Color(0xFF2D2640)  // Темний фіолетово-сірий
    val practiceEnd = Color(0xFF1F1F2E)    // Майже чорний

    val practiceGradient = Brush.linearGradient(
        colors = listOf(practiceStart, practiceEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Compatibility: Aliases for default card header
    val start = theoryStart                // Default to theory
    val end = theoryEnd                    // Default to theory
}

// ===== 3. TAG & BADGE GRADIENTS =====

object TagColors {
    // Primary Tag (#A78BFA → #7C3AED, 135°) — для Section Tags
    val primaryStart = Color(0xFFA78BFA)   // Світлий фіолетовий
    val primaryEnd = Color(0xFF7C3AED)     // Фіолетовий

    val primaryGradient = Brush.linearGradient(
        colors = listOf(primaryStart, primaryEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Secondary Tag (#F59E0B → #D97706, 135°) — для Practice Tags
    val secondaryStart = Color(0xFFF59E0B) // Помаранчевий
    val secondaryEnd = Color(0xFFD97706)   // Темний помаранчевий

    val secondaryGradient = Brush.linearGradient(
        colors = listOf(secondaryStart, secondaryEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Level Pill (#FBBF24 → #F59E0B, 135°)
    val levelStart = Color(0xFFFBBF24)     // Жовтий
    val levelEnd = Color(0xFFF59E0B)       // Помаранчевий

    val levelGradient = Brush.linearGradient(
        colors = listOf(levelStart, levelEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

// ===== 4. PROGRESS BAR COLORS =====

object ProgressColors {
    // Progress Track (#C4B5FD → #A78BFA, 180° vertical)
    val trackStart = Color(0xFFC4B5FD)     // Світло-фіолетовий
    val trackEnd = Color(0xFFA78BFA)       // Фіолетовий

    val trackGradient = Brush.linearGradient(
        colors = listOf(trackStart, trackEnd),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY) // Вертикальний
    )

    // Progress Fill (#22C55E → #16A34A, 90° horizontal) + glow
    val fillStart = Color(0xFF22C55E)      // Яскравий зелений
    val fillEnd = Color(0xFF16A34A)        // Темний зелений

    val fillGradient = Brush.linearGradient(
        colors = listOf(fillStart, fillEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f) // Горизонтальний
    )

    // Glow color для fill (70% opacity)
    val fillGlow = Color(0xB322C55E)       // 70% зелений для glow effect
}

// ===== 5. GLASS EFFECT COLORS =====

object GlassColors {
    // Background з різною інтенсивністю
    val background = Color(0x33FFFFFF)      // 20% white
    val backgroundMedium = Color(0x4DFFFFFF) // 30% white
    val backgroundLight = Color(0x66FFFFFF)  // 40% white

    // Borders
    val border = Color(0x33FFFFFF)          // 20% white
    val borderMedium = Color(0x4DFFFFFF)    // 30% white
    val borderLight = Color(0x66FFFFFF)     // 40% white

    // Compatibility: Aliases for easier access
    val light = Color(0x0DFFFFFF)           // 5% white
    val medium = Color(0x14FFFFFF)          // 8% white
    val strong = Color(0x1AFFFFFF)          // 10% white
}

// ===== 6. TEXT COLORS =====

object TextColors {
    // Text on LIGHT backgrounds (cards, white surfaces)
    val onLightPrimary = Color(0xFF111827)      // Майже чорний (основний текст)
    val onLightSecondary = Color(0xFF4B5563)    // Темно-сірий (вторинний текст)
    val onLightMuted = Color(0xFF6B7280)        // Сірий (приглушений текст)
    val onLightHint = Color(0xFF9CA3AF)         // Світло-сірий (підказки)

    // Text on DARK backgrounds (gradient bg, dark headers)
    val onDarkPrimary = Color(0xFFFFFFFF)       // Білий (основний текст)
    val onDarkSecondary = Color(0xFFE5E7EB)     // Світло-сірий (вторинний текст)
    val onDarkMuted = Color(0xFF9CA3AF)         // Приглушений сірий

    // Text on COLORED buttons/tags
    val onPrimary = Color(0xFFFFFFFF)           // Білий на фіолетовому
    val onSecondary = Color(0xFF1F2937)         // Темний на жовтому/помаранчевому

    // Compatibility: General text colors
    val primary = Color(0xFFF9FAFB)             // Заголовки, основний контент
    val secondary = Color(0xFF9CA3AF)           // Описи, підписи
    val tertiary = Color(0xFF6B7280)            // Meta info, icons, hints
    val muted = Color(0xFF6B7280)               // Hints, placeholder
}

// ===== 7. BORDER COLORS =====

object BorderColors {
    // Borders на світлих поверхнях
    val light = Color(0xFFE5E7EB)           // Світло-сірий
    val default = Color(0xFFD1D5DB)         // Сірий

    // Borders на темних поверхнях
    val onDark = Color(0x33FFFFFF)          // 20% white
    val onDarkAccent = Color(0x4DFFFFFF)    // 30% white

    // Accent borders
    val primary = Color(0xFF8B5CF6)         // Фіолетовий
    val primaryLight = Color(0xFFA78BFA)    // Світлий фіолетовий

    // Compatibility: Additional border colors
    val subtle = Color(0x0FFFFFFF)          // 6% white - тонкі межі
    val border = Color(0x1AFFFFFF)          // 10% white - стандартні межі
    val accent = Color(0x4D8B5CF6)          // 30% primary - акцентні межі
}

// ===== 8. SEMANTIC COLORS =====

object SemanticColors {
    val success = Color(0xFF10B981)         // Зелений
    val warning = Color(0xFFF59E0B)         // Помаранчевий
    val error = Color(0xFFEF4444)           // Червоний
    val info = Color(0xFF3B82F6)            // Синій

    // Lighter variants
    val successLight = Color(0xFF22C55E)
    val warningLight = Color(0xFFFBBF24)
    val errorLight = Color(0xFFF87171)
    val infoLight = Color(0xFF60A5FA)
}

// ===== 9. ADDITIONAL GRADIENTS =====

object AdditionalGradients {
    // Section Background (#F3F4F6 → #E5E7EB, 135°)
    val sectionBg = Brush.linearGradient(
        colors = listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Record Button (ідентичний app background)
    val recordButton = BackgroundColors.appGradient

    // Highlight Box (15% #F59E0B → 20% #8B5CF6, 135°)
    val highlightBox = Brush.linearGradient(
        colors = listOf(
            Color(0x26F59E0B),  // 15% помаранчевий
            Color(0x338B5CF6)   // 20% фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

// ===== 10. GEOMETRIC SHAPES (для фону) =====

object GeometricShapeColors {
    val white = Color(0x1AFFFFFF)       // 10% white для великих кіл
    val orange = Color(0x1AF59E0B)      // 10% помаранчевий
    val green = Color(0x1A10B981)       // 10% зелений
}

// ============================================
// COMPATIBILITY LAYER (для існуючого коду)
// ============================================

// PrimaryColors (індиго)
object PrimaryColors {
    val light = Color(0xFFA78BFA)                      // Світлий фіолетовий
    val default = Color(0xFF8B5CF6)                    // Фіолетовий
    val dark = Color(0xFF7C3AED)                       // Темний фіолетовий
    val darker = Color(0xFF6D28D9)                     // Найтемніший фіолетовий
    val glow = Color(0xFF8B5CF6)                       // Для glow ефектів
}

// SecondaryColors (фіолетовий)
object SecondaryColors {
    val light = Color(0xFFA78BFA)                      // Світлий фіолетовий
    val default = Color(0xFF8B5CF6)                    // Фіолетовий
    val dark = Color(0xFF7C3AED)                       // Темний фіолетовий
}

// AccentColors (помаранчевий для CTA)
object AccentColors {
    val light = Color(0xFFFBBF24)                      // Світлий помаранчевий
    val default = Color(0xFFF59E0B)                    // Помаранчевий
    val dark = Color(0xFFD97706)                       // Темний помаранчевий
}

// SuccessColors
object SuccessColors {
    val light = Color(0xFF34D399)                      // Світлий зелений
    val default = Color(0xFF22C55E)                    // Зелений
    val dark = Color(0xFF16A34A)                       // Темний зелений
}

// ErrorColors
object ErrorColors {
    val light = Color(0xFFF87171)                      // Світлий червоний
    val default = Color(0xFFEF4444)                    // Червоний
    val dark = Color(0xFFDC2626)                       // Темний червоний
}

// WarningColors
object WarningColors {
    val light = Color(0xFFFBBF24)                      // Світлий помаранчевий
    val default = Color(0xFFF59E0B)                    // Помаранчевий
    val dark = Color(0xFFD97706)                       // Темний помаранчевий
}

// ProgressBarColors
object ProgressBarColors {
    val trackStart = ProgressColors.trackStart         // #C4B5FD
    val trackEnd = ProgressColors.trackEnd             // #A78BFA
    val fillStart = ProgressColors.fillStart           // #22C55E
    val fillEnd = ProgressColors.fillEnd               // #16A34A
}

// LevelBadgeColors
object LevelBadgeColors {
    val start = TagColors.levelStart                   // #FBBF24
    val end = TagColors.levelEnd                       // #F59E0B
}

// FactBoxColors
object FactBoxColors {
    val start = Color(0x26F59E0B)                      // 15% помаранчевий
    val end = Color(0x338B5CF6)                        // 20% фіолетовий
}

// CardBodyColors
object CardBodyColors {
    val start = Color(0xFFFAFAFC)                      // Світлий градієнт для body
    val end = Color(0xFFF3F4F6)                        // Світлий градієнт для body
}

// IconColors
object IconColors {
    val primary = TextColors.onDarkPrimary             // #FFFFFF
    val secondary = Color(0xFF9CA3AF)                  // Сірий
    val muted = Color(0xFF6B7280)                      // Темно-сірий
    val accent = Color(0xFF8B5CF6)                     // Фіолетовий
    val onPrimary = Color.White                        // Білий
}

// ShadowColors
object ShadowColors {
    val black10 = Color(0x1A000000)                    // 10% black
    val black20 = Color(0x33000000)                    // 20% black
    val black30 = Color(0x4D000000)                    // 30% black
    val black40 = Color(0x66000000)                    // 40% black
    val primary30 = Color(0x4D8B5CF6)                  // 30% primary
    val primary40 = Color(0x668B5CF6)                  // 40% primary
    val secondary40 = Color(0x66F59E0B)                // 40% secondary

    // Glow colors for shadows
    val primaryGlow = Color(0x4D8B5CF6)                // 30% primary
    val accentGlow = Color(0x4DF59E0B)                 // 30% orange
    val purpleGlow = Color(0x4D8B5CF6)                 // 30% purple
    val successGlow = Color(0x4D22C55E)                // 30% green
}

// GlassEffect (alias для GlassColors)
object GlassEffect {
    val backgroundLight = GlassColors.background       // 20% white
    val backgroundMedium = GlassColors.backgroundMedium // 30% white
    val backgroundStrong = Color(0x1AFFFFFF)           // 10% white
    val borderColor = GlassColors.border               // 20% white

    // Додаткові aliases
    val light = GlassColors.backgroundLight            // 40% white
    val medium = GlassColors.backgroundMedium          // 30% white
    val strong = Color(0x1AFFFFFF)                     // 10% white
    val border = GlassColors.border                    // 20% white
}

// ============================================
// LEGACY COMPATIBILITY (для зворотної сумісності)
// ============================================
val Primary = PrimaryColors.default                    // #8B5CF6
val RecordingActive = SemanticColors.error             // #EF4444
val RecordingInactive = Color(0xFF6B7280)              // Сірий
val TextSecondary = Color(0xFF9CA3AF)                  // Сірий
val Success = SemanticColors.success                   // #10B981
val Warning = SemanticColors.warning                   // #F59E0B
val Error = SemanticColors.error                       // #EF4444
