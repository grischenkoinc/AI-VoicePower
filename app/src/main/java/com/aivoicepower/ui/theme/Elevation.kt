package com.aivoicepower.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Elevation System v2.0
 *
 * Джерело: Design_Example_react.md
 * 3D Shadow System з multi-layer shadows
 */

object Elevation {

    // ===== STANDARD ELEVATION LEVELS =====

    val none = 0.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 8.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    // Compatibility aliases
    val level0 = none
    val level1 = xs
    val level2 = sm
    val level3 = md
    val level4 = lg
    val level5 = xl

    // ===== SHADOW PRESETS =====

    /**
     * Main Card Shadow
     * CSS: box-shadow: 0 30px 60px rgba(0, 0, 0, 0.3);
     */
    object Card {
        val elevation = 30.dp
        val color = Color(0x4D000000)  // 30% black
    }

    /**
     * Practice Card Shadow (трохи менша)
     * CSS: box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
     */
    object PracticeCard {
        val elevation = 20.dp
        val color = Color(0x40000000)  // 25% black
    }

    /**
     * Record Button Shadow (з кольоровим glow)
     * Idle: box-shadow: 0 20px 40px rgba(102, 126, 234, 0.4);
     * Active: box-shadow: 0 25px 50px rgba(102, 126, 234, 0.6);
     */
    object RecordButton {
        val idleElevation = 20.dp
        val idleColor = Color(0x66667EEA)  // 40% indigo

        val activeElevation = 25.dp
        val activeColor = Color(0x99667EEA)  // 60% indigo
    }

    /**
     * Nav Button Primary (білий фон)
     * CSS: box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
     * Hover: box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
     */
    object NavButtonPrimary {
        val elevation = 10.dp
        val hoverElevation = 15.dp
        val color = Color(0x33000000)  // 20% black
        val hoverColor = Color(0x4D000000)  // 30% black
    }

    /**
     * Tag Shadow (дуже м'яка)
     * CSS: box-shadow: 0 4px 10px rgba(167, 139, 250, 0.25);
     */
    object Tag {
        val elevation = 4.dp
        val primaryColor = Color(0x40A78BFA)  // 25% purple
        val secondaryColor = Color(0x40F59E0B)  // 25% orange
    }

    /**
     * Progress Bar Glow (зелений glow навколо fill)
     * CSS: box-shadow: 0 0 24px rgba(34, 197, 94, 0.7);
     */
    object ProgressGlow {
        val blurRadius = 24.dp
        val color = Color(0xB322C55E)  // 70% green
    }
}

/**
 * 3D Shadow Data Classes
 * Для multi-layer shadows (inset + regular)
 */

data class MultiLayerShadow(
    val layers: List<ShadowLayer>
)

data class ShadowLayer(
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val blurRadius: Dp = 0.dp,
    val spreadRadius: Dp = 0.dp,
    val color: Color,
    val inset: Boolean = false
)

/**
 * 3D Progress Track Shadow
 * CSS:
 * inset 0 3px 8px rgba(0, 0, 0, 0.3),
 * inset 0 1px 2px rgba(0, 0, 0, 0.4),
 * 0 2px 4px rgba(0, 0, 0, 0.1)
 */
object ProgressTrack3DShadow {
    val shadow = MultiLayerShadow(
        layers = listOf(
            // Inset shadow 1 (основна глибина)
            ShadowLayer(
                offsetY = 3.dp,
                blurRadius = 8.dp,
                color = Color(0x4D000000),  // 30% black
                inset = true
            ),
            // Inset shadow 2 (додаткова глибина)
            ShadowLayer(
                offsetY = 1.dp,
                blurRadius = 2.dp,
                color = Color(0x66000000),  // 40% black
                inset = true
            ),
            // Outer shadow (легка тінь)
            ShadowLayer(
                offsetY = 2.dp,
                blurRadius = 4.dp,
                color = Color(0x1A000000),  // 10% black
                inset = false
            )
        )
    )
}

