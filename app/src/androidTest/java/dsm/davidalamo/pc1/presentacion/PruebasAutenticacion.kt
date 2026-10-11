package dsm.davidalamo.pc1.presentacion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import dsm.davidalamo.pc1.presentacion.acceso.VistaInicioSesion
import dsm.davidalamo.pc1.ui.tema.TemaBolilla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PruebasAutenticacion {
    @get:Rule
    val reglaComposicion = createComposeRule()

    @Test
    fun entregaElUidRetornadoPorAuthConLasCredencialesIngresadas() {
        var uidRecibido: String? = null
        var correoRecibido: String? = null
        reglaComposicion.setContent {
            TemaBolilla {
                VistaInicioSesion(
                    alIngresar = { uidRecibido = it }, alCrearCuenta = {},
                    iniciarSesion = { correo, _ ->
                        correoRecibido = correo
                        "uidRetornadoPorAuth"
                    }
                )
            }
        }
        reglaComposicion.onNodeWithText("Iniciar sesión").performScrollTo().assertIsNotEnabled()
        reglaComposicion.onNodeWithText("Correo electrónico").performTextInput("usuario@example.com")
        reglaComposicion.onNodeWithText("Contraseña").performTextInput("clavePrueba")
        reglaComposicion.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        reglaComposicion.runOnIdle {
            assertEquals("uidRetornadoPorAuth", uidRecibido)
            assertEquals("usuario@example.com", correoRecibido)
        }
    }

    @Test
    fun autenticacionRechazadaNoPermiteNavegar() {
        var uidRecibido: String? = null
        reglaComposicion.setContent {
            TemaBolilla {
                VistaInicioSesion(
                    alIngresar = { uidRecibido = it }, alCrearCuenta = {},
                    iniciarSesion = { _, _ -> error("Credenciales inválidas") }
                )
            }
        }
        reglaComposicion.onNodeWithText("Correo electrónico").performTextInput("usuario@example.com")
        reglaComposicion.onNodeWithText("Contraseña").performTextInput("clavePrueba")
        reglaComposicion.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        reglaComposicion.onNodeWithText("Credenciales inválidas").performScrollTo().assertIsDisplayed()
        reglaComposicion.runOnIdle { assertNull(uidRecibido) }
    }
}
