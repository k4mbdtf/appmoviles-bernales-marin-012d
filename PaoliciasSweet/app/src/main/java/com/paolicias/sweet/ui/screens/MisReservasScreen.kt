package com.paolicias.sweet.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paolicias.sweet.data.local.Evento
import com.paolicias.sweet.data.repository.EstadoAbono
import com.paolicias.sweet.ui.components.ComprobanteMiniatura
import com.paolicias.sweet.ui.components.EstadoChip
import com.paolicias.sweet.ui.components.fechaLegible
import com.paolicias.sweet.util.Formato
import com.paolicias.sweet.viewmodel.ReservasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    onVolver: () -> Unit,
    viewModel: ReservasViewModel = viewModel()
) {
    val context = LocalContext.current
    val reservas by viewModel.misReservas.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var eventoSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumirMensaje()
        }
    }

    // Camara: devuelve un Bitmap de vista previa
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { foto ->
        val id = eventoSeleccionado
        if (foto != null && id != null) viewModel.guardarFotoCamara(id, foto)
    }

    // Permiso de camara en tiempo de ejecucion
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            camaraLauncher.launch(null)
        } else {
            viewModel.mostrarMensaje("Sin permiso de cámara no puedes fotografiar el comprobante. Usa la galería.")
        }
    }

    // Galeria: selector de fotos del sistema, no requiere permiso
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val id = eventoSeleccionado
        if (uri != null && id != null) viewModel.guardarDesdeGaleria(id, uri)
    }

    fun abrirCamara(eventoId: Int) {
        eventoSeleccionado = eventoId
        val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (tienePermiso) camaraLauncher.launch(null)
        else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    fun abrirGaleria(eventoId: Int) {
        eventoSeleccionado = eventoId
        galeriaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mis reservas", fontWeight = FontWeight.Bold) },
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
        if (reservas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.EventBusy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("Aún no tienes reservas", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservas, key = { it.id }) { evento ->
                    ReservaCard(
                        evento = evento,
                        onCamara = { abrirCamara(evento.id) },
                        onGaleria = { abrirGaleria(evento.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservaCard(
    evento: Evento,
    onCamara: () -> Unit,
    onGaleria: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(evento.bloqueHorario, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${evento.asistentes} personas · Total ${Formato.clp(evento.total)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Abono 50%: ${Formato.clp(evento.total / 2)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (evento.observaciones.isNotBlank()) {
                Text("Obs: ${evento.observaciones}", style = MaterialTheme.typography.bodySmall)
            }

            if (evento.comprobanteUri.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComprobanteMiniatura(evento.comprobanteUri)
                    Spacer(Modifier.width(12.dp))
                    Text("Comprobante adjunto", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (evento.estadoAbono == EstadoAbono.PENDIENTE) {
                Text(
                    if (evento.comprobanteUri.isBlank()) "Adjunta el comprobante del abono:"
                    else "¿Quieres reemplazarlo?",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCamara, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Cámara")
                    }
                    OutlinedButton(onClick = onGaleria, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Galería")
                    }
                }
            }
        }
    }
}
