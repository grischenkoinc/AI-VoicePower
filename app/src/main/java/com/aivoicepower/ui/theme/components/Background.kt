package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Background Components v2.0
 *
 * Джерело: Design_Example_react.md
 * Градієнтний фон + геометричні фігури
 */

/**
 * Gradient Background з геометричними фігурами
 * CSS: .v3-app + .geometric-bg
 *
 * Основний фон застосунку
 */
@Composable
fun GradientBackground(
    content: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    // Статус бар налаштовується глобально в Theme.kt і MainActivity.kt
    // Не перезаписуємо тут, щоб уникнути конфліктів

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Gradients.appBackground)
    ) {
        // Geometric shapes (фонові кола)
        GeometricShapes()

        // Content поверх фону
        content()
    }
}

/**
 * Geometric Shapes (3 фонові кола)
 * CSS: .geometric-bg + .geo-shape
 */
@Composable
private fun GeometricShapes() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Shape 1: White circle (top-right)
        GeoShape(
            size = 300.dp,
            color = Color.White.copy(alpha = 0.08f),
            offsetX = (-80).dp,
            offsetY = (-100).dp,
            alignment = androidx.compose.ui.Alignment.TopEnd
        )

        // Shape 2: Orange circle (bottom-left)
        GeoShape(
            size = 200.dp,
            color = Color(0xFFF59E0B).copy(alpha = 0.08f),
            offsetX = (-50).dp,
            offsetY = 50.dp,
            alignment = androidx.compose.ui.Alignment.BottomStart
        )

        // Shape 3: Green circle (mid-left)
        GeoShape(
            size = 150.dp,
            color = Color(0xFF10B981).copy(alpha = 0.08f),
            offsetX = (-40).dp,
            offsetY = 0.dp,
            alignment = androidx.compose.ui.Alignment.CenterStart
        )
    }
}

/**
 * Geo Shape (окреме коло)
 * CSS: .geo-shape
 */
@Composable
private fun BoxScope.GeoShape(
    size: androidx.compose.ui.unit.Dp,
    color: androidx.compose.ui.graphics.Color,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    alignment: androidx.compose.ui.Alignment
) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(x = offsetX, y = offsetY)
            .background(color, CircleShape)
            .align(alignment)
    )
}

/**
 * Screen Content Wrapper
 * CSS: .v3-content
 *
 * Контейнер для контенту з padding та z-index
 */
@Composable
fun ScreenContent(
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        content()
    }
}
