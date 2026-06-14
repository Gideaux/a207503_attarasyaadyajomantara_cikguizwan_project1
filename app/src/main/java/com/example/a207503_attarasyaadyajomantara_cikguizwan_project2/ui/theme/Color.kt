package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val KahootPurple       = Color(0xFF7B2FBE)
internal val KahootPurpleLight  = Color(0xFFAB5FE8)
internal val KahootYellow       = Color(0xFFFFB300)
internal val KahootGreen        = Color(0xFF00C853)
internal val DarkBackground     = Color(0xFF121212)
internal val DarkSurface        = Color(0xFF1E1E1E)
internal val DarkCard           = Color(0xFF2C2C2C)

internal val KahootDarkColorScheme = darkColorScheme(
    primary          = KahootYellow,
    onPrimary        = Color.White,
    primaryContainer = KahootPurpleLight,
    secondary        = KahootYellow,
    onSecondary      = Color.Black,
    tertiary         = KahootGreen,
    background       = DarkBackground,
    onBackground     = Color.White,
    surface          = DarkSurface,
    onSurface        = Color.White,
    surfaceVariant   = DarkCard,
    onSurfaceVariant = Color.LightGray
)

internal val KahootLightColorScheme = lightColorScheme(
    primary          = KahootPurple,
    onPrimary        = Color.White,
    primaryContainer = KahootPurpleLight,
    secondary        = KahootYellow,
    onSecondary      = Color.Black,
    tertiary         = KahootGreen
)
