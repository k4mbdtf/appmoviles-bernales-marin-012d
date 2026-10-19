package com.paolicias.sweet.util

data class EstimacionEvento(
    val garzones: Int,
    val reposteros: Int,
    val porcionesTorta: Int,
    val bocadillos: Int,
    val litrosBebida: Int
)

/** Estimacion referencial de insumos y personal segun la cantidad de asistentes. */
object Estimador {
    fun estimar(asistentes: Int): EstimacionEvento = EstimacionEvento(
        garzones = (asistentes + 14) / 15,                 // 1 garzon cada 15 personas
        reposteros = if (asistentes <= 40) 1 else 2,
        porcionesTorta = asistentes + (asistentes + 9) / 10, // 10% extra
        bocadillos = asistentes * 6,                       // 6 por persona
        litrosBebida = (asistentes * 4 + 9) / 10           // 0,4 L por persona
    )
}
