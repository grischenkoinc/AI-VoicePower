package com.aivoicepower.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ===== ELEVATION LEVELS =====
object Elevation {
    val level0 = 0.dp   // Flat
    val level1 = 2.dp   // Subtle lift
    val level2 = 4.dp   // Cards
    val level3 = 8.dp   // Raised cards
    val level4 = 16.dp  // Dropdowns
    val level5 = 24.dp  // Modals
}

// ===== SHADOW PRESETS =====
object ShadowPresets {

    // ===== CARD SHADOWS (темні картки на темному фоні) =====

    // Звичайна картка
    val card = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black40,  // 40% black
            offset = androidx.compose.ui.geometry.Offset(0f, 24f),
            blurRadius = 60f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black10,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 8f
        )
    )

    // Картка з підсвіткою (hover/active)
    val cardElevated = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black40,
            offset = androidx.compose.ui.geometry.Offset(0f, 30f),
            blurRadius = 70f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.primaryGlow,  // 30% primary
            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
            blurRadius = 40f
        )
    )

    // ===== BUTTON SHADOWS =====

    // Primary button (синій з glow)
    val primaryButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.primaryGlow,  // 30% primary
            offset = androidx.compose.ui.geometry.Offset(0f, 8f),
            blurRadius = 22f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black20,
            offset = androidx.compose.ui.geometry.Offset(0f, 2f),
            blurRadius = 5f
        )
    )

    // CTA button (помаранчевий з сильним glow)
    val ctaButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.accentGlow,  // 30% orange
            offset = androidx.compose.ui.geometry.Offset(0f, 14f),
            blurRadius = 36f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black20,
            offset = androidx.compose.ui.geometry.Offset(0f, 3f),
            blurRadius = 8f
        )
    )

    // ===== RECORD BUTTON SHADOWS =====

    // Idle state
    val recordButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.primaryGlow,
            offset = androidx.compose.ui.geometry.Offset(0f, 10f),
            blurRadius = 28f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black20,
            offset = androidx.compose.ui.geometry.Offset(0f, 3f),
            blurRadius = 8f
        )
    )

    // Recording state (пульсуючий)
    val recordButtonActive = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.primaryGlow,
            offset = androidx.compose.ui.geometry.Offset(0f, 14f),
            blurRadius = 42f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black30,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 10f
        )
    )

    // ===== PROGRESS RING SHADOWS =====

    val circularProgress = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.purpleGlow,  // 30% purple
            offset = androidx.compose.ui.geometry.Offset(0f, 8f),
            blurRadius = 20f
        )
    )

    val successRing = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.successGlow,  // 30% green
            offset = androidx.compose.ui.geometry.Offset(0f, 14f),
            blurRadius = 42f
        )
    )

    // ===== BADGE SHADOWS =====

    val levelBadge = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.accentGlow,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 10f
        )
    )

    val secondaryBadge = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.purpleGlow,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 10f
        )
    )
}

// ===== 3D EFFECT PARAMETERS (для прогрес-бару) =====
object ThreeDEffects {

    // Inset shadows для track (впадина)
    val progressTrackInset = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black30,
            offset = androidx.compose.ui.geometry.Offset(0f, 3f),
            blurRadius = 8f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.black40,
            offset = androidx.compose.ui.geometry.Offset(0f, 1f),
            blurRadius = 2f
        )
    )

    // Shadows для fill (випуклість + glow)
    val progressFillShadows = listOf(
        // Outer glow
        androidx.compose.ui.graphics.Shadow(
            color = ShadowColors.successGlow,  // 30% green
            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
            blurRadius = 24f
        ),
        // Drop shadow
        androidx.compose.ui.graphics.Shadow(
            color = SuccessColors.default.copy(alpha = 0.4f),
            offset = androidx.compose.ui.geometry.Offset(0f, 3f),
            blurRadius = 6f
        ),
        // Subtle bottom shadow
        androidx.compose.ui.graphics.Shadow(
            color = SuccessColors.default.copy(alpha = 0.5f),
            offset = androidx.compose.ui.geometry.Offset(0f, 1f),
            blurRadius = 2f
        )
    )
}

// ===== HELPER: Blur radius для glassmorphism =====
object GlassBlur {
    val light = 12.dp
    val medium = 22.dp
    val strong = 32.dp
}
