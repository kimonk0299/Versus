package com.versus.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ── Light Color Scheme ──
// Bright, playful colors for a party-game feel.
// Warm cream background with vibrant purple and coral accents.
private val LightColorScheme = lightColorScheme(
    primary = VsPrimary,
    onPrimary = VsOnPrimary,
    primaryContainer = VsPrimaryContainer,
    onPrimaryContainer = VsOnPrimaryContainer,
    secondary = VsSecondary,
    onSecondary = VsOnSecondary,
    secondaryContainer = VsSecondaryContainer,
    onSecondaryContainer = VsOnSecondaryContainer,
    background = VsBackground,
    onBackground = VsOnBackground,
    surface = VsSurface,
    onSurface = VsOnSurface,
    surfaceVariant = VsSurfaceVariant,
    onSurfaceVariant = VsOnSurfaceVariant,
    error = VsError,
    onError = VsOnError
)

/**
 * The main app theme. Wrap your entire UI with this.
 *
 * Uses a bright, playful light theme with Fredoka + Nunito fonts
 * for a fun party-game vibe (think Heads Up, Psych, Pictionary).
 */
@Composable
fun VersusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = VsTypography,
        content = content
    )
}
