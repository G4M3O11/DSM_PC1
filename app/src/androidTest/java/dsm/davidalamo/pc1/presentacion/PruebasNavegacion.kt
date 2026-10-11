package dsm.davidalamo.pc1.presentacion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dsm.davidalamo.pc1.ActividadPrincipal
import org.junit.Rule
import org.junit.Test

class PruebasNavegacion {
    @get:Rule
    val reglaComposicion = createAndroidComposeRule<ActividadPrincipal>()

    private fun mostrarAplicacion() {
        reglaComposicion.waitForIdle()
    }

    private fun pulsarTexto(texto: String) {
        reglaComposicion.onNodeWithText(texto).performScrollTo().performClick()
    }

    @Test
    fun recorrerVistasSinCredencialesYSalir() {
        mostrarAplicacion()
        pulsarTexto("Ingresar al prototipo")
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
        pulsarTexto("Continuar al prototipo")
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun salirDesdeSorteoLimpiaLaPila() {
        mostrarAplicacion()
        pulsarTexto("Ingresar al prototipo")
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
        pulsarTexto("Ingresar al prototipo")
        pulsarTexto("Ver sorteo")
        reglaComposicion.waitForIdle()
        reglaComposicion.runOnUiThread {
            reglaComposicion.activity.onBackPressedDispatcher.onBackPressed()
        }
        reglaComposicion.onNodeWithText("Elige tus 6 números").performScrollTo().assertIsDisplayed()
    }
}
