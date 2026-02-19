package com.aivoicepower.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.aivoicepower.ui.theme.AnimationEasing

/**
 * Centralized navigation transition definitions.
 *
 * Transition types:
 * - Standard: horizontal slide + fade (forward/back navigation)
 * - Modal: vertical slide + fade (Premium, AI Coach)
 * - Crossfade: opacity only (tab switching)
 * - FadeThrough: Material fade-through (Splash → Onboarding)
 */
object NavTransitions {

    // ===== DURATION CONSTANTS =====

    private const val STANDARD_DURATION = 300
    private const val POP_DURATION = 250
    private const val MODAL_ENTER_DURATION = 400
    private const val MODAL_EXIT_DURATION = 300
    private const val TAB_DURATION = 200
    private const val FADE_THROUGH_DURATION = 400

    // ===== STANDARD (horizontal slide + fade) =====

    val enterForward: EnterTransition = slideInHorizontally(
        animationSpec = tween(STANDARD_DURATION, easing = AnimationEasing.standard),
        initialOffsetX = { fullWidth -> fullWidth / 4 }
    ) + fadeIn(
        animationSpec = tween(STANDARD_DURATION, easing = AnimationEasing.standard)
    )

    val exitForward: ExitTransition = slideOutHorizontally(
        animationSpec = tween(STANDARD_DURATION, easing = AnimationEasing.standard),
        targetOffsetX = { fullWidth -> -fullWidth / 4 }
    ) + fadeOut(
        animationSpec = tween(STANDARD_DURATION, easing = AnimationEasing.standard)
    )

    val enterBack: EnterTransition = slideInHorizontally(
        animationSpec = tween(POP_DURATION, easing = AnimationEasing.standard),
        initialOffsetX = { fullWidth -> -fullWidth / 4 }
    ) + fadeIn(
        animationSpec = tween(POP_DURATION, easing = AnimationEasing.standard)
    )

    val exitBack: ExitTransition = slideOutHorizontally(
        animationSpec = tween(POP_DURATION, easing = AnimationEasing.standard),
        targetOffsetX = { fullWidth -> fullWidth / 4 }
    ) + fadeOut(
        animationSpec = tween(POP_DURATION, easing = AnimationEasing.standard)
    )

    // ===== MODAL (vertical slide + fade) =====

    val modalEnter: EnterTransition = slideInVertically(
        animationSpec = tween(MODAL_ENTER_DURATION, easing = AnimationEasing.easeOut),
        initialOffsetY = { fullHeight -> fullHeight / 6 }
    ) + fadeIn(
        animationSpec = tween(MODAL_ENTER_DURATION, easing = AnimationEasing.easeOut)
    )

    val modalExit: ExitTransition = slideOutVertically(
        animationSpec = tween(MODAL_EXIT_DURATION, easing = AnimationEasing.easeIn),
        targetOffsetY = { fullHeight -> fullHeight / 6 }
    ) + fadeOut(
        animationSpec = tween(MODAL_EXIT_DURATION, easing = AnimationEasing.easeIn)
    )

    // ===== CROSSFADE (tab switching) =====

    val crossfadeIn: EnterTransition = fadeIn(
        animationSpec = tween(TAB_DURATION, easing = AnimationEasing.standard)
    )

    val crossfadeOut: ExitTransition = fadeOut(
        animationSpec = tween(TAB_DURATION, easing = AnimationEasing.standard)
    )

    // ===== FADE THROUGH (splash, onboarding flow) =====

    val fadeThroughEnter: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = FADE_THROUGH_DURATION,
            delayMillis = 100,
            easing = AnimationEasing.easeOut
        )
    ) + scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(
            durationMillis = FADE_THROUGH_DURATION,
            delayMillis = 100,
            easing = AnimationEasing.easeOut
        )
    )

    val fadeThroughExit: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = 150,
            easing = AnimationEasing.easeIn
        )
    ) + scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(
            durationMillis = 150,
            easing = AnimationEasing.easeIn
        )
    )

    // ===== ROUTE GROUPS =====

    val tabRoutes = setOf(
        Screen.Home.route,
        Screen.Courses.route,
        Screen.Warmup.route,
        Screen.Improvisation.route,
        Screen.Progress.route
    )

    val modalRoutes = setOf(
        Screen.Premium.route,
        Screen.AiCoach.route
    )

    val onboardingFlowRoutes = setOf(
        Screen.Splash.route,
        Screen.Onboarding.route,
        "onboarding?startPage={startPage}",
        Screen.Auth.route,
        Screen.UserName.route,
        Screen.Main.route
    )
}
