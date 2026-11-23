package com.mindguard.core.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2D5A94),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF193A63),
    onPrimaryContainer = Color(0xFFDCE8F8),
    secondary = Color(0xFFB8C7E0),
    onSecondary = Color(0xFF223044),
    secondaryContainer = Color(0xFF35465F),
    onSecondaryContainer = Color(0xFFD9E3F8),
    background = Color(0xFF101318),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF1A1D23),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF252A33),
    onSurfaceVariant = Color(0xFFB8C0CC),
    outline = Color(0xFF525B6A)
)

@Composable
fun MindShieldAiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val context = view.context

    if (!view.isInEditMode) {
        val activity = remember(context) {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is Activity) return@remember ctx
                ctx = ctx.baseContext
            }
            null
        }

        SideEffect {
            activity?.window?.let { window ->
                val scheme = if (darkTheme) DarkColorScheme else LightColorScheme
                window.statusBarColor = scheme.background.toArgb()
                window.navigationBarColor = scheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes(
            small = RoundedCornerShape(22.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(26.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        content = content
    )
}
