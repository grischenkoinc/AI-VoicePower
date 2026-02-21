package com.aivoicepower.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.audio.LocalSoundManager
import com.aivoicepower.audio.SoundEffect
import com.aivoicepower.ui.screens.onboarding.components.*
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    startPage: Int = 0,
    onNavigateToAuth: () -> Unit = {},
    onNavigateToDiagnostic: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { 4 }
    )
    val scope = rememberCoroutineScope()

    // Page change sound
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect {
            soundManager.play(SoundEffect.ONBOARDING_PAGE)
        }
    }

    // Синхронізація pagerState з state.currentPage
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    // Синхронізація state.currentPage з pagerState
    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onEvent(OnboardingEvent.PageChanged(page))
        }
    }

    // Навігація після завершення onboarding
    LaunchedEffect(state.isNavigating) {
        if (state.isNavigating) {
            onNavigateToDiagnostic()
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = startPage == 0 // Disable swipe back to welcome if we started from page 1
    ) { page ->
        // Parallax depth effect — pages scale down and fade as they move off-screen
        val pageOffset = ((pagerState.currentPage - page) +
            pagerState.currentPageOffsetFraction).absoluteValue

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val fraction = pageOffset.coerceIn(0f, 1f)
                    // Scale: 1.0 → 0.92 as page moves away
                    val scale = 1f - (fraction * 0.08f)
                    scaleX = scale
                    scaleY = scale
                    // Alpha: 1.0 → 0.6 as page moves away
                    alpha = 1f - (fraction * 0.4f)
                }
        ) {
            when (page) {
                0 -> OnboardingPage1(
                    onNextClick = {
                        // Navigate to Auth screen instead of next page
                        onNavigateToAuth()
                    }
                )

                1 -> OnboardingPage2(
                    selectedGoal = state.selectedGoal,
                    onGoalSelected = { goal ->
                        viewModel.onEvent(OnboardingEvent.GoalSelected(goal))
                    },
                    onNextClick = {
                        scope.launch {
                            viewModel.onEvent(OnboardingEvent.NextClicked)
                        }
                    },
                    onBackClick = {
                        scope.launch {
                            viewModel.onEvent(OnboardingEvent.BackClicked)
                        }
                    }
                )

                2 -> OnboardingPage3(
                    selectedMinutes = state.dailyMinutes,
                    onMinutesSelected = { minutes ->
                        viewModel.onEvent(OnboardingEvent.MinutesSelected(minutes))
                    },
                    onNextClick = {
                        scope.launch {
                            viewModel.onEvent(OnboardingEvent.NextClicked)
                        }
                    },
                    onBackClick = {
                        scope.launch {
                            viewModel.onEvent(OnboardingEvent.BackClicked)
                        }
                    }
                )

                3 -> OnboardingPage4(
                    onStartDiagnostic = {
                        viewModel.onEvent(OnboardingEvent.StartDiagnosticClicked)
                    },
                    onBackClick = {
                        scope.launch {
                            viewModel.onEvent(OnboardingEvent.BackClicked)
                        }
                    }
                )
            }
        }
    }
}
