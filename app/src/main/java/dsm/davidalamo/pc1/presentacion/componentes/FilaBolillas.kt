package dsm.davidalamo.pc1.presentacion.componentes

import androidx.compose.foundation.background
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

@Composable
fun FilaBolillas(numeros: List<Int>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(6) { indice ->
            val numero = numeros.getOrNull(indice)
            Box(
                modifier = Modifier.size(40.dp)
                    .background(
                        if (numero != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .semantics {
                        contentDescription = if (numero != null) "Bolilla $numero" else "Bolilla pendiente"
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    numero?.toString() ?: "-",
                    fontWeight = FontWeight.Bold,
                    color = if (numero != null) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
