package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DeepPurpleLight,
    onPrimary = Color.White,
    primaryContainer = DeepPurpleContainer,
    onPrimaryContainer = Color(0xFFE9D8FD),
    secondary = GoldReward,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A4400),
    onSecondaryContainer = GoldRewardLight,
    background = BackgroundDark,
    onBackground = Color(0xFFEDE9FE),
    surface = SurfaceDark,
    onSurface = Color(0xFFEDE9FE),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFD6CDEB),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DeepPurpleDark,
    onPrimary = Color.White,
    primaryContainer = DeepPurpleContainer,
    onPrimaryContainer = Color.White,
    secondary = GoldReward,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF3D0),
    onSecondaryContainer = Color(0xFF5A4400),
    background = BackgroundLight,
    onBackground = Color(0xFF1E1430),
    surface = SurfaceLight,
    onSurface = Color(0xFF1E1430),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4C3F65),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional deep purple brand aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
