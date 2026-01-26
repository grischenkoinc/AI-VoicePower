package com.aivoicepower.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.modifiers.*
import com.aivoicepower.ui.theme.components.*

enum class CourseFilter {
    ALL, ACTIVE, COMPLETED, LOCKED
}

@Composable
fun CoursesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onSearch: () -> Unit,
    viewModel: CoursesListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val courses = state.courses
    var selectedFilter by remember { mutableStateOf(CourseFilter.ALL) }

    val filteredCourses = remember(courses, selectedFilter) {
        when (selectedFilter) {
            CourseFilter.ALL -> courses
            CourseFilter.ACTIVE -> courses.filter { it.isStarted && !it.isCompleted }
            CourseFilter.COMPLETED -> courses.filter { it.isCompleted }
            CourseFilter.LOCKED -> courses.filter { it.course.isPremium && !it.isStarted }
        }
    }

    val stats = remember(courses) {
        Triple(
            courses.count { it.isStarted && !it.isCompleted }, // Active
            courses.count { it.isCompleted }, // Completed
            courses.count { !it.course.isPremium && !it.isStarted } // Available
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x33A78BFA),
                                    Color.Transparent,
                                    Color(0x26FBBF24)
                                ),
                                center = Offset(300f, 400f),
                                radius = 1000f
                            )
                        )
                )
            }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            CoursesHeader(
                onNavigateBack = onNavigateBack,
                onSearch = onSearch,
                totalCourses = courses.size,
                totalLessons = courses.sumOf { it.totalLessons }
            )

            // Filter Tabs
            FilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatsBar(
                        active = stats.first,
                        completed = stats.second,
                        available = stats.third
                    )
                }

                items(filteredCourses) { courseWithProgress ->
                    CourseCardItem(
                        courseWithProgress = courseWithProgress,
                        onClick = {
                            if (!courseWithProgress.course.isPremium || courseWithProgress.isStarted) {
                                onNavigateToCourse(courseWithProgress.course.id)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CoursesHeader(
    onNavigateBack: () -> Unit,
    onSearch: () -> Unit,
    totalCourses: Int,
    totalLessons: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(GlassColors.background, CircleShape)
                    .border(1.dp, GlassColors.borderLight, CircleShape)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(GlassColors.background, CircleShape)
                    .border(1.dp, GlassColors.borderLight, CircleShape)
                    .clickable { onSearch() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔍", fontSize = 18.sp)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Курси",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Text(
                text = "$totalCourses курси • $totalLessons уроків",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FilterTabs(
    selectedFilter: CourseFilter,
    onFilterSelected: (CourseFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterTab(
            text = "Всі курси",
            isActive = selectedFilter == CourseFilter.ALL,
            onClick = { onFilterSelected(CourseFilter.ALL) }
        )
        FilterTab(
            text = "В процесі",
            isActive = selectedFilter == CourseFilter.ACTIVE,
            onClick = { onFilterSelected(CourseFilter.ACTIVE) }
        )
        FilterTab(
            text = "Завершені",
            isActive = selectedFilter == CourseFilter.COMPLETED,
            onClick = { onFilterSelected(CourseFilter.COMPLETED) }
        )
        FilterTab(
            text = "Заблоковані",
            isActive = selectedFilter == CourseFilter.LOCKED,
            onClick = { onFilterSelected(CourseFilter.LOCKED) }
        )
    }
}

@Composable
private fun FilterTab(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) Color.White else GlassColors.background,
                RoundedCornerShape(14.dp)
            )
            .border(
                1.5.dp,
                if (isActive) Color.White else GlassColors.borderLight,
                RoundedCornerShape(14.dp)
            )
            .then(
                if (isActive) {
                    Modifier.multiLayerShadow(
                        elevation = 4.dp,
                        spotColor = Color.White.copy(alpha = 0.3f)
                    )
                } else Modifier
            )
            .scaleOnPress(pressedScale = 0.95f)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.labelMedium,
            color = if (isActive) Color(0xFF667EEA) else TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatsBar(
    active: Int,
    completed: Int,
    available: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassColors.background, RoundedCornerShape(16.dp))
            .border(1.5.dp, GlassColors.borderLight, RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.15f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(value = active.toString(), label = "Активний")
        StatItem(value = completed.toString(), label = "Завершено")
        StatItem(value = available.toString(), label = "Доступно")
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label.uppercase(),
            style = AppTypography.labelSmall,
            color = TextColors.onDarkSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun CourseCardItem(
    courseWithProgress: CourseWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val course = courseWithProgress.course
    val gradientColors = remember(course.id) {
        when (course.id.hashCode() % 3) {
            0 -> listOf(Color(0xFF667EEA), Color(0xFF764BA2))
            1 -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
            else -> listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
        }
    }

    val badge = remember(courseWithProgress) {
        when {
            courseWithProgress.isStarted && !courseWithProgress.isCompleted -> "В процесі"
            courseWithProgress.isCompleted -> "Завершено"
            !course.isPremium -> "Доступно"
            else -> null
        }
    }

    val isLocked = course.isPremium && !courseWithProgress.isStarted

    Box(
        modifier = modifier
            .fillMaxWidth()
            .multiLayerShadow(
                elevation = 8.dp,
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundColors.surface, RoundedCornerShape(24.dp))
                .clickable(enabled = !isLocked) { onClick() }
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(200f, 100f),
                                radius = 300f
                            )
                        )
                )

                Text(text = course.iconEmoji, fontSize = 64.sp)

                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(GlassColors.background, RoundedCornerShape(10.dp))
                            .border(1.5.dp, GlassColors.borderMedium, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = badge.uppercase(),
                            style = AppTypography.labelSmall,
                            color = TextColors.onDarkPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF5F5F7), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = course.difficulty.name.uppercase(),
                            style = AppTypography.labelSmall,
                            color = Color(0xFF667EEA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⏱", fontSize = 13.sp)
                        Text(
                            text = "~${course.estimatedDays} днів",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = course.title,
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = course.description,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ПРОГРЕС",
                            style = AppTypography.labelSmall,
                            color = TextColors.onLightMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${courseWithProgress.progressPercent}% • ${courseWithProgress.completedLessons}/${courseWithProgress.totalLessons}",
                            style = AppTypography.bodySmall,
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE5E5EA), RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(courseWithProgress.progressPercent / 100f)
                                .fillMaxHeight()
                                .background(
                                    Brush.linearGradient(gradientColors),
                                    RoundedCornerShape(10.dp)
                                )
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    spotColor = gradientColors.first().copy(alpha = 0.5f)
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📚", fontSize = 16.sp)
                        Text(
                            text = "${course.totalLessons} уроків",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯", fontSize = 16.sp)
                        Text(
                            text = "${course.lessons.sumOf { it.exercises.size }} вправ",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Locked overlay (ВИПРАВЛЕНО — працює на Android)
        if (isLocked) {
            // Затемнення
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0x99000000), // 60% чорного (0.6 * 255 = 153 = 0x99)
                        RoundedCornerShape(24.dp)
                    )
            )

            // Контент overlay
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFAFFFFFF), CircleShape) // 98% білого
                        .shadow(12.dp, CircleShape, spotColor = Color(0x66000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔒", fontSize = 36.sp)
                }

                Text(
                    text = "Курс заблоковано",
                    style = AppTypography.titleLarge,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint()
                            paint.color = android.graphics.Color.BLACK
                            paint.alpha = 204 // 80% opacity
                            paint.maskFilter = android.graphics.BlurMaskFilter(8f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                        }
                    }
                )

                if (course.isPremium) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0x40FFFFFF), // 25% білого
                                RoundedCornerShape(32.dp)
                            )
                            .border(2.dp, Color(0x66FFFFFF), RoundedCornerShape(32.dp)) // 40% білого
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Потрібен Premium для доступу",
                            style = AppTypography.bodyMedium,
                            color = Color(0xFFA0F791), // Яскраво-зелений
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.drawWithContent {
                                drawContent()
                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint()
                                    paint.color = android.graphics.Color.BLACK
                                    paint.alpha = 230 // 90% opacity
                                    paint.maskFilter = android.graphics.BlurMaskFilter(6f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
