package uk.co.cctvline.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CctvBlack = Color(0xFF0B0B0D)
val CctvRed = Color(0xFFB4161B)
val CctvDarkGrey = Color(0xFF1F2329)
val CctvLightBg = Color(0xFFF5F6F8)

private val LightColors = lightColorScheme(
    primary = CctvRed,
    onPrimary = Color.White,
    secondary = CctvBlack,
    onSecondary = Color.White,
    background = CctvLightBg,
    onBackground = CctvBlack,
    surface = Color.White,
    onSurface = CctvBlack
)

@Composable
fun CCTVLineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
