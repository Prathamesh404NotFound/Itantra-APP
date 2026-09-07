package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmEditorialColorScheme = lightColorScheme(
  primary = WarmTerracotta,
  onPrimary = Color.White,
  primaryContainer = WarmTerracottaLight,
  onPrimaryContainer = WarmTerracottaDark,
  secondary = WarmAmber,
  onSecondary = Color.White,
  secondaryContainer = WarmAmberLight,
  onSecondaryContainer = WarmAmber,
  tertiary = WarmGold,
  onTertiary = Color.White,
  background = WarmCanvas,
  onBackground = TextWarmPrimary,
  surface = WarmCard,
  onSurface = TextWarmPrimary,
  surfaceVariant = WarmCardSubtle,
  onSurfaceVariant = TextWarmSecondary,
  outline = BorderWarm,
  outlineVariant = BorderWarm,
  error = WarmCrimson,
  onError = Color.White,
  errorContainer = WarmCrimsonLight,
  onErrorContainer = WarmCrimson
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = WarmEditorialColorScheme,
    typography = Typography,
    content = content
  )
}



