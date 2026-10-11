package dsm.davidalamo.pc1.presentacion

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performScrollTo
import dsm.davidalamo.pc1.datos.modelos.Bolilla
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.ResumenSorteo
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import dsm.davidalamo.pc1.presentacion.sorteo.EstadoVistaSorteo
import dsm.davidalamo.pc1.presentacion.sorteo.VistaSorteo
import dsm.davidalamo.pc1.ui.tema.TemaBolilla
import org.junit.Rule
import org.junit.Test
import java.util.Date

class PruebasVistaSorteo {
    @get:Rule
    val reglaComposicion = createComposeRule()

    @Test
    fun actualizaResumenYEsperaSextaBolillaAntesDelResultado() {
        val numeros = listOf(5, 12, 18, 23, 34, 41)
        val resumen = ResumenSorteo(
            Sorteo("actual", "Sorteo prueba", Date(), EstadoSorteo.ABIERTO),
            emptyList(), Jugada("actual", "usuario", numeros), true
        )
        val estado = mutableStateOf(EstadoVistaSorteo(cargando = false, resumen = resumen))
        reglaComposicion.setContent {
            TemaBolilla { VistaSorteo(estado.value, {}, {}, {}) }
        }
        reglaComposicion.onNodeWithText("Esperando el inicio del sorteo.").assertIsDisplayed()
        (1..6).forEach { cantidad ->
            reglaComposicion.runOnIdle {
                estado.value = EstadoVistaSorteo(
                    cargando = false,
                    resumen = resumen.copy(
                        sorteo = resumen.sorteo.copy(estado = EstadoSorteo.EN_CURSO),
                        bolillas = numeros.take(cantidad).mapIndexed { indice, numero -> Bolilla(numero, indice + 1) }
                    )
                )
            }
            reglaComposicion.onNodeWithText("Bolillas publicadas: $cantidad de 6").performScrollTo().assertIsDisplayed()
            reglaComposicion.onNodeWithText("Aciertos: $cantidad de 6").performScrollTo().assertIsDisplayed()
            reglaComposicion.onNodeWithContentDescription("Bolilla 5, acierto").performScrollTo().assertIsDisplayed()
            reglaComposicion.onNodeWithText("GANADOR").assertDoesNotExist()
        }
        reglaComposicion.runOnIdle {
            estado.value = estado.value.copy(resumen = estado.value.resumen?.copy(
                sorteo = resumen.sorteo.copy(estado = EstadoSorteo.FINALIZADO),
                bolillas = numeros.take(5).mapIndexed { indice, numero -> Bolilla(numero, indice + 1) }
            ))
        }
        reglaComposicion.onNodeWithText("Esperando resultados: faltan bolillas por recibir.")
            .performScrollTo().assertIsDisplayed()
        reglaComposicion.onNodeWithText("GANADOR").assertDoesNotExist()
        reglaComposicion.runOnIdle {
            estado.value = estado.value.copy(resumen = estado.value.resumen?.copy(
                bolillas = numeros.mapIndexed { indice, numero -> Bolilla(numero, indice + 1) }
            ))
        }
        reglaComposicion.onNodeWithText("GANADOR").performScrollTo().assertIsDisplayed()
        reglaComposicion.runOnIdle {
            estado.value = estado.value.copy(resumen = estado.value.resumen?.copy(
                jugada = Jugada("actual", "otroUsuario", listOf(5, 12, 18, 23, 34, 42))
            ))
        }
        reglaComposicion.onNodeWithText("SIGUE INTENTANDO").performScrollTo().assertIsDisplayed()
        reglaComposicion.onNodeWithText("Aciertos: 5 de 6").performScrollTo().assertIsDisplayed()
        reglaComposicion.onNodeWithText("GANADOR").assertDoesNotExist()
    }
}
