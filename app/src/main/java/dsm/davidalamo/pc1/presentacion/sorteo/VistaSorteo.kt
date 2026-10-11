package dsm.davidalamo.pc1.presentacion.sorteo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista
import dsm.davidalamo.pc1.presentacion.componentes.FilaBolillas
import java.text.DateFormat

@Composable
fun VistaSorteo(
    estado: EstadoVistaSorteo,
    alReintentar: () -> Unit,
    alVolver: () -> Unit,
    alSalir: () -> Unit
) {
    ContenedorVista(
        "Sorteo", alVolver = alVolver, alSalir = alSalir
    ) {
        Text("Seguimiento del sorteo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (estado.cargando) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Conectando con tu sorteo...")
        }
        estado.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Text("No se puede determinar el resultado con datos inválidos o incompletos.")
            TextButton(onClick = alReintentar) { Text("Reintentar conexión") }
        }
        estado.resumen?.let { resumen ->
            Text(resumen.sorteo.nombre, style = MaterialTheme.typography.titleLarge)
            Text("Sorteo: ${resumen.sorteo.identificador} · ${DateFormat.getDateTimeInstance().format(resumen.sorteo.fecha)}")
            Text(resumen.mensajeEstado, color = MaterialTheme.colorScheme.primary)
            if (!resumen.datosConfirmados) {
                Text("Datos pendientes de confirmación. Esperando conexión con el sorteo.",
                    color = MaterialTheme.colorScheme.error)
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Bolillas publicadas: ${resumen.bolillas.size} de 6",
                        style = MaterialTheme.typography.titleMedium)
                    FilaBolillas(resumen.bolillas.map { it.numero })
                    Text("En orden de salida, del 1 al 6.", style = MaterialTheme.typography.bodySmall)
                }
            }
            val jugada = resumen.jugada
            if (jugada == null) {
                Text("No tienes una jugada registrada en este sorteo.")
            } else {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Mi jugada registrada", style = MaterialTheme.typography.titleMedium)
                        FilaBolillas(jugada.numeros, resumen.numerosAcertados)
                        Text("En el orden registrado. Los aciertos están resaltados.",
                            style = MaterialTheme.typography.bodySmall)
                        Text("Aciertos: ${resumen.cantidadAciertos} de 6",
                            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        if (resumen.numerosAcertados.isNotEmpty()) {
                            Text("Números acertados: ${jugada.numeros.filter { it in resumen.numerosAcertados }.joinToString(", ")}")
                        }
                    }
                }
            }
            resumen.resultadoFinal?.let { resultado ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(resultado, Modifier.padding(20.dp),
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
        OutlinedButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver a mi jugada") }
    }
}
