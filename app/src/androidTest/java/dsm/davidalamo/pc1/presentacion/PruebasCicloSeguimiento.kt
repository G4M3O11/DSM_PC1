package dsm.davidalamo.pc1.presentacion

import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dsm.davidalamo.pc1.datos.modelos.ResumenSorteo
import dsm.davidalamo.pc1.datos.remoto.FuenteSeguimiento
import dsm.davidalamo.pc1.presentacion.sorteo.ModeloVistaSorteo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class PruebasCicloSeguimiento {
    @get:Rule
    val reglaComposicion = createComposeRule()

    @Test
    fun escuchaSoloReanudadaSinDuplicadosYReintentaLiberandoLaAnterior() {
        val activas = AtomicInteger()
        val iniciadas = AtomicInteger()
        val fuente = object : FuenteSeguimiento {
            override fun observar(): Flow<Result<ResumenSorteo>> = callbackFlow {
                activas.incrementAndGet()
                iniciadas.incrementAndGet()
                awaitClose { activas.decrementAndGet() }
            }
        }
        val propietario = object : LifecycleOwner {
            val registro = LifecycleRegistry(this)
            override val lifecycle: Lifecycle get() = registro
        }
        val modelo = ModeloVistaSorteo(fuente)
        val almacen = ViewModelStore()
        almacen.put("sorteo", modelo)
        try {
            reglaComposicion.runOnUiThread {
                propietario.registro.currentState = Lifecycle.State.RESUMED
            }
            reglaComposicion.setContent {
                val estado by modelo.estado.collectAsStateWithLifecycle(
                    lifecycle = propietario.lifecycle, minActiveState = Lifecycle.State.RESUMED
                )
                Text(if (estado.cargando) "Esperando" else "Recibido")
            }
            reglaComposicion.waitUntil { activas.get() == 1 }
            repeat(3) {
                reglaComposicion.runOnUiThread {
                    propietario.registro.currentState = Lifecycle.State.STARTED
                }
                reglaComposicion.waitUntil { activas.get() == 0 }
                reglaComposicion.runOnUiThread {
                    propietario.registro.currentState = Lifecycle.State.RESUMED
                }
                reglaComposicion.waitUntil { activas.get() == 1 }
            }
            assertEquals(4, iniciadas.get())
            reglaComposicion.runOnUiThread { modelo.reintentar() }
            reglaComposicion.waitUntil { iniciadas.get() == 5 }
            assertEquals(1, activas.get())
            reglaComposicion.runOnUiThread {
                propietario.registro.currentState = Lifecycle.State.DESTROYED
            }
            reglaComposicion.waitUntil { activas.get() == 0 }
        } finally {
            reglaComposicion.runOnUiThread { almacen.clear() }
        }
    }
}
