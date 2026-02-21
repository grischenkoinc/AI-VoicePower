package com.aivoicepower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.modifiers.shimmerEffect

/**
 * Reusable shimmer skeleton building blocks.
 * Used as loading placeholders before real content loads.
 */

/** Rounded rectangle shimmer placeholder */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    baseColor: Color = Color(0xFFE8E8ED)
) {
    Box(
        modifier = modifier
            .background(baseColor, shape)
            .shimmerEffect()
    )
}

/** Text line shimmer placeholder */
@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
    baseColor: Color = Color(0xFFE8E8ED)
) {
    Box(
        modifier = modifier
            .height(height)
            .background(baseColor, RoundedCornerShape(height / 2))
            .shimmerEffect()
    )
}

/** Circular shimmer placeholder (for icons/avatars) */
@Composable
fun ShimmerCircle(
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFE8E8ED)
) {
    Box(
        modifier = modifier
            .size(size)
            .background(baseColor, CircleShape)
            .shimmerEffect()
    )
}

// ============================================================
// HomeScreen Skeleton
// ============================================================

@Composable
fun HomeSkeletonContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header skeleton (greeting + name + settings button) - on dark bg
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerCircle(size = 40.dp, baseColor = Color.White.copy(alpha = 0.2f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerLine(
                        modifier = Modifier.width(100.dp),
                        height = 13.dp,
                        baseColor = Color.White.copy(alpha = 0.15f)
                    )
                    ShimmerLine(
                        modifier = Modifier.width(150.dp),
                        height = 26.dp,
                        baseColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
            ShimmerCircle(size = 40.dp, baseColor = Color.White.copy(alpha = 0.2f))
        }

        // Streak card skeleton - gradient card like real one
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA).copy(alpha = 0.45f),
                            Color(0xFF764BA2).copy(alpha = 0.35f)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    baseColor = Color.White.copy(alpha = 0.2f)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerLine(
                        modifier = Modifier.width(70.dp),
                        height = 12.dp,
                        baseColor = Color.White.copy(alpha = 0.15f)
                    )
                    ShimmerLine(
                        modifier = Modifier.width(130.dp),
                        height = 28.dp,
                        baseColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                repeat(7) {
                    ShimmerCircle(size = 40.dp, baseColor = Color.White.copy(alpha = 0.15f))
                }
            }
        }

        // Motivation card skeleton - solid yellow card like real one
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.12f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                baseColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerLine(
                    modifier = Modifier.width(80.dp),
                    height = 12.dp,
                    baseColor = Color(0xFFD97706).copy(alpha = 0.2f)
                )
                ShimmerLine(
                    modifier = Modifier.fillMaxWidth(),
                    height = 14.dp,
                    baseColor = Color(0xFF92400E).copy(alpha = 0.15f)
                )
                ShimmerLine(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    height = 14.dp,
                    baseColor = Color(0xFF92400E).copy(alpha = 0.12f)
                )
            }
        }

        // Daily goal card skeleton - solid white card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerLine(
                modifier = Modifier.width(100.dp),
                height = 18.dp,
                baseColor = Color(0xFFE8E8ED)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerCircle(size = 80.dp, baseColor = Color(0xFFE8E8ED))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerLine(modifier = Modifier.fillMaxWidth(), height = 14.dp)
                    ShimmerLine(modifier = Modifier.fillMaxWidth(), height = 14.dp)
                }
            }
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    baseColor = Color(0xFFF5F5F7)
                )
            }
        }

        // Skills section title - on dark bg
        ShimmerLine(
            modifier = Modifier.width(100.dp),
            height = 20.dp,
            baseColor = Color.White.copy(alpha = 0.2f)
        )

        // Quick actions skeleton - solid white cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(145.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.15f))
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ShimmerLine(modifier = Modifier.fillMaxWidth(0.8f), height = 16.dp)
                }
            }
        }
    }
}

// ============================================================
// ProgressScreen Skeleton
// ============================================================

@Composable
fun ProgressSkeletonContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header skeleton - on dark bg
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerLine(
                modifier = Modifier.width(130.dp),
                height = 13.dp,
                baseColor = Color.White.copy(alpha = 0.15f)
            )
            ShimmerLine(
                modifier = Modifier.width(100.dp),
                height = 26.dp,
                baseColor = Color.White.copy(alpha = 0.2f)
            )
        }

        // Overview card skeleton - gradient card like real
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF6366F1).copy(alpha = 0.3f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShimmerLine(
                modifier = Modifier.width(160.dp),
                height = 22.dp,
                baseColor = Color.White.copy(alpha = 0.2f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerCircle(size = 120.dp, baseColor = Color.White.copy(alpha = 0.15f))
                Column(
                    modifier = Modifier.weight(1f).padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerLine(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        height = 20.dp,
                        baseColor = Color.White.copy(alpha = 0.2f)
                    )
                    ShimmerLine(
                        modifier = Modifier.fillMaxWidth(),
                        height = 14.dp,
                        baseColor = Color.White.copy(alpha = 0.15f)
                    )
                }
            }
        }

        // Skills card skeleton - solid white
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerLine(modifier = Modifier.width(100.dp), height = 14.dp)
                    ShimmerLine(modifier = Modifier.width(40.dp), height = 14.dp)
                }
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(14.dp),
                    shape = RoundedCornerShape(7.dp),
                    baseColor = Color(0xFFE5E7EB)
                )
            }
        }

        // Actions - solid white cards
        ShimmerLine(
            modifier = Modifier.width(50.dp),
            height = 20.dp,
            baseColor = Color.White.copy(alpha = 0.2f)
        )
        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.12f))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                ShimmerLine(modifier = Modifier.weight(1f), height = 16.dp)
            }
        }
    }
}
