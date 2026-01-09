package com.aivoicepower.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AI VoicePower Color System
 * Based on Design Bible v1.0
 * 
 * Філософія: Індиго-based Dark Theme з преміум якістю
 */

// ============================================
// BACKGROUNDS (Індиго-based Dark)
// ============================================
object BackgroundColors {
    val primary = Color(0xFF2D2640)       // Основний фон екранів
    val secondary = Color(0xFF1A1625)     // Чергування секцій, фон списків
    val tertiary = Color(0xFF0F0D1A)      // Найтемніший фон
    val surface = Color(0xFF4338CA)       // Картки, inputs, chips
    val surfaceElevated = Color(0xFF5B52E0) // Модальні вікна, dropdown, bottom sheet
}

// ============================================
// CARD COLORS
// ============================================
object CardHeaderColors {
    val start = Color(0xFF3D266A)         // Темна фіолетова шапка
    val end = Color(0xFF1F1F2E)
}

object CardBodyColors {
    val start = Color(0xFFFAFAFC)          // Світлий градієнт для body
    val end = Color(0xFFF3F4F6)
}

// ============================================
// PRIMARY (Індиго)
// ============================================
object PrimaryColors {
    val light = Color(0xFF818CF8)      // Іконки неактивні, subtle accents
    val default = Color(0xFF6366F1)    // Кнопки, активні елементи, посилання
    val dark = Color(0xFF4F46E5)       // Hover state
    val darker = Color(0xFF4338CA)     // Pressed state
    val glow = Color(0xFF6366F1)       // Для glow ефектів (з blur)
}

// ============================================
// SECONDARY (Фіолетовий)
// ============================================
object SecondaryColors {
    val light = Color(0xFFA78BFA)      // Highlight, badges
    val default = Color(0xFF8B5CF6)    // Secondary елементи
    val dark = Color(0xFF7C3AED)       // Hover/pressed
}

// ============================================
// ACCENT (Помаранчевий для CTA)
// ============================================
object AccentColors {
    val light = Color(0xFFFBBF24)      // Світлий помаранчевий
    val default = Color(0xFFF59E0B)    // CTA кнопки
    val dark = Color(0xFFD97706)       // Hover/pressed
}

// ============================================
// SUCCESS COLORS
// ============================================
object SuccessColors {
    val light = Color(0xFF34D399)
    val default = Color(0xFF22C55E)    // Успіх, прогрес
    val dark = Color(0xFF16A34A)
}

// ============================================
// PROGRESS BAR COLORS
// ============================================
object ProgressBarColors {
    val trackStart = Color(0xFFC4B5FD)  // Світлий фіолетовий трек
    val trackEnd = Color(0xFFA78BFA)
    val fillStart = Color(0xFF22C55E)   // Зелений fill
    val fillEnd = Color(0xFF16A34A)
}

// ============================================
// LEVEL BADGE COLORS
// ============================================
object LevelBadgeColors {
    val start = Color(0xFFFBBF24)       // Жовто-помаранчевий
    val end = Color(0xFFF59E0B)
}

// ============================================
// FACT BOX COLORS
// ============================================
object FactBoxColors {
    val start = Color(0x26F59E0B)       // rgba(245, 158, 11, 0.15)
    val end = Color(0x338B5CF6)         // rgba(139, 92, 246, 0.2)
}

// ============================================
// SEMANTIC COLORS
// ============================================
object SemanticColors {
    // Success
    val success = Color(0xFF10B981)
    val successLight = Color(0xFF34D399)

    // Warning
    val warning = Color(0xFFF59E0B)
    val warningLight = Color(0xFFFBBF24)

    // Error
    val error = Color(0xFFEF4444)
    val errorLight = Color(0xFFF87171)

    // Info
    val info = Color(0xFF3B82F6)
    val infoLight = Color(0xFF60A5FA)
}

// ============================================
// ERROR COLORS
// ============================================
object ErrorColors {
    val light = Color(0xFFF87171)
    val default = Color(0xFFEF4444)
    val dark = Color(0xFFDC2626)
}

// ============================================
// WARNING COLORS
// ============================================
object WarningColors {
    val light = Color(0xFFFBBF24)
    val default = Color(0xFFF59E0B)
    val dark = Color(0xFFD97706)
}

// ============================================
// TEXT COLORS
// ============================================
object TextColors {
    val primary = Color(0xFFF9FAFB)     // Заголовки, основний контент
    val secondary = Color(0xFF9CA3AF)   // Описи, підписи
    val tertiary = Color(0xFF6B7280)    // Meta info, icons, hints
    val muted = Color(0xFF6B7280)       // Hints, placeholder
    val onPrimary = Color(0xFFFFFFFF)   // На primary кнопках
    val onSecondary = Color(0xFFFFFFFF) // На secondary кнопках
}

// ============================================
// BORDER COLORS
// ============================================
object BorderColors {
    val subtle = Color(0x0FFFFFFF)      // 6% white — тонкі межі
    val light = Color(0x0FFFFFFF)       // 6% white — alias for subtle
    val default = Color(0x1AFFFFFF)     // 10% white — стандартні межі
    val border = Color(0x1AFFFFFF)      // 10% white — alias for default
    val accent = Color(0x4D6366F1)      // 30% primary — акцентні межі
    val primary = Color(0xFF6366F1)     // Primary color for borders
}

// ============================================
// GLASSMORPHISM
// ============================================
object GlassEffect {
    val backgroundLight = Color(0x0DFFFFFF)   // 5% white
    val backgroundMedium = Color(0x14FFFFFF)  // 8% white
    val backgroundStrong = Color(0x1AFFFFFF)  // 10% white

    val borderColor = Color(0x1AFFFFFF)       // 10% white
}

// Alias for easier access
object GlassColors {
    val light = GlassEffect.backgroundLight
    val medium = GlassEffect.backgroundMedium
    val strong = GlassEffect.backgroundStrong
    val border = GlassEffect.borderColor
}

// ============================================
// ICON COLORS
// ============================================
object IconColors {
    val primary = TextColors.primary        // #F9FAFB
    val secondary = TextColors.secondary    // #9CA3AF
    val muted = TextColors.muted           // #6B7280
    val accent = PrimaryColors.default      // #6366F1
    val onPrimary = Color.White
}

// ============================================
// SHADOW COLORS (for multi-layer shadows)
// ============================================
object ShadowColors {
    val black10 = Color(0x1A000000)     // 10% black
    val black20 = Color(0x33000000)     // 20% black
    val black30 = Color(0x4D000000)     // 30% black
    val black40 = Color(0x66000000)     // 40% black
    val primary30 = Color(0x4D6366F1)   // 30% primary
    val primary40 = Color(0x666366F1)   // 40% primary
    val secondary40 = Color(0x66F97316) // 40% secondary

    // Glow colors for shadows
    val primaryGlow = Color(0x4D6366F1)   // 30% primary (#6366F1)
    val accentGlow = Color(0x4DF59E0B)    // 30% orange (#F59E0B)
    val purpleGlow = Color(0x4D8B5CF6)    // 30% purple (#8B5CF6)
    val successGlow = Color(0x4D22C55E)   // 30% green (#22C55E)
}

// ============================================
// LEGACY COMPATIBILITY (для зворотної сумісності)
// ============================================
val Primary = PrimaryColors.default
val RecordingActive = SemanticColors.error
val RecordingInactive = TextColors.muted
val TextSecondary = TextColors.secondary
val Success = SemanticColors.success
val Warning = SemanticColors.warning
val Error = SemanticColors.error
