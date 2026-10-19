package com.paolicias.sweet.util

import java.time.LocalDate

/**
 * Validaciones centralizadas y desacopladas de la UI.
 * Cada función retorna el mensaje de error, o null si el valor es válido.
 */
object Validadores {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    const val ASISTENTES_MIN = 10
    const val ASISTENTES_MAX = 100
    const val ASISTENTES_PASO = 10
    const val PASSWORD_MIN = 6

    fun validarEmail(email: String): String? = when {
        email.isBlank() -> "El correo es obligatorio"
        !EMAIL_REGEX.matches(email.trim()) -> "Ingresa un correo válido (ej: nombre@dominio.cl)"
        else -> null
    }

    fun validarPassword(password: String): String? = when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < PASSWORD_MIN -> "Mínimo $PASSWORD_MIN caracteres"
        else -> null
    }

    fun validarTelefono(telefono: String): String? {
        val limpio = telefono.filter { !it.isWhitespace() }
        return when {
            limpio.isBlank() -> "El teléfono es obligatorio"
            !limpio.all { it.isDigit() } -> "Solo se permiten números"
            limpio.length != 9 -> "Debe tener 9 dígitos"
            !limpio.startsWith("9") -> "Un celular chileno comienza con 9"
            else -> null
        }
    }

    fun validarAsistentes(asistentes: Int?): String? = when {
        asistentes == null -> "Indica la cantidad de asistentes"
        asistentes < ASISTENTES_MIN || asistentes > ASISTENTES_MAX ->
            "Debe estar entre $ASISTENTES_MIN y $ASISTENTES_MAX"
        asistentes % ASISTENTES_PASO != 0 -> "Debe ser múltiplo de $ASISTENTES_PASO"
        else -> null
    }

    fun validarAsistentesTexto(texto: String): String? =
        validarAsistentes(texto.trim().toIntOrNull())

    fun validarFecha(fecha: LocalDate?): String? = when {
        fecha == null -> "Selecciona la fecha del evento"
        !fecha.isAfter(LocalDate.now()) -> "La fecha debe ser posterior a hoy"
        else -> null
    }

    fun validarHorario(horario: String): String? =
        if (horario.isBlank()) "Selecciona un horario" else null
}
