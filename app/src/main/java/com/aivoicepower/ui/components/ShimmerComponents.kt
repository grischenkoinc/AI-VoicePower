package com.aivoicepower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    baseColor: Color = Color.White.copy(alpha = 0.08f)
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
    baseColor: Color = Color.White.copy(alpha = 0.08f)
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
    baseColor: Color = Color.White.copy(alpha = 0.08f)
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
        // Header skeleton (greeting + name + settings button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerCircle(size = 40.dp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerLine(modifier = Modifier.width(100.dp), height = 13.dp)
                    ShimmerLine(modifier = Modifier.width(150.dp), height = 26.dp)
                }
            }
            ShimmerCircle(size = 40.dp)
        }

        // Streak card skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(130.dp),
            shape = RoundedCornerShape(20.dp)
        )

        // Motivation card skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(95.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color(0xFFFEF3C7).copy(alpha = 0.15f)
        )

        // Daily goal card skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(260.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )

        // Skills section skeleton
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ShimmerLine(modifier = Modifier.width(100.dp), height = 20.dp)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier.width(160.dp).height(190.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Continue course skeleton
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerLine(modifier = Modifier.width(200.dp), height = 20.dp)
            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                shape = RoundedCornerShape(24.dp),
                baseColor = Color.White.copy(alpha = 0.12f)
            )
        }

        // Quick actions skeleton (2x2 grid)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ShimmerLine(modifier = Modifier.width(140.dp), height = 20.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(2) {
                    ShimmerBox(
                        modifier = Modifier.weight(1f).height(145.dp),
                        shape = RoundedCornerShape(20.dp),
                        baseColor = Color.White.copy(alpha = 0.12f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(2) {
                    ShimmerBox(
                        modifier = Modifier.weight(1f).height(145.dp),
                        shape = RoundedCornerShape(20.dp),
                        baseColor = Color.White.copy(alpha = 0.12f)
                    )
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
        // Header skeleton
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerLine(modifier = Modifier.width(130.dp), height = 13.dp)
            ShimmerLine(modifier = Modifier.width(100.dp), height = 26.dp)
        }

        // Overview card skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )

        // Journey section skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(20.dp)
        )

        // Skills title
        ShimmerLine(modifier = Modifier.width(100.dp), height = 20.dp)

        // Skills chart card skeleton (radar + bars)
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )

        // Achievements title
        ShimmerLine(modifier = Modifier.width(180.dp), height = 20.dp)

        // Achievements card skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )

        // Actions
        ShimmerLine(modifier = Modifier.width(50.dp), height = 20.dp)

        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            baseColor = Color.White.copy(alpha = 0.12f)
        )
    }
}
