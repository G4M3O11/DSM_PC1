package dsm.davidalamo.pc1.datos

import dsm.davidalamo.pc1.datos.modelos.Bolilla
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.ResumenSorteo
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import dsm.davidalamo.pc1.datos.modelos.validarBolillas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class PruebasSeguimiento {
    private val numeros = listOf(5, 12, 18, 23, 34, 41)
    private val ganadora = Jugada("actual", "usuarioA", numeros)
    private val perdedora = Jugada("actual", "usuarioB", listOf(5, 12, 18, 23, 34, 42))

    private fun resumen(
        cantidad: Int,
        estado: EstadoSorteo,
        jugada: Jugada? = ganadora,
        confirmado: Boolean = true
    ) = ResumenSorteo(
        Sorteo("actual", "Sorteo", Date(), estado),
        numeros.take(cantidad).mapIndexed { indice, numero -> Bolilla(numero, indice + 1) },
        jugada, confirmado
    )

    @Test
    fun seisPublicacionesActualizanAmbasJugadasSinDependerDelOrden() {
        (0..6).forEach { cantidad ->
            val estado = if (cantidad == 0) EstadoSorteo.ABIERTO else EstadoSorteo.EN_CURSO
            val resultadoA = resumen(cantidad, estado)
            val resultadoB = resumen(cantidad, estado, perdedora)
            assertEquals(cantidad, resultadoA.bolillas.size)
            assertEquals(cantidad, resultadoA.cantidadAciertos)
            assertEquals(minOf(cantidad, 5), resultadoB.cantidadAciertos)
            assertEquals(numeros, resultadoA.jugada?.numeros)
            assertNull(resultadoA.resultadoFinal)
            assertNull(resultadoB.resultadoFinal)
        }
        assertEquals("GANADOR", resumen(6, EstadoSorteo.FINALIZADO).resultadoFinal)
        assertEquals("SIGUE INTENTANDO", resumen(6, EstadoSorteo.FINALIZADO, perdedora).resultadoFinal)
    }

    @Test
    fun quintaBolillaTieneCincoAciertosParaAmbosSinResultadoFinal() {
        listOf(ganadora, perdedora).forEach { jugada ->
            val quinto = resumen(5, EstadoSorteo.EN_CURSO, jugada)
            assertEquals(5, quinto.cantidadAciertos)
            assertNull(quinto.resultadoFinal)
            val sexto = resumen(6, EstadoSorteo.EN_CURSO, jugada)
            assertNull(sexto.resultadoFinal)
        }
    }

    @Test
    fun reconstruyeResultadoConJugadaYBolillasRecuperadasSinResultadoAlmacenado() {
        listOf(ganadora, perdedora).forEach { jugada ->
            val anterior = resumen(6, EstadoSorteo.FINALIZADO, jugada)
            val recuperado = ResumenSorteo(
                anterior.sorteo.copy(),
                anterior.bolillas.map { it.copy() },
                jugada.copy(numeros = jugada.numeros.toList()),
                datosConfirmados = true
            )
            assertEquals(anterior.cantidadAciertos, recuperado.cantidadAciertos)
            assertEquals(anterior.resultadoFinal, recuperado.resultadoFinal)
            assertEquals(jugada.numeros, recuperado.jugada?.numeros)
        }
        val ordenInverso = resumen(6, EstadoSorteo.FINALIZADO, ganadora.copy(numeros = numeros.reversed()))
        assertEquals(6, ordenInverso.cantidadAciertos)
        assertEquals("GANADOR", ordenInverso.resultadoFinal)
        assertEquals(numeros.reversed(), ordenInverso.jugada?.numeros)
    }

    @Test
    fun resultadoEsperaEstadoFinalSeisBolillasYConfirmacion() {
        (0..5).forEach { cantidad ->
            val parcial = resumen(cantidad, EstadoSorteo.FINALIZADO)
            assertNull(parcial.resultadoFinal)
            assertTrue(parcial.mensajeEstado.startsWith("Esperando resultados"))
        }
        assertNull(resumen(6, EstadoSorteo.EN_CURSO).resultadoFinal)
        assertNull(resumen(6, EstadoSorteo.FINALIZADO, confirmado = false).resultadoFinal)
        assertNull(resumen(6, EstadoSorteo.FINALIZADO, jugada = null).resultadoFinal)
        assertEquals("Esperando el inicio del sorteo.", resumen(0, EstadoSorteo.ABIERTO).mensajeEstado)
        assertEquals("Sorteo en curso", resumen(3, EstadoSorteo.EN_CURSO).mensajeEstado)
    }

    @Test
    fun ordenaPorSalidaNoPorNumeroNiPorNombreDocumento() {
        val desordenadas = listOf(Bolilla(7, 2), Bolilla(45, 1), Bolilla(1, 3))
        assertEquals(listOf(45, 7, 1), validarBolillas(desordenadas).map { it.numero })
        assertTrue(validarBolillas(emptyList()).isEmpty())
    }

    @Test
    fun rechazaNumerosOrdenesDuplicadosHuecosYRangosInvalidos() {
        val invalidas = listOf(
            listOf(Bolilla(1, 1), Bolilla(1, 2)),
            listOf(Bolilla(1, 1), Bolilla(2, 1)),
            listOf(Bolilla(1, 2)),
            listOf(Bolilla(1, 1), Bolilla(2, 3)),
            (1..6).map { Bolilla(it, it) } + Bolilla(7, 6)
        )
        invalidas.forEach { assertTrue(runCatching { validarBolillas(it) }.isFailure) }
        listOf(0, 46, Int.MIN_VALUE, Int.MAX_VALUE).forEach {
            assertTrue(runCatching { Bolilla(it, 1) }.isFailure)
        }
        listOf(0, 7).forEach {
            assertTrue(runCatching { Bolilla(1, it) }.isFailure)
        }
    }

    @Test
    fun rechazaJugadaDeOtroSorteo() {
        assertTrue(runCatching {
            resumen(6, EstadoSorteo.FINALIZADO, Jugada("otro", "usuario", numeros))
        }.isFailure)
    }
}
