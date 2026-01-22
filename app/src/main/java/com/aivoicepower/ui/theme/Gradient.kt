package com.aivoicepower.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * AI VoicePower Gradient System v2.0
 *
 * Джерело: Design_Example_react.md
 * Всі градієнти з еталонного дизайну
 */

object Gradients {

    // ===== PRIMARY GRADIENTS =====

    /**
     * Основний фон застосунку
     * CSS: linear-gradient(135deg, #4ECDC4 0%, #667eea 50%, #764ba2 100%)
     */
    val appBackground = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4ECDC4),  // Бірюзовий
            Color(0xFF667EEA),  // Світло-фіолетовий
            Color(0xFF764BA2)   // Темно-фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // 135° діагональ
    )

    /**
     * Theory Card Header (темна шапка)
     * CSS: linear-gradient(135deg, #3d266a, #1f1f2e)
     */
    val cardHeaderTheory = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3D266A),  // Темний фіолетовий
            Color(0xFF1F1F2E)   // Майже чорний
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    /**
     * Practice Card Header (альтернативна темна шапка)
     * CSS: linear-gradient(135deg, #3d266a, #1f1f2e)
     */
    val cardHeaderPractice = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3D266A),  // Темний фіолетовий (як у теорії)
            Color(0xFF1F1F2E)   // Майже чорний
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ===== TAG GRADIENTS =====

    /**
     * Primary Tag (Section Tag — 📖 Теорія)
     * CSS: linear-gradient(135deg, #a78bfa, #7c3aed)
     */
    val tagPrimary = Brush.linearGradient(
        colors = listOf(
            Color(0xFFA78BFA),  // Світлий фіолетовий
            Color(0xFF7C3AED)   // Фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    /**
     * Secondary Tag (Practice Tag — 🔥 Практика)
     * CSS: linear-gradient(135deg, #f59e0b, #d97706)
     */
    val tagSecondary = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF59E0B),  // Помаранчевий
            Color(0xFFD97706)   // Темний помаранчевий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    /**
     * Level Pill (⚡ Рівень 3)
     * CSS: linear-gradient(135deg, #fbbf24, #f59e0b)
     */
    val levelPill = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFBBF24),  // Жовтий
            Color(0xFFF59E0B)   // Помаранчевий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ===== PROGRESS BAR GRADIENTS =====

    /**
     * Progress Track (180° вертикальний)
     * CSS: linear-gradient(180deg, #c4b5fd 0%, #a78bfa 100%)
     */
    val progressTrack = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF5F3FF),  // Майже білий з фіолетовим відтінком
            Color(0xFFE9E5FF)   // Світло-світло-фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY) // Вертикальний (180°)
    )

    /**
     * Progress Fill (90° горизонтальний)
     * CSS: linear-gradient(90deg, #22c55e, #16a34a)
     */
    val progressFill = Brush.linearGradient(
        colors = listOf(
            Color(0xFF22C55E),  // Яскравий зелений
            Color(0xFF16A34A)   // Темний зелений
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f) // Горизонтальний (90°)
    )

    // ===== BUTTON GRADIENTS =====

    /**
     * Record Button (ідентичний app background)
     * CSS: linear-gradient(135deg, #667eea, #764ba2)
     */
    val recordButton = appBackground // Використовуємо той самий градієнт

    // ===== SPECIAL GRADIENTS =====

    /**
     * Section Background (для внутрішніх секцій)
     * CSS: linear-gradient(135deg, #f3f4f6, #e5e7eb)
     */
    val sectionBackground = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF3F4F6),  // Майже білий
            Color(0xFFE5E7EB)   // Світло-сірий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    /**
     * Highlight Box (15% помаранчевий → 20% фіолетовий)
     * Для блоків з важливою інформацією
     */
    val highlightBox = Brush.linearGradient(
        colors = listOf(
            Color(0x26F59E0B),  // 15% помаранчевий
            Color(0x338B5CF6)   // 20% фіолетовий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ===== SHIMMER/GLOW EFFECTS =====

    /**
     * Shimmer effect для progress fill
     * Світлий градієнт на краю для glow ефекту
     */
    val progressShimmer = Brush.linearGradient(
        colors = listOf(
            Color(0x00FFFFFF),  // Прозорий
            Color(0x8CFFFFFF)   // 55% білий
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

    /**
     * Top highlight для 3D progress fill
     * Gradient поверх fill для 3D ефекту
     */
    val progressTopHighlight = Brush.linearGradient(
        colors = listOf(
            Color(0x99FFFFFF),  // 60% білий
            Color(0x00FFFFFF)   // Прозорий
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    /**
     * Top highlight для 3D progress track
     * Gradient поверх track для 3D ефекту
     */
    val progressTrackHighlight = Brush.linearGradient(
        colors = listOf(
            Color(0x80FFFFFF),  // 50% білий
            Color(0x00FFFFFF)   // Прозорий
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    // ===== COMPATIBILITY LAYER =====

    // Legacy aliases для існуючого коду
    val background = appBackground
    val cardHeader = cardHeaderTheory
    val cardBody = sectionBackground
    val primaryButton = appBackground
    val secondaryBadge = tagPrimary
    val ctaButton = tagSecondary
    val success = progressFill
    val levelBadge = levelPill
    val factBox = highlightBox
    val timer = tagPrimary
    val scoreRing = progressFill
    val premium = levelPill
}
