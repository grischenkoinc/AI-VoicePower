package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Content Components v2.0
 *
 * Джерело: Design_Example_react.md
 */

/**
 * Highlight Box (💡 Ключовий інсайт)
 * CSS: .highlight-box
 *
 * Orange-purple gradient bg, left border
 */
@Composable
fun HighlightBox(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        // Orange left border (3dp)
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(IntrinsicSize.Min)
                .background(Color(0xFFF59E0B))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Gradients.highlightBox, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = content,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

/**
 * Numbered Tips Container
 * CSS: .numbered-tips
 */
@Composable
fun NumberedTips(
    header: String = "Важливо знати",
    tips: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = header,
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        // Tips
        tips.forEachIndexed { index, tip ->
            TipRow(
                number = index + 1,
                text = tip
            )
        }
    }
}

/**
 * Tip Row (numbered item)
 * CSS: .tip-row
 *
 * З hover effect (purple glow)
 */
@Composable
fun TipRow(
    number: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { /* Tip clicked */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = if (isPressed) 8.dp else 4.dp,
                    shape = CircleShape,
                    spotColor = Color(0x40A78BFA)
                )
                .background(
                    brush = Gradients.tagPrimary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = AppTypography.labelMedium,
                color = TextColors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Content Text Section
 * CSS: .content-text
 *
 * H3 title + paragraphs
 */
@Composable
fun ContentText(
    title: String? = null,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Optional title
        if (title != null) {
            Text(
                text = title,
                style = AppTypography.headlineMedium,
                color = TextColors.onLightPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 28.sp
            )
        }

        // Body text
        Text(
            text = text,
            style = AppTypography.bodyLarge,
            color = TextColors.onLightSecondary,
            fontSize = 15.sp,
            lineHeight = 25.sp
        )
    }
}
