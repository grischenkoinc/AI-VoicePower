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
    val primary = Color(0xFF312E81)       // Основний фон екранів, splash screen, overlay
    val secondary = Color(0xFF3730A3)     // Чергування секцій, фон списків
    val surface = Color(0xFF4338CA)       // Картки, inputs, chips
    val surfaceElevated = Color(0xFF5B52E0) // Модальні вікна, dropdown, bottom sheet
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
// SECONDARY (Коралово-помаранчевий)
// ============================================
object SecondaryColors {
    val light = Color(0xFFFB923C)      // Highlight, badges
    val default = Color(0xFFF97316)    // CTA кнопки, важливі дії
    val dark = Color(0xFFEA580C)       // Hover/pressed
}

// ============================================
// ACCENT (Бірюзовий)
// ============================================
object AccentColors {
    val light = Color(0xFF5EEAD4)
    val default = Color(0xFF14B8A6)    // Прогрес, успіх, streak
    val dark = Color(0xFF0D9488)
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
// TEXT COLORS
// ============================================
object TextColors {
    val primary = Color(0xFFF9FAFB)     // Заголовки, основний контент
    val secondary = Color(0xFF9CA3AF)   // Описи, підписи
    val muted = Color(0xFF6B7280)       // Hints, placeholder
    val onPrimary = Color(0xFFFFFFFF)   // На primary кнопках
    val onSecondary = Color(0xFFFFFFFF) // На secondary кнопках
}

// ============================================
// BORDER COLORS
// ============================================
object BorderColors {
    val subtle = Color(0x0FFFFFFF)      // 6% white — тонкі межі
    val default = Color(0x1AFFFFFF)     // 10% white — стандартні межі
    val accent = Color(0x4D6366F1)      // 30% primary — акцентні межі
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
    val black30 = Color(0x4D000000)     // 30% black
    val black20 = Color(0x33000000)     // 20% black
    val primary30 = Color(0x4D6366F1)   // 30% primary
    val primary40 = Color(0x666366F1)   // 40% primary
    val secondary40 = Color(0x66F97316) // 40% secondary
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
