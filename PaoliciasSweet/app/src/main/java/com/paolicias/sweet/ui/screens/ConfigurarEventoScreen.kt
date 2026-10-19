package com.paolicias.sweet.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paolicias.sweet.navigation.Rutas
import com.paolicias.sweet.ui.components.CampoValidado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarEventoScreen(
    onNavigateNext: () -> Unit = {}
) {
    var fechaEvento by remember { mutableStateOf("15/11/2026") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var bloqueHorario by remember { mutableStateOf("10:00 - 14:00 hrs (Mañana)") }
    var asistentes by remember { mutableStateOf("50") }
    var observaciones by remember { mutableStateOf("2 menús aptos para celíacos y 3 vegetarianos") }

    var errorFecha by remember { mutableStateOf<String?>(null) }
    var errorHorario by remember { mutableStateOf<String?>(null) }
    var errorAsistentes by remember { mutableStateOf<String?>(null) }
    var errorObservaciones by remember { mutableStateOf<String?>(null) }

    val tieneErrores = errorFecha != null || errorHorario != null || errorAsistentes != null || errorObservaciones != null
    val colorBotonCotizar by animateColorAsState(
        targetValue = if (tieneErrores) Color(0xFFC62828) else MaterialTheme.colorScheme.primary,
        label = "colorBotonCotizar"
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            fechaEvento = formatter.format(Date(millis))
                            errorFecha = null
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Configurar Evento",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Completa los parámetros de tu evento y restricciones alimentarias",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            CampoValidado(
                valor = fechaEvento,
                onCambio = {
                    fechaEvento = it
                    errorFecha = if (it.isBlank()) "Selecciona o ingresa una fecha válida" else null
                },
                etiqueta = "Fecha del Evento (DatePicker)",
                error = errorFecha,
                icono = Icons.Default.CalendarMonth,
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Abrir calendario"
                        )
                    }
                }
            )

            Button(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Seleccionar Fecha en Calendario")
            }

            CampoValidado(
                valor = bloqueHorario,
                onCambio = {
                    bloqueHorario = it
                    errorHorario = if (it.isBlank()) "El bloque horario es obligatorio" else null
                },
                etiqueta = "Bloque Horario",
                error = errorHorario,
                icono = Icons.Default.AccessTime
            )

            CampoValidado(
                valor = asistentes,
                onCambio = {
                    asistentes = it
                    val num = it.toIntOrNull()
                    errorAsistentes = when {
                        num == null -> "Ingresa un número válido de asistentes"
                        num < 10 || num > 100 -> "La capacidad debe ser entre 10 y 100 personas"
                        else -> null
                    }
                },
                etiqueta = "Cantidad de Asistentes (10 a 100)",
                error = errorAsistentes,
                icono = Icons.Default.Groups,
                keyboardType = KeyboardType.Number
            )

            CampoValidado(
                valor = observaciones,
                onCambio = {
                    observaciones = it
                    errorObservaciones = if (it.isBlank()) "Indica alergias/restricciones o escribe 'Sin restricciones'" else null
                },
                etiqueta = "Observaciones y Restricciones Alimentarias",
                error = errorObservaciones,
                icono = Icons.Default.Description,
                singleLine = false
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Cotizado (Tarifa Escalonada):",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$645.000 CLP",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Abono 50% requerido para bloquear fecha: $322.500 CLP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val num = asistentes.toIntOrNull()
                    errorFecha = if (fechaEvento.isBlank()) "Selecciona una fecha" else null
                    errorHorario = if (bloqueHorario.isBlank()) "Ingresa el bloque horario" else null
                    errorAsistentes = if (num == null || num < 10 || num > 100) "Capacidad permitida: 10 a 100 asistentes" else null
                    errorObservaciones = if (observaciones.isBlank()) "Indica restricciones o 'Sin restricciones'" else null

                    if (errorFecha == null && errorHorario == null && errorAsistentes == null && errorObservaciones == null) {
                        onNavigateNext()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonCotizar),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (tieneErrores) "Corrige los campos en rojo" else "Ver Resumen de Cotización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ConfigurarEventoScreen(navController: NavController) {
    ConfigurarEventoScreen(onNavigateNext = { navController.navigate(Rutas.RESUMEN) })
}
