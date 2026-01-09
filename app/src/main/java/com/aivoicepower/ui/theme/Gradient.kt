package com.aivoicepower.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {

    // ===== BACKGROUND (темний з переходами) =====
    val background = Brush.verticalGradient(
        colors = listOf(
            BackgroundColors.primary,    // #2d2640
            BackgroundColors.secondary,  // #1a1625
            BackgroundColors.tertiary    // #0f0d1a
        )
    )

    // ===== CARD HEADER (темна фіолетова шапка) =====
    val cardHeader = Brush.linearGradient(
        colors = listOf(
            CardHeaderColors.start,  // #3d266a
            CardHeaderColors.end     // #1f1f2e
        )
    )

    // ===== CARD BODY (світлий градієнт) =====
    val cardBody = Brush.verticalGradient(
        colors = listOf(
            CardBodyColors.start,  // #fafafc
            CardBodyColors.end     // #f3f4f6
        )
    )

    // ===== PRIMARY BUTTON (синій індиго) =====
    val primaryButton = Brush.linearGradient(
        colors = listOf(
            PrimaryColors.default,  // #6366f1
            PrimaryColors.dark      // #4f46e5
        )
    )

    // ===== SECONDARY BADGE (фіолетовий) =====
    val secondaryBadge = Brush.linearGradient(
        colors = listOf(
            SecondaryColors.light,   // #a78bfa
            SecondaryColors.dark     // #7c3aed
        )
    )

    // ===== CTA BUTTON (помаранчевий) =====
    val ctaButton = Brush.linearGradient(
        colors = listOf(
            AccentColors.default,  // #f59e0b
            AccentColors.dark      // #d97706
        )
    )

    // ===== SUCCESS/PROGRESS (зелений) =====
    val success = Brush.linearGradient(
        colors = listOf(
            SuccessColors.default,  // #22c55e
            SuccessColors.dark      // #16a34a
        )
    )

    // ===== PROGRESS BAR TRACK (світлий фіолетовий) =====
    val progressTrack = Brush.verticalGradient(
        colors = listOf(
            ProgressBarColors.trackStart,  // #c4b5fd
            ProgressBarColors.trackEnd     // #a78bfa
        )
    )

    // ===== PROGRESS BAR FILL (зелений 3D) =====
    val progressFill = Brush.horizontalGradient(
        colors = listOf(
            ProgressBarColors.fillStart,  // #22c55e
            ProgressBarColors.fillEnd     // #16a34a
        )
    )

    // ===== LEVEL BADGE (жовто-помаранчевий) =====
    val levelBadge = Brush.linearGradient(
        colors = listOf(
            LevelBadgeColors.start,  // #fbbf24
            LevelBadgeColors.end     // #f59e0b
        )
    )

    // ===== FACT BOX (помаранчевий → фіолетовий) =====
    val factBox = Brush.linearGradient(
        colors = listOf(
            FactBoxColors.start,  // rgba(245, 158, 11, 0.15)
            FactBoxColors.end     // rgba(139, 92, 246, 0.2)
        )
    )

    // ===== RECORD BUTTON (синій як primary) =====
    val recordButton = Brush.linearGradient(
        colors = listOf(
            PrimaryColors.default,  // #6366f1
            PrimaryColors.dark      // #4f46e5
        )
    )

    // ===== TIMER GRADIENT (фіолетовий для SVG) =====
    val timer = Brush.linearGradient(
        colors = listOf(
            SecondaryColors.light,  // #a78bfa
            SecondaryColors.dark    // #7c3aed
        )
    )

    // ===== SCORE RING (зелений для результатів) =====
    val scoreRing = Brush.linearGradient(
        colors = listOf(
            SuccessColors.default,  // #22c55e
            SuccessColors.dark      // #16a34a
        )
    )

    // ===== PREMIUM BADGE (золотий градієнт) =====
    val premium = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700),  // Gold
            Color(0xFFFFA500)   // Orange gold
        )
    )
}
