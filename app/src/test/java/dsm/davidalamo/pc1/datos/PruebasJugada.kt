package dsm.davidalamo.pc1.datos

import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.ReglasJugada
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import dsm.davidalamo.pc1.presentacion.jugada.EstadoVistaJugada
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class PruebasJugada {
    private val numeros = listOf(1, 7, 14, 22, 35, 45)
    private val estadoListo = EstadoVistaJugada(
        cargando = false,
        sorteo = Sorteo("actual", "Sorteo Seisora", Date(), EstadoSorteo.ABIERTO),
        uidUsuario = "participante",
        numerosSeleccionados = numeros
    )

    @Test
    fun requiereSeisNumerosDistintosDentroDelRango() {
        assertTrue(ReglasJugada.esJugadaValida(numeros))
        assertFalse(ReglasJugada.esJugadaValida(numeros.take(5)))
        assertFalse(ReglasJugada.esJugadaValida(numeros + 2))
        assertFalse(ReglasJugada.esJugadaValida(listOf(1, 1, 2, 3, 4, 5)))
        assertFalse(ReglasJugada.esJugadaValida(listOf(0, 1, 2, 3, 4, 5)))
        assertFalse(ReglasJugada.esJugadaValida(listOf(1, 2, 3, 4, 5, 46)))
    }

    @Test
    fun requiereIdentificadorYUid() {
        assertTrue(runCatching { Jugada("", "usuario", numeros) }.isFailure)
        assertTrue(runCatching { Jugada("actual", "", numeros) }.isFailure)
        assertTrue(runCatching { Jugada("actual", "usuario", listOf(1, 2)) }.isFailure)
    }

    @Test
    fun habilitaRegistroSoloConParticipacionLista() {
        assertTrue(estadoListo.puedeRegistrar)
        assertFalse(estadoListo.copy(cargando = true).puedeRegistrar)
        assertFalse(estadoListo.copy(guardando = true).puedeRegistrar)
        assertFalse(estadoListo.copy(uidUsuario = null).puedeRegistrar)
        assertFalse(estadoListo.copy(errorCarga = "Sin conexión").puedeRegistrar)
        assertFalse(estadoListo.copy(numerosSeleccionados = listOf(1, 2)).puedeRegistrar)
        assertFalse(estadoListo.copy(jugadaRegistrada = Jugada("actual", "participante", numeros)).puedeRegistrar)
        assertFalse(estadoListo.copy(sorteo = estadoListo.sorteo?.copy(estado = EstadoSorteo.EN_CURSO)).puedeRegistrar)
        assertFalse(estadoListo.copy(sorteo = estadoListo.sorteo?.copy(estado = EstadoSorteo.FINALIZADO)).puedeRegistrar)
    }
}
