package dsm.davidalamo.pc1.presentacion.jugada

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.ReglasJugada
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import dsm.davidalamo.pc1.datos.remoto.RepositorioJugadas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoVistaJugada(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val sorteo: Sorteo? = null,
    val uidUsuario: String? = null,
    val numerosSeleccionados: List<Int> = emptyList(),
    val jugadaRegistrada: Jugada? = null,
    val errorCarga: String? = null,
    val errorRegistro: String? = null,
    val mensajeSeleccion: String? = null
) {
    val puedeSeleccionar: Boolean
        get() = !cargando && !guardando && errorCarga == null && uidUsuario != null &&
            sorteo?.estado == EstadoSorteo.ABIERTO && jugadaRegistrada == null
    val puedeRegistrar: Boolean
        get() = puedeSeleccionar && ReglasJugada.esJugadaValida(numerosSeleccionados)
}

class ModeloVistaJugada : ViewModel() {
    private val repositorio = RepositorioJugadas()
    private val estadoMutable = MutableStateFlow(EstadoVistaJugada())
    val estado = estadoMutable.asStateFlow()
    private var tareaCarga: Job? = null

    init { cargar() }

    fun cargar() {
        if (estadoMutable.value.guardando || tareaCarga?.isActive == true) return
        estadoMutable.update { it.copy(cargando = true, errorCarga = null, errorRegistro = null) }
        tareaCarga = viewModelScope.launch {
            try {
                val datos = repositorio.cargarParticipacion()
                estadoMutable.update {
                    it.copy(
                        cargando = false, sorteo = datos.sorteo, uidUsuario = datos.uidUsuario,
                        jugadaRegistrada = datos.jugada,
                        numerosSeleccionados = datos.jugada?.numeros
                            ?: if (it.uidUsuario == null || it.uidUsuario == datos.uidUsuario) it.numerosSeleccionados
                            else emptyList()
                    )
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (excepcion: Exception) {
                estadoMutable.update {
                    it.copy(cargando = false, errorCarga = excepcion.localizedMessage ?: "No se pudo cargar la participación.")
                }
            }
        }
    }

    fun alternarNumero(numero: Int) {
        estadoMutable.update {
            when {
                !it.puedeSeleccionar -> it
                numero !in 1..45 -> it.copy(mensajeSeleccion = "El número debe estar entre 1 y 45.")
                numero in it.numerosSeleccionados ->
                    it.copy(numerosSeleccionados = it.numerosSeleccionados - numero, mensajeSeleccion = null)
                it.numerosSeleccionados.size < 6 ->
                    it.copy(numerosSeleccionados = it.numerosSeleccionados + numero, mensajeSeleccion = null)
                else -> it.copy(mensajeSeleccion = "Ya elegiste seis números. Deselecciona uno para cambiarlo.")
            }
        }
    }

    fun registrar() {
        val actual = estadoMutable.value
        if (!actual.puedeRegistrar) {
            estadoMutable.update { it.copy(errorRegistro = "Selecciona seis números y verifica que el sorteo esté abierto.") }
            return
        }
        val jugada = Jugada(
            requireNotNull(actual.sorteo).identificador,
            requireNotNull(actual.uidUsuario), actual.numerosSeleccionados
        )
        estadoMutable.update { it.copy(guardando = true, errorRegistro = null) }
        viewModelScope.launch {
            try {
                repositorio.registrarJugada(jugada)
                estadoMutable.update { it.copy(guardando = false, jugadaRegistrada = jugada) }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (excepcion: Exception) {
                estadoMutable.update {
                    it.copy(guardando = false, errorRegistro = excepcion.localizedMessage ?: "No se pudo registrar la jugada.")
                }
            }
        }
    }
}
