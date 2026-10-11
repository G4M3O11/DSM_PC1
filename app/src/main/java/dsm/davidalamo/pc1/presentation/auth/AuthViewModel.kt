package dsm.davidalamo.pc1.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dsm.davidalamo.pc1.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el estado de carga, autenticación y errores para la autenticación.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkUserSession()
    }

    /**
     * Recupera el estado de la sesión al abrir/reabrir la aplicación.
     */
    fun checkUserSession() {
        val email = repository.getCurrentUserEmail()
        if (email != null) {
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentUserEmail = email,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    currentUserEmail = null
                )
            }
        }
    }

    /**
     * Valida e inicia sesión con correo y contraseña.
     */
    fun login(email: String, password: String, onLoginSuccess: () -> Unit = {}) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos.") }
            return
        }

        if (!isValidEmail(trimmedEmail)) {
            _uiState.update { it.copy(errorMessage = "El formato del correo electrónico no es válido.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.login(trimmedEmail, password)
            if (result.isSuccess) {
                val userEmail = result.getOrNull() ?: trimmedEmail
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUserEmail = userEmail,
                        errorMessage = null,
                        successMessage = "Inicio de sesión exitoso"
                    )
                }
                onLoginSuccess()
            } else {
                val exception = result.exceptionOrNull()
                val friendlyMessage = parseAuthError(exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
            }
        }
    }

    /**
     * Valida y registra un nuevo usuario con correo y contraseña.
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String = password,
        onRegisterSuccess: () -> Unit = {}
    ) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos.") }
            return
        }

        if (!isValidEmail(trimmedEmail)) {
            _uiState.update { it.copy(errorMessage = "El formato del correo electrónico no es válido.") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.register(trimmedEmail, password)
            if (result.isSuccess) {
                val userEmail = result.getOrNull() ?: trimmedEmail
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUserEmail = userEmail,
                        errorMessage = null,
                        successMessage = "Registro exitoso"
                    )
                }
                onRegisterSuccess()
            } else {
                val exception = result.exceptionOrNull()
                val friendlyMessage = parseAuthError(exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
            }
        }
    }

    /**
     * Cierra la sesión activa.
     */
    fun logout() {
        repository.logout()
        _uiState.update {
            AuthUiState(
                isAuthenticated = false,
                currentUserEmail = null,
                errorMessage = null
            )
        }
    }

    /**
     * Limpia el mensaje de error de la pantalla.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun parseAuthError(exception: Throwable?): String {
        if (exception == null) return "Ocurrió un error inesperado."

        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas. Verifica tu correo y contraseña."
            is FirebaseAuthUserCollisionException -> "El correo electrónico ya se encuentra registrado."
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Debe tener al menos 6 caracteres."
            else -> {
                val msg = exception.message ?: ""
                when {
                    msg.contains("The email address is badly formatted", ignoreCase = true) ->
                        "El formato del correo electrónico es incorrecto."
                    msg.contains("There is no user record corresponding to this identifier", ignoreCase = true) ||
                    msg.contains("user-not-found", ignoreCase = true) ->
                        "Usuario no encontrado. Revisa tu correo o regístrate."
                    msg.contains("wrong-password", ignoreCase = true) ||
                    msg.contains("password is invalid", ignoreCase = true) ->
                        "Contraseña incorrecta."
                    msg.contains("network", ignoreCase = true) ->
                        "Error de red. Por favor, verifica tu conexión a internet."
                    else -> exception.localizedMessage ?: "Error de autenticación. Inténtalo de nuevo."
                }
            }
        }
    }
}
