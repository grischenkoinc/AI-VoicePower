package com.aivoicepower.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Shape System
 * Based on Design Bible v1.0
 * 
 * Використовується для corner radius елементів
 */

// ============================================
// CORNER RADIUS VALUES
// ============================================
object CornerRadius {
    val xs = 4.dp      // Tags, маленькі елементи
    val sm = 8.dp      // Chips, малі кнопки
    val md = 12.dp     // Кнопки, inputs
    val lg = 16.dp     // Картки
    val xl = 24.dp     // Bottom sheets, великі картки
    val xxl = 32.dp    // Модальні вікна
    val full = 100.dp  // Круглі елементи, pills
}

// ============================================
// MATERIAL 3 SHAPES
// ============================================
val AppShapes = Shapes(
    // Extra Small - для маленьких елементів (tags, badges)
    extraSmall = RoundedCornerShape(CornerRadius.xs),
    
    // Small - для chips, малих кнопок
    small = RoundedCornerShape(CornerRadius.sm),
    
    // Medium - для кнопок, inputs (найчастіше використовується)
    medium = RoundedCornerShape(CornerRadius.md),
    
    // Large - для карток
    large = RoundedCornerShape(CornerRadius.lg),
    
    // Extra Large - для bottom sheets, великих карток
    extraLarge = RoundedCornerShape(CornerRadius.xl)
)

/**
 * Кастомні shapes для специфічних випадків
 */
object CustomShapes {
    // Modal діалоги
    val modal = RoundedCornerShape(CornerRadius.xxl)
    
    // Pill-подібні елементи (streak counter, tags)
    val pill = RoundedCornerShape(CornerRadius.full)
    
    // Bottom sheet з округленням тільки зверху
    val bottomSheet = RoundedCornerShape(
        topStart = CornerRadius.xl,
        topEnd = CornerRadius.xl,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Record button (коло)
    val circle = RoundedCornerShape(50) // 50% = circle
    
    // Картка з різними радіусами (для спеціальних випадків)
    fun asymmetric(
        topStart: androidx.compose.ui.unit.Dp = CornerRadius.lg,
        topEnd: androidx.compose.ui.unit.Dp = CornerRadius.lg,
        bottomStart: androidx.compose.ui.unit.Dp = CornerRadius.lg,
        bottomEnd: androidx.compose.ui.unit.Dp = CornerRadius.lg
    ) = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd
    )
}

/**
 * Таблиця використання:
 * 
 * XS (4dp)   - Tags, маленькі badges
 * SM (8dp)   - Chips, малі кнопки
 * MD (12dp)  - Кнопки, inputs, text fields
 * LG (16dp)  - Картки, course cards
 * XL (24dp)  - Bottom sheets, великі діалоги
 * XXL (32dp) - Модальні вікна
 * FULL (100dp) - Circular елементи, pills, streak counter
 */
