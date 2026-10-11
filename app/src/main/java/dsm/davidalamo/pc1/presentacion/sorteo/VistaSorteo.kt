package dsm.davidalamo.pc1.presentacion.sorteo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista
import dsm.davidalamo.pc1.presentacion.componentes.FilaBolillas

@Composable
fun VistaSorteo(alVolver: () -> Unit, alSalir: () -> Unit) {
    ContenedorVista("Sorteo", alVolver = alVolver, alSalir = alSalir) {
        Text("Así se verá tu sorteo", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold)
        Text("Datos de ejemplo: no hay un sorteo en tiempo real en este MVP.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Bolillas de ejemplo · 3/6", style = MaterialTheme.typography.titleMedium)
                FilaBolillas(listOf(1, 7, 14))
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Mi jugada de ejemplo", style = MaterialTheme.typography.titleMedium)
                FilaBolillas(listOf(1, 7, 14, 22, 35, 45))
                Text("Progreso ilustrativo: 3 de 6 aciertos · 50 %",
                    style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(progress = { 0.5f }, modifier = Modifier.fillMaxWidth())
                Text("La actualización de bolillas, los aciertos y el resultado final quedan para el siguiente MVP.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a mi jugada")
        }
    }
}
