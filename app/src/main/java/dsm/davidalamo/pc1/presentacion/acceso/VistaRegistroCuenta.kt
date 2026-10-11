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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dsm.davidalamo.pc1.presentacion.componentes.CampoContrasena
import dsm.davidalamo.pc1.presentacion.componentes.ContenedorVista

@Composable
fun VistaRegistroCuenta(alContinuar: () -> Unit, alVolver: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }
    ContenedorVista("Crear cuenta", alVolver = alVolver) {
        Text("Únete al sorteo", style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoContrasena(contrasena, { contrasena = it })
        CampoContrasena(confirmacionContrasena, { confirmacionContrasena = it }, "Confirmar contraseña")
        Text("Vista ilustrativa: no se crea una cuenta ni se envían estos datos.",
            style = MaterialTheme.typography.bodySmall)
        Button(onClick = alContinuar, modifier = Modifier.fillMaxWidth()) {
            Text("Continuar al prototipo")
        }
    }
}
