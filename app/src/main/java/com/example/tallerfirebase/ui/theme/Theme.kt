package com.example.tallerfirebase.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaColoresOscuro = darkColorScheme(
    primary = AcentoPrincipal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A3A5C),
    onPrimaryContainer = AcentoSecundario,
    secondary = AcentoCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A3A3A),
    onSecondaryContainer = AcentoCyan,
    tertiary = AcentoAmbar,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF3A3A1A),
    onTertiaryContainer = AcentoAmbar,
    error = ColorError,
    onError = Color.White,
    errorContainer = Color(0xFF3A1A1A),
    onErrorContainer = ColorError,
    background = FondoOscuroPrincipal,
    onBackground = TextoPrimario,
    surface = FondoOscuroSecundario,
    onSurface = TextoPrimario,
    surfaceVariant = FondoTarjeta,
    onSurfaceVariant = TextoSecundario,
    outline = BordeSutil,
    outlineVariant = TextoDeshabilitado,
)

private val EsquemaColoresClaro = lightColorScheme(
    primary = AcentoPrincipalClaro,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E9FF),
    onPrimaryContainer = Color(0xFF004085),
    secondary = Color(0xFF17A2B8),
    onSecondary = Color.White,
    background = FondoClaroPrincipal,
    onBackground = TextoClaroPrimario,
    surface = FondoClaroSecundario,
    onSurface = TextoClaroPrimario,
    error = Color(0xFFDC3545),
    onError = Color.White,
    outline = Color(0xFFCED4DA)
)

@Composable
fun TallerFirebaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquemaColores = if (darkTheme) EsquemaColoresOscuro else EsquemaColoresClaro

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColores.background.toArgb()
            ventana.navigationBarColor = esquemaColores.background.toArgb()
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Tipografia,
        content = content
    )
}
