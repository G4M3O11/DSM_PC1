package dsm.davidalamo.pc1.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaOscuro = darkColorScheme(
    primary = TurquesaClaro,
    onPrimary = AzulProfundo,
    primaryContainer = Color(0xFF124B48),
    onPrimaryContainer = Color(0xFFADFFF0),
    secondary = Dorado,
    onSecondary = AzulProfundo,
    secondaryContainer = Color(0xFF514021),
    onSecondaryContainer = Color(0xFFFFE6B0),
    tertiary = Dorado,
    tertiaryContainer = Color(0xFF514021),
    onTertiaryContainer = Color(0xFFFFE6B0),
    background = AzulProfundo,
    onBackground = Color(0xFFE2EDF0),
    surface = AzulMedianoche,
    onSurface = Color(0xFFE2EDF0),
    surfaceVariant = Color(0xFF243F50),
    onSurfaceVariant = Color(0xFFB6CAD0),
    outline = Color(0xFF829AA6)
)

private val EsquemaClaro = lightColorScheme(
    primary = Turquesa,
    onPrimary = Blanco,
    primaryContainer = Color(0xFFC3F4E8),
    onPrimaryContainer = Color(0xFF003D36),
    secondary = AzulMedianoche,
    onSecondary = Blanco,
    secondaryContainer = Color(0xFFDCEAF1),
    onSecondaryContainer = AzulMedianoche,
    tertiary = DoradoOscuro,
    tertiaryContainer = Color(0xFFFFE6AB),
    onTertiaryContainer = Color(0xFF332600),
    background = Marfil,
    onBackground = Tinta,
    surface = Blanco,
    onSurface = Tinta,
    surfaceVariant = Color(0xFFE7EFED),
    onSurfaceVariant = GrisAzulado,
    outline = Color(0xFF657F87)
)

@Composable
fun TemaBolilla(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) EsquemaOscuro else EsquemaClaro,
        typography = TipografiaBolilla,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp)
        ),
        content = contenido
    )
}
