package dsm.davidalamo.pc1.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaOscuro = darkColorScheme(
    primary = MoradoClaro,
    secondary = GrisMoradoClaro,
    tertiary = VerdeClaro
)

private val EsquemaClaro = lightColorScheme(
    primary = MoradoOscuro,
    secondary = GrisMoradoOscuro,
    tertiary = VerdeOscuro,
    tertiaryContainer = FondoVerde,
    onTertiaryContainer = TextoSobreVerde,
    background = FondoClaro,
    surface = FondoClaro
)

@Composable
fun TemaBolilla(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) EsquemaOscuro else EsquemaClaro,
        typography = TipografiaBolilla,
        content = contenido
    )
}
