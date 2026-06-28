package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = BentoMathPrimary,
    onPrimaryContainer = BentoMathContainer,
    secondary = BentoEssayContainer,
    onSecondary = BentoEssayPrimary,
    tertiary = BentoSummaryContainer,
    onTertiary = BentoSummaryPrimary,
    background = BentoBackgroundDark,
    surface = BentoSurfaceDark,
    onBackground = Color(0xFFF1F5F9), // Slate 100
    onSurface = Color(0xFFF1F5F9)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoMathPrimary,
    onPrimary = Color.White,
    primaryContainer = BentoMathContainer,
    onPrimaryContainer = BentoMathPrimary,
    secondary = BentoEssayPrimary,
    onSecondary = Color.White,
    secondaryContainer = BentoEssayContainer,
    onSecondaryContainer = BentoEssayPrimary,
    tertiary = BentoSummaryPrimary,
    onTertiary = Color.White,
    tertiaryContainer = BentoSummaryContainer,
    onTertiaryContainer = BentoSummaryPrimary,
    background = BentoBackgroundLight,
    surface = BentoSurfaceLight,
    onBackground = Color(0xFF0F172A), // Slate 900
    onSurface = Color(0xFF0F172A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Custom Bento theme defaults to false for dynamic color to preserve customized palettes
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
