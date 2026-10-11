package dsm.davidalamo.pc1.datos.remoto

import com.google.firebase.firestore.DocumentSnapshot
import dsm.davidalamo.pc1.datos.modelos.Bolilla
import dsm.davidalamo.pc1.datos.modelos.EstadoSorteo
import dsm.davidalamo.pc1.datos.modelos.Jugada
import dsm.davidalamo.pc1.datos.modelos.Sorteo

internal fun DocumentSnapshot.leerSorteo(): Sorteo {
    check(exists()) { "Crea la cabecera sorteos/$id en Firebase." }
    val identificador = getString("identificador")
    require(identificador == id) { "El identificador de la cabecera debe coincidir con el documento." }
    val nombre = getString("nombre")
    require(!nombre.isNullOrBlank()) { "La cabecera requiere un nombre." }
    val fecha = getTimestamp("fecha") ?: error("La cabecera requiere fecha de tipo timestamp.")
    val estado = EstadoSorteo.entries.firstOrNull { it.valorFirestore == getString("estado") }
        ?: error("El estado debe ser abierto, en_curso o finalizado.")
    return Sorteo(identificador, nombre, fecha.toDate(), estado)
}

internal fun DocumentSnapshot.leerJugada(identificadorSorteo: String, uidEsperado: String): Jugada {
    check(getString("identificadorSorteo") == identificadorSorteo && getString("uidUsuario") == uidEsperado) {
        "La jugada almacenada no corresponde al sorteo o al usuario."
    }
    val valores = get("numeros") as? List<*> ?: error("La jugada requiere un arreglo numérico.")
    val numeros = valores.map { valor ->
        require(valor is Long && valor in 1L..45L) { "La jugada contiene números inválidos." }
        valor.toInt()
    }
    return Jugada(identificadorSorteo, uidEsperado, numeros)
}

internal fun DocumentSnapshot.leerBolilla(): Bolilla {
    val numero = get("numero")
    val orden = get("ordenSalida")
    require(numero is Long && numero in 1L..45L) {
        "Bolilla $id: numero debe ser un entero del 1 al 45."
    }
    require(orden is Long && orden in 1L..6L) {
        "Bolilla $id: ordenSalida debe ser un entero del 1 al 6."
    }
    return Bolilla(numero.toInt(), orden.toInt())
}
