package dsm.davidalamo.pc1.datos.remoto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dsm.davidalamo.pc1.datos.modelos.ResumenSorteo
import dsm.davidalamo.pc1.datos.modelos.validarBolillas
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface FuenteSeguimiento {
    fun observar(): Flow<Result<ResumenSorteo>>
}

class RepositorioSeguimiento : FuenteSeguimiento {
    private val autenticacion = FirebaseAuth.getInstance()
    private val documentoSorteo = FirebaseFirestore.getInstance().collection("sorteos")
        .document(RepositorioJugadas.IDENTIFICADOR_SORTEO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observar(): Flow<Result<ResumenSorteo>> =
        observarUsuario().distinctUntilChanged().flatMapLatest { uidUsuario ->
            if (uidUsuario == null) {
                flowOf(Result.failure(IllegalStateException("No hay una sesión autenticada. Vuelve a Mi jugada.")))
            } else {
                combine(
                    documentoSorteo.observarDocumento(),
                    documentoSorteo.collection("bolillas").observarColeccion(),
                    documentoSorteo.collection("jugadas").document(uidUsuario).observarDocumento()
                ) { cabecera, publicaciones, participacion ->
                    runCatching {
                        val documento = cabecera.getOrThrow()
                        val coleccion = publicaciones.getOrThrow()
                        val documentoJugada = participacion.getOrThrow()
                        val sorteo = documento.leerSorteo()
                        val bolillas = validarBolillas(coleccion.documents.map { it.leerBolilla() })
                        val jugada = if (documentoJugada.exists()) {
                            documentoJugada.leerJugada(sorteo.identificador, uidUsuario)
                        } else null
                        ResumenSorteo(
                            sorteo, bolillas, jugada,
                            datosConfirmados = !documento.metadata.isFromCache &&
                                !coleccion.metadata.isFromCache && !documentoJugada.metadata.isFromCache &&
                                !documento.metadata.hasPendingWrites() &&
                                !coleccion.metadata.hasPendingWrites() && !documentoJugada.metadata.hasPendingWrites()
                        )
                    }
                }
            }
        }

    private fun observarUsuario(): Flow<String?> = callbackFlow {
        val escucha = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        autenticacion.addAuthStateListener(escucha)
        awaitClose { autenticacion.removeAuthStateListener(escucha) }
    }
}

private fun DocumentReference.observarDocumento(): Flow<Result<DocumentSnapshot>> = callbackFlow {
    val escucha = addSnapshotListener(MetadataChanges.INCLUDE) { documento, excepcion ->
        trySend(
            if (excepcion != null) Result.failure(excepcion)
            else if (documento != null) Result.success(documento)
            else Result.failure(IllegalStateException("Firestore no devolvió el documento $path."))
        )
    }
    awaitClose { escucha.remove() }
}

private fun Query.observarColeccion(): Flow<Result<QuerySnapshot>> = callbackFlow {
    // Sin orderBy: un documento sin ordenSalida debe generar error, no quedar oculto.
    val escucha = addSnapshotListener(MetadataChanges.INCLUDE) { documentos, excepcion ->
        trySend(
            if (excepcion != null) Result.failure(excepcion)
            else if (documentos != null) Result.success(documentos)
            else Result.failure(IllegalStateException("Firestore no devolvió las bolillas."))
        )
    }
    awaitClose { escucha.remove() }
}
