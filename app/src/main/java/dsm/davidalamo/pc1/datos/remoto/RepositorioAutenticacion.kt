package dsm.davidalamo.pc1.datos.remoto

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object RepositorioAutenticacion {
    private val autenticacion get() = FirebaseAuth.getInstance()

    suspend fun iniciarSesion(correo: String, contrasena: String): String {
        val resultado = autenticacion.signInWithEmailAndPassword(correo.trim(), contrasena).await()
        return requireNotNull(resultado.user) { "Firebase no devolvió el usuario autenticado." }.uid
    }

    suspend fun registrarCuenta(correo: String, contrasena: String): String {
        val resultado = autenticacion.createUserWithEmailAndPassword(correo.trim(), contrasena).await()
        return requireNotNull(resultado.user) { "Firebase no devolvió el usuario registrado." }.uid
    }

    fun cerrarSesion() = autenticacion.signOut()
}
