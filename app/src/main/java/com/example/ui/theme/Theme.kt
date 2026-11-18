package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StarlightCottageColorScheme = darkColorScheme(
    primary = ButtercreamGold,
    onPrimary = Color(0xFF231604),
    primaryContainer = DarkAmber,
    onPrimaryContainer = SoftCream,
    secondary = PastelLavender,
    onSecondary = Color(0xFF1F163B),
    secondaryContainer = ArcanePurpleDark,
    onSecondaryContainer = LavenderBlush,
    tertiary = PastelMint,
    onTertiary = Color(0xFF052E16),
    background = StarlightIndigo,
    onBackground = SoftCream,
    surface = StarlightTwilight,
    onSurface = SoftCream,
    surfaceVariant = StarlightPlum,
    onSurfaceVariant = OnSurfaceMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = StarlightCottageColorScheme,
        typography = Typography,
        content = content
    )
}
