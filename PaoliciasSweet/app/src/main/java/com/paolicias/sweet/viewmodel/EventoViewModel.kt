package com.paolicias.sweet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paolicias.sweet.data.repository.Repository
import com.paolicias.sweet.util.Sesion
import com.paolicias.sweet.util.Validadores
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

data class EventoUiState(
    val fecha: LocalDate? = null,
    val horario: String = "",
    val asistentes: Int = Validadores.ASISTENTES_MIN,
    val observaciones: String = "",
    val fechaError: String? = null,
    val horarioError: String? = null,
    val asistentesError: String? = null,
    val tarifaPorPersona: Int = 0,
    val total: Int = 0,
    val guardando: Boolean = false,
    val errorGeneral: String? = null,
    /** No nulo cuando la reserva se guardo; la UI navega al resumen. */
    val eventoGuardadoId: Long? = null
) {
    val abono: Int get() = total / 2
}

class EventoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.obtener(application)

    private val _uiState = MutableStateFlow(EventoUiState())
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    private var cotizacionJob: Job? = null

    init {
        cotizacionJob = viewModelScope.launch {
            repository.precargarSiVacio()
            calcularCotizacion()
        }
    }

    fun onFechaChange(fecha: LocalDate?) = _uiState.update {
        it.copy(fecha = fecha, fechaError = Validadores.validarFecha(fecha))
    }

    /** El DatePicker de Material 3 entrega milisegundos en UTC. */
    fun onFechaSeleccionada(millis: Long?) {
        val fecha = millis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
        onFechaChange(fecha)
    }

    fun onHorarioChange(horario: String) = _uiState.update {
        it.copy(horario = horario, horarioError = Validadores.validarHorario(horario))
    }

    fun onAsistentesChange(asistentes: Int) {
        _uiState.update {
            it.copy(asistentes = asistentes, asistentesError = Validadores.validarAsistentes(asistentes))
        }
        cotizacionJob?.cancel()
        cotizacionJob = viewModelScope.launch { calcularCotizacion() }
    }

    fun onObservacionesChange(texto: String) = _uiState.update { it.copy(observaciones = texto) }

    private suspend fun calcularCotizacion() {
        val cotizacion = repository.cotizar(_uiState.value.asistentes)
        _uiState.update {
            it.copy(
                tarifaPorPersona = cotizacion?.tarifaPorPersona ?: 0,
                total = cotizacion?.total ?: 0
            )
        }
    }

    fun guardarReserva() {
        val actual = _uiState.value
        if (actual.guardando) return

        val errorFecha = Validadores.validarFecha(actual.fecha)
        val errorHorario = Validadores.validarHorario(actual.horario)
        val errorAsistentes = Validadores.validarAsistentes(actual.asistentes)
        if (errorFecha != null || errorHorario != null || errorAsistentes != null) {
            _uiState.update {
                it.copy(fechaError = errorFecha, horarioError = errorHorario, asistentesError = errorAsistentes)
            }
            return
        }

        val cliente = Sesion.usuario.value
        if (cliente == null) {
            _uiState.update { it.copy(errorGeneral = "Debes iniciar sesión para reservar") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorGeneral = null) }
            val id = repository.registrarEvento(
                clienteId = cliente.id,
                fechaIso = actual.fecha.toString(),
                bloqueHorario = actual.horario,
                asistentes = actual.asistentes,
                observaciones = actual.observaciones
            )
            if (id == null) {
                _uiState.update {
                    it.copy(guardando = false, errorGeneral = "No hay tarifa disponible para esa cantidad")
                }
            } else {
                _uiState.update { it.copy(guardando = false, eventoGuardadoId = id) }
            }
        }
    }

    /** Llamar despues de navegar al resumen. */
    fun consumirGuardado() = _uiState.update { it.copy(eventoGuardadoId = null) }

    /** Deja el formulario limpio para una nueva reserva. */
    fun reiniciar() {
        _uiState.value = EventoUiState()
        cotizacionJob?.cancel()
        cotizacionJob = viewModelScope.launch { calcularCotizacion() }
    }

    companion object {
        val BLOQUES_HORARIO = listOf(
            "Mañana (09:00 - 13:00)",
            "Tarde (14:00 - 18:00)",
            "Noche (19:00 - 23:00)"
        )
    }
}
