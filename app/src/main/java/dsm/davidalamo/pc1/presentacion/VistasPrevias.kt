package dsm.davidalamo.pc1.presentacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dsm.davidalamo.pc1.presentacion.acceso.VistaInicioSesion
import dsm.davidalamo.pc1.presentacion.acceso.VistaRegistroCuenta
import dsm.davidalamo.pc1.presentacion.jugada.VistaJugada
import dsm.davidalamo.pc1.presentacion.jugada.EstadoVistaJugada
import dsm.davidalamo.pc1.presentacion.sorteo.VistaSorteo
import dsm.davidalamo.pc1.presentacion.sorteo.EstadoVistaSorteo
import dsm.davidalamo.pc1.ui.tema.TemaBolilla

@Preview(name = "Inicio de sesion", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun VistaPreviaInicioSesion() {
    TemaBolilla { VistaInicioSesion({}, {}) }
}

@Preview(name = "Registro de cuenta", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun VistaPreviaRegistroCuenta() {
    TemaBolilla { VistaRegistroCuenta({}, {}) }
}

@Preview(name = "Mi jugada", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun VistaPreviaJugada() {
    TemaBolilla { VistaJugada(EstadoVistaJugada(cargando = false), {}, {}, {}, {}, {}) }
}

@Preview(name = "Sorteo", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun VistaPreviaSorteo() {
    TemaBolilla { VistaSorteo(EstadoVistaSorteo(), {}, {}, {}) }
}
