package com.paolicias.sweet.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formatos de presentacion para la UI (pesos chilenos y fechas). */
object Formato {
    private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /** 24000 -> "$24.000" */
    fun clp(valor: Int): String =
        "$" + String.format(Locale.US, "%,d", valor).replace(',', '.')

    fun fecha(fecha: LocalDate): String = fecha.format(FORMATO_FECHA)
}
