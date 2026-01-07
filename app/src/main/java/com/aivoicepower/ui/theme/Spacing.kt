package com.aivoicepower.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Spacing System
 * Based on Design Bible v1.0
 * 
 * Консистентна система відступів для всього застосунку
 */

object Spacing {
    // ============================================
    // ОСНОВНІ ЗНАЧЕННЯ
    // ============================================
    val xxs: Dp = 2.dp    // Мінімальні відступи
    val xs: Dp = 4.dp     // Між елементами в групі
    val sm: Dp = 8.dp     // Внутрішній padding малих елементів
    val md: Dp = 16.dp    // Стандартний padding (найчастіше)
    val lg: Dp = 24.dp    // Між секціями
    val xl: Dp = 32.dp    // Великі відступи
    val xxl: Dp = 48.dp   // Padding екранів
    val xxxl: Dp = 64.dp  // Особливі випадки
    
    // ============================================
    // СЕМАНТИЧНІ ALIAS
    // ============================================
    
    // Screen padding
    val screenHorizontal: Dp = md  // 16dp - padding від країв екрану
    val screenVertical: Dp = md    // 16dp - padding зверху/знизу екрану
    val screenTop: Dp = xxl        // 48dp - для екранів з великим верхнім padding
    
    // Section spacing
    val sectionSpacing: Dp = lg    // 24dp - між секціями на екрані
    val betweenSections: Dp = lg   // 24dp - альтернативна назва
    
    // Card spacing
    val cardPadding: Dp = md       // 16dp - padding всередині картки
    val cardSpacing: Dp = sm       // 8dp - між картками в списку
    
    // List item spacing
    val listItemSpacing: Dp = sm   // 8dp - між елементами списку
    val listItemPadding: Dp = md   // 16dp - padding елемента списку
    
    // Button spacing
    val buttonPaddingHorizontal: Dp = lg  // 24dp
    val buttonPaddingVertical: Dp = sm    // 8dp
    val buttonSpacing: Dp = sm            // 8dp - між кнопками
    
    // Icon spacing
    val iconTextSpacing: Dp = xs   // 4dp - між іконкою та текстом
    val iconPadding: Dp = sm       // 8dp - padding навколо іконки
    
    // Bottom navigation
    val bottomNavHeight: Dp = 80.dp
    val bottomNavPadding: Dp = sm  // 8dp
    
    // FAB positioning
    val fabPadding: Dp = md        // 16dp
    val fabBottomPadding: Dp = md + bottomNavHeight  // 16 + 80 = 96dp
    
    // Dialog spacing
    val dialogPadding: Dp = lg     // 24dp
    val dialogSpacing: Dp = md     // 16dp - між елементами в діалозі
}

/**
 * Таблиця використання:
 * 
 * XXS (2dp)  - Мінімальні відступи, тонкі dividers
 * XS (4dp)   - Між іконкою та текстом, між елементами в тісній групі
 * SM (8dp)   - Padding малих кнопок, між елементами списку, між картками
 * MD (16dp)  - Стандартний padding (екрани, картки, елементи)
 * LG (24dp)  - Між секціями, padding великих кнопок
 * XL (32dp)  - Великі відступи між major sections
 * XXL (48dp) - Padding екранів з великим breathing space
 * XXXL (64dp) - Рідкісні випадки, splash screen
 */

/**
 * Extension functions для зручності
 */

// Vertical spacing
fun Dp.vertical() = this

// Horizontal spacing  
fun Dp.horizontal() = this
