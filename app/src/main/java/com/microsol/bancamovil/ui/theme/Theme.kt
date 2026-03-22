package com.microsol.bancamovil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BankingPrimaryDark,
    secondary = BankingSecondaryDark,
    tertiary = BankingTertiary,
    background = BankingBackgroundDark,
    surface = BankingSurfaceDark,
    onPrimary = BankingBackground,
    onSecondary = BankingBackground,
    onTertiary = BankingBackground,
    onBackground = BankingOnSurfaceDark,
    onSurface = BankingOnSurfaceDark,
    error = BankingError
)

private val LightColorScheme = lightColorScheme(
    primary = BankingPrimary,
    secondary = BankingSecondary,
    tertiary = BankingTertiary,
    background = BankingBackground,
    surface = BankingSurface,
    onPrimary = BankingBackground,
    onSecondary = BankingBackground,
    onTertiary = BankingBackground,
    onBackground = BankingOnSurface,
    onSurface = BankingOnSurface,
    error = BankingError
)

@Composable
fun BancaMovilInterbankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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