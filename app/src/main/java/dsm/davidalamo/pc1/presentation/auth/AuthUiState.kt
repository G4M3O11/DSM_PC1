package dsm.davidalamo.pc1.presentation.auth

/**
 * Estado de la interfaz de usuario para los flujos de autenticación.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUserEmail: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
