package com.aivoicepower.ui.theme.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Shadow Modifiers v2.0
 *
 * Multi-layer shadow system для 3D ефектів
 */

/**
 * Multi-Layer Shadow для 3D depth
 *
 * Використання:
 * Modifier.multiLayerShadow(
 *     elevation = 12.dp,
 *     spotColor = Color.Black,
 *     ambientColor = Color.Black.copy(alpha = 0.1f)
 * )
 */
fun Modifier.multiLayerShadow(
    elevation: Dp,
    spotColor: Color = Color.Black.copy(alpha = 0.25f),
    ambientColor: Color = Color.Black.copy(alpha = 0.05f),
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
): Modifier = this.then(
    Modifier.drawBehind {
        val elevationPx = elevation.toPx()

        drawIntoCanvas { canvas ->
            // Layer 1: Ambient shadow (soft, large)
            val ambientPaint = Paint().apply {
                color = ambientColor
                isAntiAlias = true
            }
            ambientPaint.asFrameworkPaint().apply {
                this.color = ambientColor.toArgb()
                setShadowLayer(
                    elevationPx * 1.5f,
                    0f,
                    elevationPx * 0.5f,
                    ambientColor.copy(alpha = 0.1f).toArgb()
                )
            }
            canvas.drawRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                paint = ambientPaint
            )

            // Layer 2: Spot shadow (sharp, directional)
            val spotPaint = Paint().apply {
                color = spotColor
                isAntiAlias = true
            }
            spotPaint.asFrameworkPaint().apply {
                this.color = spotColor.toArgb()
                setShadowLayer(
                    elevationPx * 0.8f,
                    0f,
                    elevationPx * 0.3f,
                    spotColor.copy(alpha = 0.25f).toArgb()
                )
            }
            canvas.drawRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                paint = spotPaint
            )

            // Layer 3: Inner highlight (top edge)
            val highlightPaint = Paint().apply {
                color = Color.White.copy(alpha = 0.1f)
                isAntiAlias = true
            }
            canvas.drawRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = 2f,
                paint = highlightPaint
            )
        }
    }
)

/**
 * Glow Shadow для progress bars та active elements
 *
 * Використання:
 * Modifier.glowShadow(
 *     glowRadius = 16.dp,
 *     glowColor = Color(0xFF22C55E)
 * )
 */
fun Modifier.glowShadow(
    glowRadius: Dp,
    glowColor: Color,
    intensity: Float = 0.6f
): Modifier = this.then(
    Modifier.drawBehind {
        val radiusPx = glowRadius.toPx()

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = glowColor.copy(alpha = intensity)
                isAntiAlias = true
            }
            paint.asFrameworkPaint().apply {
                this.color = glowColor.copy(alpha = 0f).toArgb()
                setShadowLayer(
                    radiusPx,
                    0f,
                    0f,
                    glowColor.copy(alpha = intensity).toArgb()
                )
            }
            canvas.drawRect(
                left = -radiusPx,
                top = -radiusPx,
                right = size.width + radiusPx,
                bottom = size.height + radiusPx,
                paint = paint
            )
        }
    }
)

/**
 * Inset Shadow simulation для depressed elements
 *
 * Використання:
 * Modifier.insetShadow(
 *     depth = 4.dp,
 *     color = Color.Black.copy(alpha = 0.3f)
 * )
 */
fun Modifier.insetShadow(
    depth: Dp,
    color: Color = Color.Black.copy(alpha = 0.2f),
    fromTop: Boolean = true,
    fromLeft: Boolean = true
): Modifier = this.then(
    Modifier.drawBehind {
        val depthPx = depth.toPx()

        // Top shadow
        if (fromTop) {
            drawRect(
                color = color,
                topLeft = Offset.Zero,
                size = androidx.compose.ui.geometry.Size(size.width, depthPx)
            )
        }

        // Left shadow
        if (fromLeft) {
            drawRect(
                color = color,
                topLeft = Offset.Zero,
                size = androidx.compose.ui.geometry.Size(depthPx, size.height)
            )
        }
    }
)

/**
 * Elevated Shadow для floating elements (FABs, modals)
 *
 * Використання:
 * Modifier.elevatedShadow(
 *     elevation = 24.dp,
 *     color = Color(0xFF667EEA)
 * )
 */
fun Modifier.elevatedShadow(
    elevation: Dp,
    color: Color = Color.Black.copy(alpha = 0.3f)
): Modifier = this.then(
    Modifier.drawBehind {
        val elevationPx = elevation.toPx()

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.color = color
                isAntiAlias = true
            }
            paint.asFrameworkPaint().apply {
                this.color = color.toArgb()
                setShadowLayer(
                    elevationPx * 1.2f,
                    0f,
                    elevationPx * 0.6f,
                    color.copy(alpha = 0.4f).toArgb()
                )
            }
            canvas.drawRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                paint = paint
            )
        }
    }
)

/**
 * 3D Button Shadow (pressed state)
 *
 * Використання:
 * Modifier.buttonShadow3D(
 *     isPressed = isPressed,
 *     elevation = 8.dp
 * )
 */
fun Modifier.buttonShadow3D(
    isPressed: Boolean,
    elevation: Dp,
    color: Color = Color.Black.copy(alpha = 0.25f)
): Modifier = this.then(
    if (isPressed) {
        Modifier.insetShadow(depth = elevation * 0.3f, color = color)
    } else {
        Modifier.multiLayerShadow(elevation = elevation, spotColor = color)
    }
)
