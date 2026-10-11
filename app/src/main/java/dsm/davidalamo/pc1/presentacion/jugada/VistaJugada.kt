package dsm.davidalamo.pc1.presentacion.jugada

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista
import dsm.davidalamo.pc1.presentacion.componentes.FilaBolillas

@Composable
fun VistaJugada(alVerSorteo: () -> Unit, alSalir: () -> Unit) {
    val numerosEjemplo = listOf(1, 7, 14, 22, 35, 45)
    ContenedorVista("Mi jugada", alSalir = alSalir) {
        Text("Elige tus 6 números", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold)
        Text("Del 1 al 45, sin repetir.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Jugada de ejemplo · 6 de 6", style = MaterialTheme.typography.titleMedium)
                FilaBolillas(numerosEjemplo)
            }
        }
        Text("Panel ilustrativo: la selección y el registro se implementarán en otro MVP.",
            style = MaterialTheme.typography.bodySmall)
        (1..45).toList().chunked(5).forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { numero ->
                    Card(Modifier.weight(1f)) {
                        Text(
                            numero.toString(),
                            modifier = Modifier.padding(12.dp),
                            fontWeight = if (numero in numerosEjemplo) FontWeight.Bold else FontWeight.Normal,
                            color = if (numero in numerosEjemplo) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Button(onClick = alVerSorteo, modifier = Modifier.fillMaxWidth()) {
            Text("Ver sorteo")
        }
    }
}
