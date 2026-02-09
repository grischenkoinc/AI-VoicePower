package com.aivoicepower.ui.screens.premium.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

/**
 * Unified pricing block — all 3 plans inside one white card.
 * Selected plan gets a gradient highlight row.
 */
@Composable
fun PricingBlock(
    selectedPlan: PricingPlan,
    onSelectPlan: (PricingPlan) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Section title
        Text(
            text = "Оберіть план",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 14.dp, start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White, RoundedCornerShape(22.dp))
        ) {
            Column {
                PricingPlan.entries.forEachIndexed { index, plan ->
                    PricingPlanRow(
                        plan = plan,
                        isSelected = selectedPlan == plan,
                        onSelect = { onSelectPlan(plan) }
                    )
                    if (index < PricingPlan.entries.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingPlanRow(
    plan: PricingPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF0EDFF) else Color.White,
        animationSpec = tween(200),
        label = "plan_bg"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSelect
                )
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .border(1.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
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

            Spacer(modifier = Modifier.width(14.dp))

            // Plan info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (plan) {
                            PricingPlan.MONTHLY -> "Місяць"
                            PricingPlan.YEARLY -> "Рік"
                            PricingPlan.LIFETIME -> "Назавжди"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    // Popular badge inline
                    if (plan.isPopular) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                                    ),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "\u2B50 Хіт",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }

                    // Savings badge
                    plan.savings?.let { savings ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF10B981).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = savings,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = plan.duration,
                    fontSize = 13.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Price
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color(0xFF764BA2) else Color(0xFF1A1A2E)
                )
                if (plan == PricingPlan.YEARLY) {
                    Text(
                        text = "(\$4.99/мiс)",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}
