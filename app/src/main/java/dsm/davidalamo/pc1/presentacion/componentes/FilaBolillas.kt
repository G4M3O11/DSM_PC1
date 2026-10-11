package dsm.davidalamo.pc1.presentacion.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow

@Composable
fun FilaBolillas(numeros: List<Int>, numerosAcertados: Set<Int> = emptySet()) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(6) { indice ->
            val numero = numeros.getOrNull(indice)
            val esAcierto = numero in numerosAcertados
            Box(
                modifier = Modifier.size(40.dp)
                    .shadow(if (numero != null) 4.dp else 0.dp, CircleShape)
                    .background(
                        Brush.verticalGradient(
                            when {
                                esAcierto -> listOf(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                                numero != null -> listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
                                )
                                else -> listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        ),
                        CircleShape
                    )
                    .border(
                        if (esAcierto) 2.dp else 1.dp,
                        if (esAcierto) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .semantics {
                        contentDescription = when {
                            esAcierto -> "Bolilla $numero, acierto"
                            numero != null -> "Bolilla $numero"
                            else -> "Bolilla pendiente"
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    numero?.toString() ?: "-",
                    fontWeight = FontWeight.Bold,
                    color = if (esAcierto) MaterialTheme.colorScheme.onTertiaryContainer
                    else if (numero != null) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
