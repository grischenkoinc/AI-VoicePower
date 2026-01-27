package com.aivoicepower.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.courses.components.CourseCard
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun CoursesListScreen(
    viewModel: CoursesListViewModel = hiltViewModel(),
    onNavigateToCourse: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = Color.White,
                        strokeWidth = 5.dp
                    )
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .background(Color.White, RoundedCornerShape(32.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(text = "⚠️", fontSize = 64.sp)

                        Text(
                            text = "Помилка",
                            style = AppTypography.titleLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = state.error!!,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 16.sp
                        )

                        PrimaryButton(
                            text = "Повторити",
                            onClick = { viewModel.onEvent(CoursesListEvent.Refresh) }
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 60.dp,
                        bottom = 130.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(17.dp)
                ) {
                    // Back button
                    item {
                        Row(
                            modifier = Modifier
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color.Black.copy(alpha = 0.2f)
                                )
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .clickable { onNavigateBack() }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "←",
                                fontSize = 24.sp,
                                color = Color(0xFF667EEA),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Назад",
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Header
                    item {
                        Text(
                            text = "Усі курси",
                            style = AppTypography.displayLarge,
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    }

                    // Course cards
                    items(state.courses) { courseWithProgress ->
                        CourseCard(
                            courseWithProgress = courseWithProgress,
                            onClick = {
                                viewModel.onEvent(CoursesListEvent.CourseClicked(courseWithProgress.course.id))
                                onNavigateToCourse(courseWithProgress.course.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