/**
 * 3D Progress Fill Shadow
 * CSS:
 * box-shadow:
 *   0 0 24px rgba(34, 197, 94, 0.7),
 *   0 3px 6px rgba(34, 197, 94, 0.4),
 *   0 1px 2px rgba(34, 197, 94, 0.5),
 *   inset 0 2px 5px rgba(255, 255, 255, 0.5),
 *   inset 0 -4px 8px rgba(0, 0, 0, 0.3);
 */
object ProgressFill3DShadow {
    val shadow = MultiLayerShadow(
        layers = listOf(
            // Glow (найяскравіший)
            ShadowLayer(
                blurRadius = 24.dp,
                color = Color(0xB322C55E),  // 70% green
                inset = false
            ),
            // Outer shadow 1
            ShadowLayer(
                offsetY = 3.dp,
                blurRadius = 6.dp,
                color = Color(0x6622C55E),  // 40% green
                inset = false
            ),
            // Outer shadow 2
            ShadowLayer(
                offsetY = 1.dp,
                blurRadius = 2.dp,
                color = Color(0x8022C55E),  // 50% green
                inset = false
            ),
            // Top highlight (білий)
            ShadowLayer(
                offsetY = 2.dp,
                blurRadius = 5.dp,
                color = Color(0x80FFFFFF),  // 50% white
                inset = true
            ),
            // Bottom shadow (темний)
            ShadowLayer(
                offsetY = -4.dp,
                blurRadius = 8.dp,
                color = Color(0x4D000000),  // 30% black
                inset = true
            )
        )
    )
}

// ===== COMPATIBILITY LAYER =====

object ShadowPresets {
    // Legacy shadows для існуючого коду
    val card = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.Card.color,
            offset = androidx.compose.ui.geometry.Offset(0f, 30f),
            blurRadius = 60f
        )
    )

    val cardElevated = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x66000000),  // 40% black
            offset = androidx.compose.ui.geometry.Offset(0f, 30f),
            blurRadius = 70f
        ),
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4D8B5CF6),  // 30% primary
            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
            blurRadius = 40f
        )
    )

    val primaryButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4D8B5CF6),  // 30% primary
            offset = androidx.compose.ui.geometry.Offset(0f, 8f),
            blurRadius = 22f
        )
    )

    val ctaButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4DF59E0B),  // 30% orange
            offset = androidx.compose.ui.geometry.Offset(0f, 14f),
            blurRadius = 36f
        )
    )

    val recordButton = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.RecordButton.idleColor,
            offset = androidx.compose.ui.geometry.Offset(0f, 20f),
            blurRadius = 40f
        )
    )

    val recordButtonActive = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.RecordButton.activeColor,
            offset = androidx.compose.ui.geometry.Offset(0f, 25f),
            blurRadius = 50f
        )
    )

    val circularProgress = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4D8B5CF6),  // 30% purple
            offset = androidx.compose.ui.geometry.Offset(0f, 8f),
            blurRadius = 20f
        )
    )

    val successRing = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4D22C55E),  // 30% green
            offset = androidx.compose.ui.geometry.Offset(0f, 14f),
            blurRadius = 42f
        )
    )

    val levelBadge = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.Tag.secondaryColor,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 10f
        )
    )

    val secondaryBadge = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.Tag.primaryColor,
            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
            blurRadius = 10f
        )
    )
}

object ThreeDEffects {
    val progressTrackInset = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Color(0x4D000000),  // 30% black
            offset = androidx.compose.ui.geometry.Offset(0f, 3f),
            blurRadius = 8f
        )
    )

    val progressFillShadows = listOf(
        androidx.compose.ui.graphics.Shadow(
            color = Elevation.ProgressGlow.color,
            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
            blurRadius = 24f
        )
    )
}

object GlassBlur {
    val light = 12.dp
    val medium = 22.dp
    val strong = 32.dp
}
