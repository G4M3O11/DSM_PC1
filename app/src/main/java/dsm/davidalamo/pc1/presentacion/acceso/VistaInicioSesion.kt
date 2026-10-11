package dsm.davidalamo.pc1.presentacion.acceso

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dsm.davidalamo.pc1.R
import dsm.davidalamo.pc1.datos.remoto.RepositorioAutenticacion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import android.util.Patterns
import androidx.compose.material3.LinearProgressIndicator
import dsm.davidalamo.pc1.presentacion.componentes.CampoContrasena
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista

@Composable
fun VistaInicioSesion(
    alIngresar: (String) -> Unit,
    alCrearCuenta: () -> Unit,
    iniciarSesion: suspend (String, String) -> String = RepositorioAutenticacion::iniciarSesion
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var ingresando by remember { mutableStateOf(false) }
    var errorAcceso by remember { mutableStateOf<String?>(null) }
    val alcance = rememberCoroutineScope()
    ContenedorVista(stringResource(R.string.nombre_aplicacion)) {
        Text("Tu suerte empieza aquí", style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold)
        Text("Ingresa para conocer el recorrido de tu jugada.")
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, enabled = !ingresando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoContrasena(contrasena, { contrasena = it }, habilitado = !ingresando)
        errorAcceso?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (ingresando) LinearProgressIndicator(Modifier.fillMaxWidth())
        Button(
            onClick = {
                ingresando = true
                errorAcceso = null
                alcance.launch {
                    try {
                        val uidUsuario = iniciarSesion(correo, contrasena)
                        alIngresar(uidUsuario)
                    } catch (cancelacion: CancellationException) {
                        throw cancelacion
                    } catch (excepcion: Exception) {
                        errorAcceso = excepcion.localizedMessage ?: "No se pudo iniciar sesión."
                    } finally {
                        ingresando = false
                    }
                }
            },
            enabled = !ingresando && Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() && contrasena.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (ingresando) "Ingresando..." else "Iniciar sesión")
        }
        TextButton(onClick = alCrearCuenta, enabled = !ingresando, modifier = Modifier.fillMaxWidth()) {
            Text("Crear cuenta")
        }
    }
}
