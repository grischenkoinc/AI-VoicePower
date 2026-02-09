package com.aivoicepower.ui.screens.premium.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.premium.PricingPlan
import com.aivoicepower.ui.theme.modifiers.glassBackground

/**
 * Premium pricing card with glass morphism style.
 *
 * Selected card gets a gradient border glow.
 * Popular plan shows a "Найпопулярніший" badge.
 */
@Composable
fun PricingCard(
    plan: PricingPlan,
    isSelected: Boolean,
    glowAlpha: Float = 0.5f,
    onSelect: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)

    // Gradient border for selected state
    val gradientBorder = Brush.linearGradient(
        colors = listOf(
            Color(0xFF667EEA).copy(alpha = if (isSelected) glowAlpha else 0f),
            Color(0xFFA78BFA).copy(alpha = if (isSelected) glowAlpha else 0f),
            Color(0xFFFBBF24).copy(alpha = if (isSelected) glowAlpha * 0.7f else 0f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (plan.isPopular) 11.dp else 0.dp)
    ) {
        // Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSelected) {
                        Modifier.shadow(
                            elevation = 12.dp,
                            shape = cardShape,
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                        )
                    } else {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = cardShape,
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        )
                    }
                )
                .then(
                    if (isSelected) {
                        Modifier
                            .clip(cardShape)
                            .background(
                                Color.White.copy(alpha = 0.18f),
                                cardShape
                            )
                            .border(
                                width = 2.dp,
                                brush = gradientBorder,
                                shape = cardShape
                            )
                    } else {
                        Modifier.glassBackground(
                            shape = cardShape,
                            backgroundColor = Color.White.copy(alpha = 0.08f),
                            borderColor = Color.White.copy(alpha = 0.15f),
                            borderWidth = 1.dp
                        )
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSelect
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: plan info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Selection indicator
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF667EEA),
                                                        Color(0xFF764BA2)
                                                    )
                                                ),
                                                RoundedCornerShape(11.dp)
                                            )
                                    } else {
                                        Modifier
                                            .background(
                                                Color.White.copy(alpha = 0.1f),
                                                RoundedCornerShape(11.dp)
                                            )
                                            .border(
                                                1.5.dp,
                                                Color.White.copy(alpha = 0.3f),
                                                RoundedCornerShape(11.dp)
                                            )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                )
                            }
                        }

                        Text(
                            text = when (plan) {
                                PricingPlan.MONTHLY -> "Місяць"
                                PricingPlan.YEARLY -> "Рік"
                                PricingPlan.LIFETIME -> "Назавжди"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Duration text
                    Text(
                        text = plan.duration,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.55f)
                    )

                    // Savings badge
                    plan.savings?.let { savings ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF10B981).copy(alpha = 0.2f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = savings,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                // Right side: price
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = plan.price,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color(0xFFFBBF24) else Color.White
                    )
                    if (plan == PricingPlan.YEARLY) {
                        Text(
                            text = "(\$4.99/мiс)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // "Найпопулярніший" badge (positioned at the top center)
        if (plan.isPopular) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-11).dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFBBF24),
                                Color(0xFFF59E0B)
                            )
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "\u2B50 Найпопулярніший",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
