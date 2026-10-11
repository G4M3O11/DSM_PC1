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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dsm.davidalamo.pc1.R
import dsm.davidalamo.pc1.presentacion.componentes.CampoContrasena
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista

@Composable
fun VistaInicioSesion(alIngresar: () -> Unit, alCrearCuenta: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    ContenedorVista(stringResource(R.string.nombre_aplicacion)) {
        Text("Tu suerte empieza aquí", style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold)
        Text("Ingresa para conocer el recorrido de tu jugada.")
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoContrasena(contrasena, { contrasena = it })
        Text("Los campos son ilustrativos. Puedes continuar sin ingresar credenciales.",
            style = MaterialTheme.typography.bodySmall)
        Button(onClick = alIngresar, modifier = Modifier.fillMaxWidth()) {
            Text("Ingresar al prototipo")
        }
        TextButton(onClick = alCrearCuenta, modifier = Modifier.fillMaxWidth()) {
            Text("Crear cuenta")
        }
    }
}
