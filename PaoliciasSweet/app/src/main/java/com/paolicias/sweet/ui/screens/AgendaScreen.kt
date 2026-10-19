package com.paolicias.sweet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paolicias.sweet.data.local.Evento
import com.paolicias.sweet.data.repository.EstadoAbono
import com.paolicias.sweet.ui.components.ComprobanteMiniatura
import com.paolicias.sweet.ui.components.EstadoChip
import com.paolicias.sweet.ui.components.fechaLegible
import com.paolicias.sweet.util.Estimador
import com.paolicias.sweet.util.Formato
import com.paolicias.sweet.viewmodel.ReservasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    onCerrarSesion: () -> Unit,
    viewModel: ReservasViewModel = viewModel()
) {
    val eventos by viewModel.agenda.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumirMensaje()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Agenda de eventos", fontWeight = FontWeight.Bold)
                        Text("${eventos.size} reservas", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (eventos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.EventAvailable,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("No hay eventos agendados", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventos, key = { it.id }) { evento ->
                    EventoAgendaCard(
                        evento = evento,
                        onValidar = { viewModel.validarAbono(evento.id) },
                        onPagado = { viewModel.marcarPagado(evento.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun EventoAgendaCard(
    evento: Evento,
    onValidar: () -> Unit,
    onPagado: () -> Unit,
    modifier: Modifier = Modifier
) {
    var verEstimacion by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    fechaLegible(evento.fecha),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(evento.estadoAbono)
            }
            Text(
                "Pedido #${evento.id} · Cliente #${evento.clienteId}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(evento.bloqueHorario, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${evento.asistentes} personas · Total ${Formato.clp(evento.total)}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (evento.observaciones.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "Observaciones o alergias",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        evento.observaciones,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (evento.comprobanteUri.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComprobanteMiniatura(evento.comprobanteUri)
                    Spacer(Modifier.width(12.dp))
                    Text("Comprobante recibido", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Text(
                    "Sin comprobante aún",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when (evento.estadoAbono) {
                EstadoAbono.PENDIENTE -> Button(
                    onClick = onValidar,
                    enabled = evento.comprobanteUri.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Validar abono") }
                EstadoAbono.ABONO_50 -> OutlinedButton(
                    onClick = onPagado,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Marcar como pagado") }
            }

            TextButton(onClick = { verEstimacion = !verEstimacion }) {
                Text(if (verEstimacion) "Ocultar estimación" else "Ver estimación de insumos y personal")
            }
            AnimatedVisibility(visible = verEstimacion) {
                val estimacion = Estimador.estimar(evento.asistentes)
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Personal", fontWeight = FontWeight.Bold)
                        Text("• ${estimacion.garzones} garzones")
                        Text("• ${estimacion.reposteros} repostero(s)")
                        Spacer(Modifier.height(4.dp))
                        Text("Insumos", fontWeight = FontWeight.Bold)
                        Text("• ${estimacion.porcionesTorta} porciones de torta")
                        Text("• ${estimacion.bocadillos} bocadillos")
                        Text("• ${estimacion.litrosBebida} litros de bebida")
                    }
                }
            }
        }
    }
}
