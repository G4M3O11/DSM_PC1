package dsm.davidalamo.pc1.presentacion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.activity.compose.setContent
import dsm.davidalamo.pc1.presentacion.navegacion.NavegacionAplicacion
import dsm.davidalamo.pc1.ui.tema.TemaBolilla
import dsm.davidalamo.pc1.ActividadPrincipal
import org.junit.Rule
import org.junit.Test

class PruebasNavegacion {
    @get:Rule
    val reglaComposicion = createAndroidComposeRule<ActividadPrincipal>()

    private fun mostrarAplicacion() {
        reglaComposicion.runOnUiThread {
            reglaComposicion.activity.setContent {
                TemaBolilla {
                    NavegacionAplicacion(
                        iniciarSesion = { _, _ -> "usuarioPrueba" },
                        registrarCuenta = { _, _ -> "usuarioPrueba" }
                    )
                }
            }
        }
        reglaComposicion.waitForIdle()
    }

    private fun ingresar() {
        reglaComposicion.onNodeWithText("Correo electrónico").performTextInput("usuario@example.com")
        reglaComposicion.onNodeWithText("Contraseña").performTextInput("clavePrueba")
        pulsarTexto("Iniciar sesión")
    }

    private fun pulsarTexto(texto: String) {
        reglaComposicion.onNodeWithText(texto).performScrollTo().performClick()
    }

    @Test
    fun recorrerVistasAutenticadasYSalir() {
        mostrarAplicacion()
        ingresar()
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
        pulsarTexto("Ver sorteo")
        reglaComposicion.onNodeWithText("Seguimiento del sorteo").assertIsDisplayed()
        pulsarTexto("Volver a mi jugada")
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
        reglaComposicion.onNodeWithText("Salir").performClick()
        reglaComposicion.onNodeWithText("Tu suerte empieza aquí").assertIsDisplayed()
    }

    @Test
    fun abrirRegistroVolverYContinuar() {
        mostrarAplicacion()
        pulsarTexto("Crear cuenta")
        reglaComposicion.onNodeWithText("Únete al sorteo").assertIsDisplayed()
        reglaComposicion.onNodeWithText("Volver").performClick()
        reglaComposicion.onNodeWithText("Tu suerte empieza aquí").assertIsDisplayed()
        pulsarTexto("Crear cuenta")
        reglaComposicion.onNodeWithText("Correo electrónico").performTextInput("usuario@example.com")
        reglaComposicion.onNodeWithText("Contraseña").performTextInput("clavePrueba")
        reglaComposicion.onNodeWithText("Confirmar contraseña").performTextInput("clavePrueba")
        pulsarTexto("Registrar cuenta")
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun salirDesdeSorteoLimpiaLaPila() {
        mostrarAplicacion()
        ingresar()
        pulsarTexto("Ver sorteo")
        reglaComposicion.onNodeWithText("Salir").performClick()
        reglaComposicion.onNodeWithText("Tu suerte empieza aquí").assertIsDisplayed()
        pulsarTexto("Crear cuenta")
        reglaComposicion.onNodeWithText("Volver").performClick()
        reglaComposicion.onNodeWithText("Tu suerte empieza aquí").assertIsDisplayed()
    }

    @Test
    fun retrocesoDelSistemaVuelveDesdeSorteoAJugada() {
        mostrarAplicacion()
        ingresar()
        pulsarTexto("Ver sorteo")
        reglaComposicion.waitForIdle()
        reglaComposicion.runOnUiThread {
            reglaComposicion.activity.onBackPressedDispatcher.onBackPressed()
        }
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
    }
}
