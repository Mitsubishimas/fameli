package com.fameli.budget.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1B6B4A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA7F5C4),
    secondary = Color(0xFF4A6572),
    background = Color(0xFFF8FBF8),
    surface = Color(0xFFF8FBF8),
    error = Color(0xFFBA1A1A),
)

@Composable
fun FameliTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
