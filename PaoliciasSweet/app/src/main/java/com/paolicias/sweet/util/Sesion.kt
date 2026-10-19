package com.paolicias.sweet.util

import com.paolicias.sweet.data.local.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sesion en memoria del usuario autenticado. Permite a los ViewModels y a la
 * navegacion saber quien esta logueado y con que rol (CLIENTE o ADMIN).
 */
object Sesion {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }
}
