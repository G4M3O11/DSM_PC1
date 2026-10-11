package dsm.davidalamo.pc1.presentacion.jugada

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista
import dsm.davidalamo.pc1.presentacion.componentes.FilaBolillas
import java.text.DateFormat

@Composable
fun VistaJugada(
    estado: EstadoVistaJugada,
    alElegirNumero: (Int) -> Unit,
    alRegistrar: () -> Unit,
    alReintentar: () -> Unit,
    alVerSorteo: () -> Unit,
    alSalir: () -> Unit
) {
    var confirmarRegistro by rememberSaveable { mutableStateOf(false) }
    ContenedorVista(
        "Mi jugada",
        alSalir = if (estado.guardando) null else alSalir
    ) {
        Text("Elige tus 6 números", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Del 1 al 45, sin repetir. Una jugada por usuario y sorteo.")
        if (estado.cargando || estado.guardando) LinearProgressIndicator(Modifier.fillMaxWidth())
        estado.sorteo?.let { sorteo ->
            Text("${sorteo.nombre} · ${DateFormat.getDateTimeInstance().format(sorteo.fecha)}")
            Text("Sorteo: ${sorteo.identificador} · Estado: ${sorteo.estado.valorFirestore}")
        }
        estado.uidUsuario?.let { Text("UID del participante: $it", style = MaterialTheme.typography.bodySmall) }
        estado.errorCarga?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = alReintentar) { Text("Reintentar conexión") }
        }
        estado.errorRegistro?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = alReintentar, enabled = !estado.guardando) { Text("Actualizar participación") }
        }
        estado.mensajeSeleccion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (estado.sorteo != null && estado.sorteo.estado != EstadoSorteo.ABIERTO && estado.jugadaRegistrada == null) {
            Text("El sorteo ya no admite nuevas jugadas.", color = MaterialTheme.colorScheme.error)
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (estado.jugadaRegistrada != null) "Jugada confirmada"
                    else "${estado.numerosSeleccionados.size} de 6 seleccionados",
                    style = MaterialTheme.typography.titleMedium
                )
                FilaBolillas(estado.jugadaRegistrada?.numeros ?: estado.numerosSeleccionados)
            }
        }
        if (estado.jugadaRegistrada == null) {
            (1..45).toList().chunked(5).forEach { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fila.forEach { numero ->
                        val elegido = numero in estado.numerosSeleccionados
                        val modificador = Modifier.weight(1f).sizeIn(minHeight = 48.dp)
                            .semantics { selected = elegido }
                        if (elegido) {
                            Button(
                                onClick = { alElegirNumero(numero) }, enabled = estado.puedeSeleccionar,
                                modifier = modificador, contentPadding = PaddingValues(0.dp)
                            ) { Text(numero.toString()) }
                        } else {
                            OutlinedButton(
                                onClick = { alElegirNumero(numero) }, enabled = estado.puedeSeleccionar,
                                modifier = modificador, contentPadding = PaddingValues(0.dp)
                            ) { Text(numero.toString()) }
                        }
                    }
                }
            }
            Button(
                onClick = { confirmarRegistro = true },
                enabled = estado.puedeRegistrar,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (estado.guardando) "Registrando..." else "Registrar jugada") }
        } else {
            Text("Tu jugada está confirmada y no se puede modificar.")
        }
        OutlinedButton(onClick = alVerSorteo, enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()) {
            Text("Ver sorteo")
        }
    }
    if (confirmarRegistro) {
        AlertDialog(
            onDismissRequest = { confirmarRegistro = false },
            title = { Text("Confirmar jugada") },
            text = { Text("Guardar ${estado.numerosSeleccionados.joinToString(", ")}. No podrás modificar esta jugada.") },
            confirmButton = {
                TextButton(
                    onClick = { confirmarRegistro = false; alRegistrar() }, enabled = estado.puedeRegistrar
                ) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { confirmarRegistro = false }) { Text("Cancelar") } }
        )
    }
}
