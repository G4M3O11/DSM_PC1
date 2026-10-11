package dsm.davidalamo.pc1.datos.modelos

data class Bolilla(val numero: Int, val ordenSalida: Int) {
    init {
        require(numero in 1..45) { "El número de la bolilla debe estar entre 1 y 45." }
        require(ordenSalida in 1..6) { "El orden de salida debe estar entre 1 y 6." }
    }
}

fun validarBolillas(bolillas: List<Bolilla>): List<Bolilla> {
    require(bolillas.size <= 6) { "El sorteo no puede tener más de seis bolillas." }
    require(bolillas.map { it.numero }.distinct().size == bolillas.size) {
        "Las bolillas publicadas tienen números repetidos."
    }
    val ordenadas = bolillas.sortedBy { it.ordenSalida }
    require(ordenadas.map { it.ordenSalida } == (1..bolillas.size).toList()) {
        "Los órdenes de salida deben ser únicos y consecutivos desde 1."
    }
    return ordenadas
}

data class ResumenSorteo(
    val sorteo: Sorteo,
    val bolillas: List<Bolilla>,
    val jugada: Jugada?,
    val datosConfirmados: Boolean
) {
    init {
        require(bolillas == validarBolillas(bolillas)) { "Las bolillas deben estar ordenadas por salida." }
        require(jugada == null || jugada.identificadorSorteo == sorteo.identificador) {
            "La jugada no corresponde a este sorteo."
        }
    }

    val numerosAcertados: Set<Int>
        get() = jugada?.numeros?.toSet()?.intersect(bolillas.map { it.numero }.toSet()).orEmpty()
    val cantidadAciertos: Int get() = numerosAcertados.size
    val resultadoFinal: String?
        get() = if (datosConfirmados && sorteo.estado == EstadoSorteo.FINALIZADO &&
            bolillas.size == 6 && jugada != null
        ) {
            if (cantidadAciertos == 6) "GANADOR" else "SIGUE INTENTANDO"
        } else null

    val mensajeEstado: String
        get() = when (sorteo.estado) {
            EstadoSorteo.ABIERTO -> "Esperando el inicio del sorteo."
            EstadoSorteo.EN_CURSO -> "Sorteo en curso"
            EstadoSorteo.FINALIZADO -> when {
                bolillas.size < 6 -> "Esperando resultados: faltan bolillas por recibir."
                !datosConfirmados -> "Esperando resultados: falta confirmar los datos con Firestore."
                jugada == null -> "Sorteo finalizado. No tienes una jugada registrada."
                else -> "Sorteo finalizado"
            }
        }
}
