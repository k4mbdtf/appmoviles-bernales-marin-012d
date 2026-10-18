package com.paolicias.sweet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RosaClaroContainer,
    onPrimary = CafeEspresso,
    primaryContainer = RosaFrambuesa,
    secondary = CafeSuaveContainer,
    onSecondary = CafeEspresso,
    tertiary = CarameloDorado,
    background = CafeEspresso,
    surface = CafeCacao,
    onBackground = CremaVainilla,
    onSurface = CremaVainilla
)

private val LightColorScheme = lightColorScheme(
    primary = RosaPastel,
    onPrimary = Color.White,
    primaryContainer = RosaClaroContainer,
    onPrimaryContainer = CafeEspresso,
    secondary = CafeCacao,
    onSecondary = Color.White,
    secondaryContainer = CafeSuaveContainer,
    onSecondaryContainer = CafeEspresso,
    tertiary = CarameloDorado,
    background = CremaVainilla,
    onBackground = CafeEspresso,
    surface = CremaChantilly,
    onSurface = CafeEspresso
)

@Composable
fun PaoliciasSweetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
