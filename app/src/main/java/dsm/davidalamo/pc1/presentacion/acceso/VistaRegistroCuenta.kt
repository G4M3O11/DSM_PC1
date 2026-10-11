package dsm.davidalamo.pc1.presentacion.acceso

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dsm.davidalamo.pc1.presentacion.componentes.CampoContrasena
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista
import dsm.davidalamo.pc1.datos.remoto.RepositorioAutenticacion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import android.util.Patterns
import androidx.compose.material3.LinearProgressIndicator

@Composable
fun VistaRegistroCuenta(
    alContinuar: (String) -> Unit,
    alVolver: () -> Unit,
    registrarCuenta: suspend (String, String) -> String = RepositorioAutenticacion::registrarCuenta
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }
    var registrando by remember { mutableStateOf(false) }
    var errorRegistro by remember { mutableStateOf<String?>(null) }
    val alcance = rememberCoroutineScope()
    ContenedorVista("Crear cuenta", alVolver = if (registrando) null else alVolver) {
        Text("Únete al sorteo", style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, enabled = !registrando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoContrasena(contrasena, { contrasena = it }, habilitado = !registrando)
        CampoContrasena(confirmacionContrasena, { confirmacionContrasena = it }, "Confirmar contraseña",
            habilitado = !registrando)
        Text("Mínimo 6 caracteres. Las contraseñas deben coincidir.", style = MaterialTheme.typography.bodySmall)
        errorRegistro?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (registrando) LinearProgressIndicator(Modifier.fillMaxWidth())
        Button(
            onClick = {
                registrando = true
                errorRegistro = null
                alcance.launch {
                    try {
                        val uidUsuario = registrarCuenta(correo, contrasena)
                        alContinuar(uidUsuario)
                    } catch (cancelacion: CancellationException) {
                        throw cancelacion
                    } catch (excepcion: Exception) {
                        errorRegistro = excepcion.localizedMessage ?: "No se pudo registrar la cuenta."
                    } finally {
                        registrando = false
                    }
                }
            },
            enabled = !registrando && Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() &&
                contrasena.length >= 6 && contrasena == confirmacionContrasena,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (registrando) "Creando cuenta..." else "Registrar cuenta")
        }
    }
}
