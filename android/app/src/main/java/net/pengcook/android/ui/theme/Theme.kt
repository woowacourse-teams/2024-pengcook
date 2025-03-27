package net.pengcook.android.ui.theme

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
    background = Black100,
    primary = Black100,
    onPrimary = White100,
    onSurfaceVariant = Gray30,
    surface = Gray50,
    surfaceTint = Gray70,
    outline = Gray50,
    secondary = Gray70,
)

private val LightColorScheme = lightColorScheme(
    background = White100,
    primary = White100,
    onPrimary = Black100,
    onSurfaceVariant = Gray30,
    surface = White50,
    surfaceTint = White100,
    outline = Gray10,
    secondary = White70,
)

@Composable
fun PengCookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
        content = content,
    )
}
