package com.paolicias.sweet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paolicias.sweet.data.local.Producto
import com.paolicias.sweet.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val productos: List<Producto> = emptyList(),
    val categorias: List<String> = emptyList(),
    val categoriaSeleccionada: String? = null,
    val cargando: Boolean = true
)

class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.obtener(application)
    private val categoriaSeleccionada = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CatalogoUiState> =
        combine(repository.productos, categoriaSeleccionada) { productos, seleccion ->
            CatalogoUiState(
                productos = if (seleccion == null) productos else productos.filter { it.categoria == seleccion },
                categorias = productos.map { it.categoria }.distinct().sorted(),
                categoriaSeleccionada = seleccion,
                cargando = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogoUiState())

    init {
        viewModelScope.launch { repository.precargarSiVacio() }
    }

    /** Tocar la categoria activa la deselecciona y vuelve a mostrar todo. */
    fun seleccionarCategoria(categoria: String?) {
        categoriaSeleccionada.value = if (categoriaSeleccionada.value == categoria) null else categoria
    }
}
