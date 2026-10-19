package com.paolicias.sweet.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paolicias.sweet.ui.components.CampoSelector
import com.paolicias.sweet.ui.components.CampoValidado
import com.paolicias.sweet.ui.components.MensajeError
import com.paolicias.sweet.util.Formato
import com.paolicias.sweet.util.Validadores
import com.paolicias.sweet.viewmodel.EventoViewModel
import java.time.ZoneOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarEventoScreen(
    viewModel: EventoViewModel,
    onVolver: () -> Unit,
    onReservaGuardada: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.eventoGuardadoId) {
        if (state.eventoGuardadoId != null) {
            onReservaGuardada()
            viewModel.consumirGuardado()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Configura tu evento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha
            CampoSelector(
                etiqueta = "Fecha del evento",
                valor = state.fecha?.let { Formato.fecha(it) } ?: "",
                icono = Icons.Filled.Event,
                error = state.fechaError,
                onClick = { mostrarCalendario = true }
            )

            // Horario
            Column {
                TituloSeccion("Bloque horario", Icons.Filled.Schedule)
                EventoViewModel.BLOQUES_HORARIO.forEach { bloque ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = state.horario == bloque,
                                onClick = { viewModel.onHorarioChange(bloque) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = state.horario == bloque, onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Text(bloque, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                MensajeError(error = state.horarioError)
            }

            // Asistentes
            Column {
                TituloSeccion("Asistentes: ${state.asistentes} personas", Icons.Filled.Groups)
                Slider(
                    value = state.asistentes.toFloat(),
                    onValueChange = {
                        val nuevo = it.roundToInt()
                        if (nuevo != state.asistentes) viewModel.onAsistentesChange(nuevo)
                    },
                    valueRange = Validadores.ASISTENTES_MIN.toFloat()..Validadores.ASISTENTES_MAX.toFloat(),
                    steps = 8
                )
                Row(Modifier.fillMaxWidth()) {
                    Text("${Validadores.ASISTENTES_MIN}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text("${Validadores.ASISTENTES_MAX}", style = MaterialTheme.typography.bodySmall)
                }
                MensajeError(error = state.asistentesError)
            }

            // Observaciones
            CampoValidado(
                valor = state.observaciones,
                onValorChange = viewModel::onObservacionesChange,
                etiqueta = "Observaciones o alergias (opcional)",
                icono = Icons.Filled.EditNote,
                error = null,
                singleLine = false,
                minLines = 3,
                mostrarValido = false
            )

            // Cotizacion
            TarjetaCotizacion(
                asistentes = state.asistentes,
                tarifaPorPersona = state.tarifaPorPersona,
                total = state.total,
                abono = state.abono
            )

            AnimatedVisibility(visible = state.errorGeneral != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        state.errorGeneral ?: "",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Button(
                onClick = viewModel::guardarReserva,
                enabled = !state.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                AnimatedContent(targetState = state.guardando, label = "botonReserva") { guardando ->
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Confirmar reserva")
                    }
                }
            }
        }
    }

    if (mostrarCalendario) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fecha?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onFechaSeleccionada(datePickerState.selectedDateMillis)
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun TituloSeccion(texto: String, icono: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

/** Cotizacion en tiempo real; el total y el abono se animan al cambiar. */
@Composable
private fun TarjetaCotizacion(asistentes: Int, tarifaPorPersona: Int, total: Int, abono: Int) {
    val totalAnimado by animateIntAsState(targetValue = total, animationSpec = tween(500), label = "total")
    val abonoAnimado by animateIntAsState(targetValue = abono, animationSpec = tween(500), label = "abono")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Cotización", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "$asistentes personas × ${Formato.clp(tarifaPorPersona)} por persona",
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(
                    Formato.clp(totalAnimado),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Abono 50% para reservar", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(Formato.clp(abonoAnimado), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
