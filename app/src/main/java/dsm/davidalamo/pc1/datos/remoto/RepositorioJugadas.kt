package dsm.davidalamo.pc1.datos.remoto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.Sorteo
import kotlinx.coroutines.tasks.await

data class DatosParticipacion(val sorteo: Sorteo, val uidUsuario: String, val jugada: Jugada?)

class RepositorioJugadas {
    companion object {
        const val IDENTIFICADOR_SORTEO = "actual"
    }

    private val autenticacion = FirebaseAuth.getInstance()
    private val baseDatos = FirebaseFirestore.getInstance()
    private val documentoSorteo = baseDatos.collection("sorteos").document(IDENTIFICADOR_SORTEO)

    suspend fun cargarParticipacion(): DatosParticipacion {
        // Conserva la sesion que provea el modulo de autenticacion.
        val usuario = autenticacion.currentUser
            ?: autenticacion.signInAnonymously().await().user
            ?: error("No se pudo iniciar la sesión anónima.")
        val sorteo = documentoSorteo.get(Source.SERVER).await().leerSorteo()
        val documentoJugada = documentoSorteo.collection("jugadas").document(usuario.uid)
            .get(Source.SERVER).await()
        check(autenticacion.currentUser?.uid == usuario.uid) {
            "La sesión cambió durante la carga. Reintenta la conexión."
        }
        return DatosParticipacion(
            sorteo, usuario.uid,
            if (documentoJugada.exists()) documentoJugada.leerJugada(IDENTIFICADOR_SORTEO, usuario.uid) else null
        )
    }

    suspend fun registrarJugada(jugada: Jugada) {
        check(autenticacion.currentUser?.uid == jugada.uidUsuario) {
            "La sesión cambió. Vuelve a cargar la jugada."
        }
        require(jugada.identificadorSorteo == IDENTIFICADOR_SORTEO) { "El sorteo no corresponde." }
        val documentoJugada = documentoSorteo.collection("jugadas").document(jugada.uidUsuario)
        // La transaccion impide registros tardios o duplicados con una vista desactualizada.
        baseDatos.runTransaction { transaccion ->
            val sorteo = transaccion.get(documentoSorteo).leerSorteo()
            val existente = transaccion.get(documentoJugada)
            check(sorteo.estado == EstadoSorteo.ABIERTO) { "El sorteo ya no está abierto." }
            check(!existente.exists()) { "Ya existe una jugada registrada para este usuario y sorteo." }
            transaccion.set(documentoJugada, mapOf(
                "identificadorSorteo" to jugada.identificadorSorteo,
                "uidUsuario" to jugada.uidUsuario,
                "numeros" to jugada.numeros,
                "fechaRegistro" to FieldValue.serverTimestamp()
            ))
        }.await()
    }

}
