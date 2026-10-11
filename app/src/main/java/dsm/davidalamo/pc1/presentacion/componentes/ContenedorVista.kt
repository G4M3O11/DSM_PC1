package dsm.davidalamo.pc1.presentacion.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorVista(
    titulo: String,
    alVolver: (() -> Unit)? = null,
    alSalir: (() -> Unit)? = null,
    aviso: String = "Prototipo de navegación · Sin conexión a Firebase",
    contenido: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (alVolver != null) TextButton(onClick = alVolver) { Text("Volver") }
                },
                actions = {
                    if (alSalir != null) TextButton(onClick = alSalir) { Text("Salir") }
                }
            )
        }
    ) { espacioInterior ->
        Column(
            modifier = Modifier.fillMaxSize().padding(espacioInterior)
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                aviso,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}
