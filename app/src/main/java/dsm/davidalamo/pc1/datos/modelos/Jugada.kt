package dsm.davidalamo.pc1.datos.modelos

object ReglasJugada {
    const val CANTIDAD_NUMEROS = 6
    const val NUMERO_MINIMO = 1
    const val NUMERO_MAXIMO = 45

    fun esSeleccionValida(numeros: List<Int>): Boolean =
        numeros.size <= CANTIDAD_NUMEROS &&
            numeros.distinct().size == numeros.size &&
            numeros.all { it in NUMERO_MINIMO..NUMERO_MAXIMO }

    fun esJugadaValida(numeros: List<Int>): Boolean =
        numeros.size == CANTIDAD_NUMEROS && esSeleccionValida(numeros)
}

data class Jugada(val identificadorSorteo: String, val uidUsuario: String, val numeros: List<Int>) {
    init {
        require(identificadorSorteo.isNotBlank()) { "Falta el identificador del sorteo." }
        require(uidUsuario.isNotBlank()) { "Falta el UID del participante." }
        require(ReglasJugada.esJugadaValida(numeros)) { "Elige seis números distintos del 1 al 45." }
    }
}

enum class EstadoSorteo(val valorFirestore: String) {
    ABIERTO("abierto"), EN_CURSO("en_curso"), FINALIZADO("finalizado")
}

data class Sorteo(
    val identificador: String,
    val nombre: String,
    val fecha: java.util.Date,
    val estado: EstadoSorteo
)
