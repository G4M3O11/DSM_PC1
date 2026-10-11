package dsm.davidalamo.pc1.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import dsm.davidalamo.pc1.presentacion.sorteo.ModeloVistaSorteo
import dsm.davidalamo.pc1.presentacion.jugada.ModeloVistaJugada
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dsm.davidalamo.pc1.presentacion.acceso.VistaInicioSesion
import dsm.davidalamo.pc1.presentacion.acceso.VistaRegistroCuenta
import dsm.davidalamo.pc1.presentacion.jugada.VistaJugada
import dsm.davidalamo.pc1.presentacion.sorteo.VistaSorteo
import dsm.davidalamo.pc1.datos.remoto.RepositorioAutenticacion

object Rutas {
    const val INICIO_SESION = "inicio_sesion"
    const val REGISTRO_CUENTA = "registro_cuenta"
    const val JUGADA = "jugada"
    const val SORTEO = "sorteo"
}

@Composable
fun NavegacionAplicacion(
    iniciarSesion: suspend (String, String) -> String = RepositorioAutenticacion::iniciarSesion,
    registrarCuenta: suspend (String, String) -> String = RepositorioAutenticacion::registrarCuenta
) {
    val controladorNavegacion = rememberNavController()
    NavHost(controladorNavegacion, startDestination = Rutas.INICIO_SESION) {
        composable(Rutas.INICIO_SESION) {
            VistaInicioSesion(
                alIngresar = { controladorNavegacion.abrirJugada() },
                iniciarSesion = iniciarSesion,
                alCrearCuenta = {
                    controladorNavegacion.navigate(Rutas.REGISTRO_CUENTA) { launchSingleTop = true }
                }
            )
        }
        composable(Rutas.REGISTRO_CUENTA) {
            VistaRegistroCuenta(
                alContinuar = { controladorNavegacion.abrirJugada() },
                registrarCuenta = registrarCuenta,
                alVolver = { controladorNavegacion.popBackStack() }
            )
        }
        composable(Rutas.JUGADA) {
            val modeloVista: ModeloVistaJugada = viewModel()
            val estado by modeloVista.estado.collectAsStateWithLifecycle()
            VistaJugada(
                estado = estado,
                alElegirNumero = modeloVista::alternarNumero,
                alRegistrar = modeloVista::registrar,
                alReintentar = modeloVista::cargar,
                alVerSorteo = {
                    controladorNavegacion.navigate(Rutas.SORTEO) { launchSingleTop = true }
                },
                alSalir = { controladorNavegacion.volverAlInicio() }
            )
        }
        composable(Rutas.SORTEO) {
            val modeloVista: ModeloVistaSorteo = viewModel()
            val estado by modeloVista.estado.collectAsStateWithLifecycle(
                minActiveState = Lifecycle.State.RESUMED
            )
            VistaSorteo(
                estado = estado,
                alReintentar = modeloVista::reintentar,
                alVolver = { controladorNavegacion.popBackStack() },
                alSalir = { controladorNavegacion.volverAlInicio() }
            )
        }
    }
}

private fun NavHostController.abrirJugada() {
    navigate(Rutas.JUGADA) {
        popUpTo(Rutas.INICIO_SESION) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.volverAlInicio() {
    RepositorioAutenticacion.cerrarSesion()
    navigate(Rutas.INICIO_SESION) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
