package dsm.davidalamo.pc1.presentacion.sorteo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dsm.davidalamo.pc1.datos.modelos.ResumenSorteo
import dsm.davidalamo.pc1.datos.remoto.RepositorioSeguimiento
import dsm.davidalamo.pc1.datos.remoto.FuenteSeguimiento
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class EstadoVistaSorteo(
    val cargando: Boolean = true,
    val resumen: ResumenSorteo? = null,
    val error: String? = null
)

class ModeloVistaSorteo(
    private val repositorio: FuenteSeguimiento = RepositorioSeguimiento()
) : ViewModel() {
    private val intentos = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado = intentos.flatMapLatest {
        repositorio.observar().map { resultado ->
            if (resultado.isSuccess) EstadoVistaSorteo(cargando = false, resumen = resultado.getOrThrow())
            else EstadoVistaSorteo(
                cargando = false,
                error = resultado.exceptionOrNull()?.localizedMessage ?: "No se pudo seguir el sorteo."
            )
        }.catch { excepcion ->
            emit(EstadoVistaSorteo(
                cargando = false,
                error = excepcion.localizedMessage ?: "No se pudo iniciar la escucha del sorteo."
            ))
        }.onStart { emit(EstadoVistaSorteo()) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 0, replayExpirationMillis = 0),
        EstadoVistaSorteo()
    )

    fun reintentar() { intentos.update { it + 1 } }
}
