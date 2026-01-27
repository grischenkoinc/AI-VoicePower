package com.aivoicepower.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * AI VoicePower Main Theme
 * Based on Design Bible v1.0
 * 
 * Індиго-based Dark Theme з преміум якістю
 */

// ============================================
// COLOR SCHEME
// ============================================
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryColors.default,
    onPrimary = TextColors.onPrimary,
    primaryContainer = PrimaryColors.dark,
    onPrimaryContainer = TextColors.onPrimary,
    
    // Secondary colors
    secondary = SecondaryColors.default,
    onSecondary = TextColors.onSecondary,
    secondaryContainer = SecondaryColors.dark,
    onSecondaryContainer = TextColors.onSecondary,
    
    // Tertiary (using Accent)
    tertiary = AccentColors.default,
    onTertiary = TextColors.onPrimary,
    tertiaryContainer = AccentColors.dark,
    onTertiaryContainer = TextColors.onPrimary,
    
    // Background
    background = BackgroundColors.primary,
    onBackground = TextColors.primary,
    
    // Surface
    surface = BackgroundColors.surface,
    onSurface = TextColors.primary,
    surfaceVariant = BackgroundColors.surfaceElevated,
    onSurfaceVariant = TextColors.secondary,
    
    // Error
    error = SemanticColors.error,
    onError = TextColors.onPrimary,
    errorContainer = SemanticColors.error,
    onErrorContainer = TextColors.onPrimary,
    
    // Outline
    outline = BorderColors.default,
    outlineVariant = BorderColors.subtle,
    
    // Surface tint
    surfaceTint = PrimaryColors.default,
    
    // Inverse
    inverseSurface = TextColors.primary,
    inverseOnSurface = BackgroundColors.primary,
    inversePrimary = PrimaryColors.light,
    
    // Scrim
    scrim = ShadowColors.black30
)

/**
 * ПРИМІТКА: Поки що підтримується тільки Dark Theme.
 * 
 * Light Theme буде додано в майбутньому, якщо потрібно.
 * Всі кольори вже продумані для Dark режиму.
 */

// ============================================
// MAIN THEME COMPOSABLE
// ============================================

/**
 * Головна тема AI VoicePower
 * 
 * Використовує:
 * - DarkColorScheme (єдина підтримувана тема поки що)
 * - AppTypography (Inter font family)
 * - AppShapes (corner radius system)
 * 
 * @param darkTheme Чи використовувати темну тему (поки що завжди true)
 * @param dynamicColor Чи використовувати dynamic color (Material You) - поки false
 * @param content Контент застосунку
 */
@Composable
fun AIVoicePowerTheme(
    darkTheme: Boolean = true, // Завжди dark, поки що
    dynamicColor: Boolean = false, // Dynamic color вимкнено
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    // Set status bar color to transparent (no dark overlay)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

/**
 * ПРИКЛАД ВИКОРИСТАННЯ:
 * 
 * ```kotlin
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContent {
 *             AIVoicePowerTheme {
 *                 Surface(
 *                     modifier = Modifier.fillMaxSize(),
 *                     color = MaterialTheme.colorScheme.background
 *                 ) {
 *                     MainScreen()
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */

// ============================================
// THEME EXTENSIONS
// ============================================

/**
 * Semantic color extensions для зручності
 * 
 * Використовуй так:
 * ```kotlin
 * Text(
 *     text = "Success!",
 *     color = MaterialTheme.colorScheme.success
 * )
 * ```
 */
val androidx.compose.material3.ColorScheme.success: androidx.compose.ui.graphics.Color
    get() = SemanticColors.success

val androidx.compose.material3.ColorScheme.successLight: androidx.compose.ui.graphics.Color
    get() = SemanticColors.successLight

val androidx.compose.material3.ColorScheme.warning: androidx.compose.ui.graphics.Color
    get() = SemanticColors.warning

val androidx.compose.material3.ColorScheme.warningLight: androidx.compose.ui.graphics.Color
    get() = SemanticColors.warningLight

val androidx.compose.material3.ColorScheme.info: androidx.compose.ui.graphics.Color
    get() = SemanticColors.info

val androidx.compose.material3.ColorScheme.infoLight: androidx.compose.ui.graphics.Color
    get() = SemanticColors.infoLight

// Text color extensions
val androidx.compose.material3.ColorScheme.textSecondary: androidx.compose.ui.graphics.Color
    get() = TextColors.secondary

val androidx.compose.material3.ColorScheme.textMuted: androidx.compose.ui.graphics.Color
    get() = TextColors.muted

// Icon color extensions
val androidx.compose.material3.ColorScheme.iconPrimary: androidx.compose.ui.graphics.Color
    get() = IconColors.primary

val androidx.compose.material3.ColorScheme.iconSecondary: androidx.compose.ui.graphics.Color
    get() = IconColors.secondary

val androidx.compose.material3.ColorScheme.iconMuted: androidx.compose.ui.graphics.Color
    get() = IconColors.muted

val androidx.compose.material3.ColorScheme.iconAccent: androidx.compose.ui.graphics.Color
    get() = IconColors.accent

/**
 * ПРИКЛАД ВИКОРИСТАННЯ EXTENSIONS:
 * 
 * ```kotlin
 * // Semantic colors
 * Icon(
 *     imageVector = Icons.Default.CheckCircle,
 *     tint = MaterialTheme.colorScheme.success
 * )
 * 
 * // Text colors
 * Text(
 *     text = "Secondary text",
 *     color = MaterialTheme.colorScheme.textSecondary,
 *     style = MaterialTheme.typography.bodyMedium
 * )
 * 
 * // Icon colors
 * Icon(
 *     imageVector = Icons.Default.Star,
 *     tint = MaterialTheme.colorScheme.iconAccent
 * )
 * ```
 */
