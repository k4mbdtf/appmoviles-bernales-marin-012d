package com.paolicias.sweet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paolicias.sweet.ui.components.VerdeValido
import com.paolicias.sweet.util.Formato
import com.paolicias.sweet.viewmodel.EventoViewModel

@Composable
fun ResumenScreen(
    viewModel: EventoViewModel,
    onVerMisReservas: () -> Unit,
    onVolverCatalogo: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var mostrarIcono by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarIcono = true }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            AnimatedVisibility(visible = mostrarIcono, enter = scaleIn() + fadeIn()) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = VerdeValido,
                    modifier = Modifier.size(96.dp)
                )
            }
            Text(
                "¡Reserva registrada!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DatoFila("Fecha", state.fecha?.let { Formato.fecha(it) } ?: "-")
                    DatoFila("Horario", state.horario.ifBlank { "-" })
                    DatoFila("Asistentes", "${state.asistentes} personas")
                    DatoFila("Tarifa por persona", Formato.clp(state.tarifaPorPersona))
                    if (state.observaciones.isNotBlank()) {
                        DatoFila("Observaciones", state.observaciones)
                    }
                    HorizontalDivider()
                    DatoFila("Total", Formato.clp(state.total), destacado = true)
                    DatoFila("Abono 50%", Formato.clp(state.abono), destacado = true)
                    DatoFila("Estado", "Pendiente de abono")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Para confirmar la fecha, adjunta el comprobante del abono del 50% desde Mis reservas.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }

            Button(onClick = onVerMisReservas, modifier = Modifier.fillMaxWidth()) {
                Text("Ver mis reservas")
            }
            OutlinedButton(onClick = onVolverCatalogo, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al catálogo")
            }
        }
    }
}

@Composable
private fun DatoFila(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            valor,
            style = if (destacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
