package com.aivoicepower.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset

/**
 * AI VoicePower Elevation & Shadow System
 * Based on Design Bible v1.0
 * 
 * Multi-layer shadows для створення глибини та преміум відчуття
 */

// ============================================
// ELEVATION LEVELS
// ============================================
object Elevation {
    val level0: Dp = 0.dp   // Flat — немає elevation
    val level1: Dp = 2.dp   // Subtle lift — легке підняття
    val level2: Dp = 4.dp   // Cards — стандартні картки
    val level3: Dp = 8.dp   // Raised cards, FAB — підняті елементи
    val level4: Dp = 16.dp  // Dropdowns, menus — floating елементи
    val level5: Dp = 24.dp  // Modals, dialogs — верхній рівень
}

// ============================================
// SHADOW DEFINITIONS (Multi-layer)
// ============================================
object Shadows {
    
    /**
     * Звичайна картка
     * Використання: Course cards, lesson cards, тощо
     */
    val card = listOf(
        Shadow(
            color = ShadowColors.black30,  // 30% black
            offset = Offset(0f, 4f),
            blurRadius = 6f
        ),
        Shadow(
            color = ShadowColors.black20,  // 20% black
            offset = Offset(0f, 10f),
            blurRadius = 20f
        )
    )
    
    /**
     * Підсвічена картка (active/selected)
     * Використання: Selected course, active lesson
     */
    val elevatedGlow = listOf(
        Shadow(
            color = ShadowColors.black30,
            offset = Offset(0f, 4f),
            blurRadius = 6f
        ),
        Shadow(
            color = ShadowColors.black20,
            offset = Offset(0f, 10f),
            blurRadius = 20f
        ),
        Shadow(
            color = ShadowColors.primary30,  // 30% primary glow
            offset = Offset(0f, 0f),
            blurRadius = 30f
        )
    )
    
    /**
     * Primary кнопка
     * Використання: Основні action кнопки
     */
    val primaryButton = listOf(
        Shadow(
            color = ShadowColors.primary30,  // 30% primary
            offset = Offset(0f, 4f),
            blurRadius = 12f
        )
    )
    
    /**
     * CTA (Secondary) кнопка
     * Використання: "Почати урок", "Записати"
     */
    val ctaButton = listOf(
        Shadow(
            color = ShadowColors.secondary40,  // 40% secondary
            offset = Offset(0f, 4f),
            blurRadius = 15f
        )
    )
    
    /**
     * Subtle shadow для малих елементів
     * Використання: Chips, small buttons
     */
    val subtle = listOf(
        Shadow(
            color = ShadowColors.black20,
            offset = Offset(0f, 2f),
            blurRadius = 4f
        )
    )
    
    /**
     * Modal/Dialog shadow
     * Використання: Модальні вікна, діалоги
     */
    val modal = listOf(
        Shadow(
            color = ShadowColors.black30,
            offset = Offset(0f, 10f),
            blurRadius = 30f
        ),
        Shadow(
            color = ShadowColors.black20,
            offset = Offset(0f, 20f),
            blurRadius = 60f
        )
    )
    
    /**
     * FAB (Floating Action Button) shadow
     * Використання: AI Coach FAB
     */
    val fab = listOf(
        Shadow(
            color = ShadowColors.black30,
            offset = Offset(0f, 6f),
            blurRadius = 10f
        ),
        Shadow(
            color = ShadowColors.primary30,
            offset = Offset(0f, 0f),
            blurRadius = 20f
        )
    )
    
    /**
     * Bottom sheet shadow
     * Використання: Bottom sheets
     */
    val bottomSheet = listOf(
        Shadow(
            color = ShadowColors.black30,
            offset = Offset(0f, -4f),  // Shadow вгору
            blurRadius = 15f
        )
    )
}

/**
 * Таблиця використання тіней:
 * 
 * CARD              - Звичайні картки (course cards, lesson items)
 * ELEVATED_GLOW     - Активні/вибрані картки з glow ефектом
 * PRIMARY_BUTTON    - Основні кнопки (primary color)
 * CTA_BUTTON        - CTA кнопки (secondary color) - "Почати", "Записати"
 * SUBTLE            - Малі елементи (chips, tags)
 * MODAL             - Модальні вікна, діалоги
 * FAB               - Floating action buttons
 * BOTTOM_SHEET      - Bottom sheets
 */

/**
 * Extension function для застосування multi-layer shadow в Compose
 * 
 * ПРИМІТКА: Jetpack Compose наразі не підтримує multiple shadows на одному елементі нативно.
 * Для багатошарових тіней потрібно використовувати:
 * 1. Modifier.drawBehind з Canvas
 * 2. Або стекати декілька Box один на одному з різними shadows
 * 3. Або використовувати elevation (але це дає тільки одну тінь)
 * 
 * TODO: Створити custom Modifier.multiLayerShadow() в окремому файлі Modifiers.kt
 */
