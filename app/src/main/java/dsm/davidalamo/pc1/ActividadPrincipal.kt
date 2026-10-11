package dsm.davidalamo.pc1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dsm.davidalamo.pc1.presentacion.navegacion.NavegacionAplicacion
import dsm.davidalamo.pc1.ui.tema.TemaBolilla

class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        enableEdgeToEdge()
        setContent {
            TemaBolilla {
                NavegacionAplicacion()
            }
        }
    }
}
