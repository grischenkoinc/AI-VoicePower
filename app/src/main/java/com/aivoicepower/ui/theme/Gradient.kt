package com.aivoicepower.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * AI VoicePower Gradient System
 * Based on Design Bible v1.0
 * 
 * Градієнти для преміум візуального досвіду
 */

object Gradients {
    
    // ============================================
    // PRIMARY GRADIENTS
    // ============================================
    
    /**
     * Primary gradient — для основних кнопок, хедерів
     * Колір: Індиго → Фіолетовий
     * Використання: Primary buttons, important headers, accents
     */
    val primary = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4F46E5),  // Primary Dark
            Color(0xFF7C3AED)   // Фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    
    /**
     * Primary horizontal — для горизонтальних елементів
     */
    val primaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4F46E5),
            Color(0xFF7C3AED)
        )
    )
    
    /**
     * Primary vertical — для вертикальних елементів
     */
    val primaryVertical = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4F46E5),
            Color(0xFF7C3AED)
        )
    )
    
    // ============================================
    // SECONDARY GRADIENTS (CTA)
    // ============================================
    
    /**
     * Secondary gradient — для CTA, важливих дій
     * Колір: Помаранчевий → Жовтий
     * Використання: "Почати урок", "Записати", CTA buttons
     */
    val secondary = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF97316),  // Orange
            Color(0xFFFBBF24)   // Yellow
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    
    /**
     * Secondary horizontal
     */
    val secondaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF97316),
            Color(0xFFFBBF24)
        )
    )
    
    /**
     * Secondary vertical
     */
    val secondaryVertical = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF97316),
            Color(0xFFFBBF24)
        )
    )
    
    // ============================================
    // SUCCESS GRADIENT
    // ============================================
    
    /**
     * Success gradient — для досягнень, завершення
     * Колір: Зелений → Світло-зелений
     * Використання: Досягнення, 100% прогрес, completed badges
     */
    val success = Brush.linearGradient(
        colors = listOf(
            Color(0xFF10B981),  // Success
            Color(0xFF34D399)   // Success Light
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    
    // ============================================
    // PREMIUM GRADIENT
    // ============================================
    
    /**
     * Premium gradient — для premium фіч
     * Колір: Золотий → Жовтий
     * Використання: Premium badge, paywall, exclusive features
     */
    val premium = Brush.linearGradient(
        colors = listOf(
            Color(0xFFD97706),  // Amber
            Color(0xFFFBBF24)   // Yellow
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    
    /**
     * Premium radial — радіальний градієнт для бейджів
     */
    val premiumRadial = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFBBF24),  // Yellow center
            Color(0xFFD97706)   // Amber edge
        )
    )
    
    // ============================================
    // BACKGROUND GRADIENTS
    // ============================================
    
    /**
     * Background subtle — тонкий фоновий градієнт
     * Використання: Фон екранів для додаткової глибини
     */
    val backgroundSubtle = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF312E81),  // Background Primary
            Color(0xFF3730A3)   // Background Secondary
        )
    )
    
    /**
     * Surface gradient — для карток з градієнтом
     * Більш насичений ніж background
     */
    val surface = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4338CA),  // Surface
            Color(0xFF3730A3)   // Background Secondary
        )
    )
    
    // ============================================
    // SPECIAL EFFECTS
    // ============================================
    
    /**
     * Glow gradient — для glow ефектів
     * Прозорий в центрі, Primary на краях
     */
    val glow = Brush.radialGradient(
        colors = listOf(
            Color(0x00FFFFFF),  // Прозорий центр
            Color(0x4D6366F1)   // 30% Primary на краях
        )
    )
    
    /**
     * Shimmer gradient — для skeleton loading
     */
    val shimmer = Brush.linearGradient(
        colors = listOf(
            Color(0x00FFFFFF),  // Прозорий
            Color(0x1AFFFFFF),  // 10% white
            Color(0x00FFFFFF)   // Прозорий
        ),
        start = Offset(-300f, -300f),
        end = Offset(300f, 300f)
    )
    
    /**
     * Record button gradient — для кнопки запису
     * Secondary колір з яскравим акцентом
     */
    val recordButton = Brush.radialGradient(
        colors = listOf(
            Color(0xFFF97316),  // Orange center
            Color(0xFFEA580C)   // Darker orange edge
        )
    )
}

/**
 * Таблиця використання градієнтів:
 * 
 * PRIMARY              - Основні кнопки, важливі header accents
 * SECONDARY            - CTA кнопки ("Почати урок", "Записати")
 * SUCCESS              - Досягнення, 100% прогрес, completed states
 * PREMIUM              - Premium badge, paywall, exclusive features
 * BACKGROUND_SUBTLE    - Фони екранів (subtle depth)
 * SURFACE              - Картки з градієнтом (більш виражений)
 * GLOW                 - Glow ефекти навколо активних елементів
 * SHIMMER              - Skeleton loading states
 * RECORD_BUTTON        - Кнопка запису
 * 
 * ВАЖЛИВО:
 * - НЕ використовуй градієнти на всіх кнопках підряд
 * - Primary gradient тільки для головних CTA
 * - Secondary gradient для критичних дій ("Почати", "Записати")
 * - Background gradient — тонкий, не відволікає від контенту
 */

/**
 * ПРИКЛАДИ ВИКОРИСТАННЯ:
 * 
 * ```kotlin
 * // Primary button з градієнтом
 * Button(
 *     onClick = { },
 *     modifier = Modifier.background(Gradients.primary, shape = RoundedCornerShape(12.dp))
 * ) {
 *     Text("Почати")
 * }
 * 
 * // Фон екрану з тонким градієнтом
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .background(Gradients.backgroundSubtle)
 * ) {
 *     // Content
 * }
 * 
 * // Premium badge
 * Box(
 *     modifier = Modifier
 *         .size(80.dp)
 *         .background(Gradients.premiumRadial, shape = CircleShape)
 * ) {
 *     Icon(Icons.Default.Star, tint = Color.White)
 * }
 * ```
 */
