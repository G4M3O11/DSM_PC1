package dsm.davidalamo.pc1.presentacion

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import dsm.davidalamo.pc1.presentacion.jugada.EstadoVistaJugada
import dsm.davidalamo.pc1.presentacion.jugada.VistaJugada
import dsm.davidalamo.pc1.ui.tema.TemaBolilla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Date

class PruebasSeleccionJugada {
    @get:Rule
    val reglaComposicion = createComposeRule()

    @Test
    fun seleccionaSeisDeseleccionaYConfirma() {
        val estado = mutableStateOf(EstadoVistaJugada(
            cargando = false,
            sorteo = Sorteo("actual", "Sorteo prueba", Date(), EstadoSorteo.ABIERTO),
            uidUsuario = "usuario"
        ))
        var registroSolicitado = false
        reglaComposicion.setContent {
            TemaBolilla {
                VistaJugada(
                    estado.value,
                    alElegirNumero = { numero ->
                        val seleccion = estado.value.numerosSeleccionados
                        estado.value = estado.value.copy(
                            numerosSeleccionados = if (numero in seleccion) seleccion - numero
                            else if (seleccion.size < 6) seleccion + numero else seleccion
                        )
                    },
                    alRegistrar = { registroSolicitado = true },
                    alReintentar = {}, alVerSorteo = {}, alSalir = {}
                )
            }
        }
        reglaComposicion.onNodeWithText("Registrar jugada").performScrollTo().assertIsNotEnabled()
        // Los numeros elegidos tambien aparecen en las bolillas; selecciona el boton por su accion.
        (1..6).forEach { numero ->
            reglaComposicion.onNode(
                androidx.compose.ui.test.hasText(numero.toString()) and androidx.compose.ui.test.hasClickAction()
            ).performScrollTo().performClick()
        }
        reglaComposicion.onNodeWithText("Registrar jugada").performScrollTo().assertIsEnabled()
        reglaComposicion.onNode(
            androidx.compose.ui.test.hasText("7") and androidx.compose.ui.test.hasClickAction()
        ).performScrollTo().performClick()
        reglaComposicion.runOnIdle { assertEquals(6, estado.value.numerosSeleccionados.size) }
        reglaComposicion.onNode(
            androidx.compose.ui.test.hasText("6") and androidx.compose.ui.test.hasClickAction()
        ).performScrollTo().performClick()
        reglaComposicion.onNodeWithText("Registrar jugada").performScrollTo().assertIsNotEnabled()
        reglaComposicion.onNode(
            androidx.compose.ui.test.hasText("45") and androidx.compose.ui.test.hasClickAction()
        ).performScrollTo().performClick()
        reglaComposicion.onNodeWithText("Registrar jugada").performScrollTo().performClick()
        reglaComposicion.onNodeWithText("Confirmar").performClick()
        reglaComposicion.runOnIdle { assertTrue(registroSolicitado) }
    }
}
