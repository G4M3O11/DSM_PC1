package dsm.davidalamo.pc1.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de gestionar la autenticación de usuarios mediante Firebase Authentication.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /**
     * Obtiene el usuario de Firebase actualmente autenticado.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Obtiene el correo del usuario actualmente autenticado (si existe sesión activa).
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Indica si hay un usuario autenticado en la aplicación.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Inicia sesión utilizando correo electrónico y contraseña.
     */
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val userEmail = result.user?.email ?: email
            Result.success(userEmail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario utilizando correo electrónico y contraseña.
     */
    suspend fun register(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val userEmail = result.user?.email ?: email
            Result.success(userEmail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión del usuario actual en Firebase Authentication.
     */
    fun logout() {
        auth.signOut()
    }
}
