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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorVista(
    titulo: String,
    alVolver: (() -> Unit)? = null,
    alSalir: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
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
                .background(Brush.verticalGradient(listOf(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    MaterialTheme.colorScheme.background
                )))
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CabeceraSeisora()
            contenido()
        }
    }
}
