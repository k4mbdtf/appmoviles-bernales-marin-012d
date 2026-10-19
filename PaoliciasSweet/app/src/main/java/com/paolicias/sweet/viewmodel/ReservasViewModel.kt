package com.paolicias.sweet.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paolicias.sweet.data.local.Evento
import com.paolicias.sweet.data.repository.EstadoAbono
import com.paolicias.sweet.data.repository.Repository
import com.paolicias.sweet.util.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Reservas del cliente (con adjunto de comprobante) y agenda de la administradora.
 * Los comprobantes se copian a almacenamiento interno de la app para que no se pierdan.
 */
class ReservasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.obtener(application)
    private val clienteId: Int? = Sesion.usuario.value?.id

    val misReservas: StateFlow<List<Evento>> =
        (if (clienteId != null) repository.eventosDeCliente(clienteId) else flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val agenda: StateFlow<List<Evento>> =
        repository.todosLosEventos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun mostrarMensaje(texto: String) {
        _mensaje.value = texto
    }

    fun consumirMensaje() {
        _mensaje.value = null
    }

    /** Foto tomada con la camara. */
    fun guardarFotoCamara(eventoId: Int, foto: Bitmap) {
        viewModelScope.launch {
            try {
                val ruta = withContext(Dispatchers.IO) {
                    val archivo = nuevoArchivo(eventoId)
                    FileOutputStream(archivo).use { salida ->
                        foto.compress(Bitmap.CompressFormat.JPEG, 90, salida)
                    }
                    archivo.absolutePath
                }
                repository.adjuntarComprobante(eventoId, ruta)
                _mensaje.value = "Comprobante adjuntado"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la foto"
            }
        }
    }

    /** Imagen elegida desde la galeria. */
    fun guardarDesdeGaleria(eventoId: Int, uri: Uri) {
        viewModelScope.launch {
            try {
                val ruta = withContext(Dispatchers.IO) {
                    val archivo = nuevoArchivo(eventoId)
                    val entrada = getApplication<Application>().contentResolver.openInputStream(uri)
                        ?: error("No se pudo leer la imagen")
                    entrada.use { input ->
                        FileOutputStream(archivo).use { salida -> input.copyTo(salida) }
                    }
                    archivo.absolutePath
                }
                repository.adjuntarComprobante(eventoId, ruta)
                _mensaje.value = "Comprobante adjuntado"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo adjuntar la imagen"
            }
        }
    }

    fun validarAbono(eventoId: Int) {
        viewModelScope.launch {
            repository.actualizarEstadoAbono(eventoId, EstadoAbono.ABONO_50)
            _mensaje.value = "Abono validado"
        }
    }

    fun marcarPagado(eventoId: Int) {
        viewModelScope.launch {
            repository.actualizarEstadoAbono(eventoId, EstadoAbono.COMPLETADO)
            _mensaje.value = "Evento marcado como pagado"
        }
    }

    private fun nuevoArchivo(eventoId: Int): File {
        val carpeta = File(getApplication<Application>().filesDir, "comprobantes").apply { mkdirs() }
        return File(carpeta, "evento_${eventoId}_${System.currentTimeMillis()}.jpg")
    }
}
