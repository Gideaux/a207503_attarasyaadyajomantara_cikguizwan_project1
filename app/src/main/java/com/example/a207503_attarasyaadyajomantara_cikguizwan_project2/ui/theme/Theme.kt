package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun A207503_AttarasyaAdyaJomantara_CikguIzwan_Project2Theme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KahootDarkColorScheme else KahootLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = KahootTypography,
        content     = content
    )
}
