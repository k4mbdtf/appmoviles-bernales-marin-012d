package com.paolicias.sweet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paolicias.sweet.data.local.Usuario
import com.paolicias.sweet.data.repository.Repository
import com.paolicias.sweet.util.Sesion
import com.paolicias.sweet.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    /** No nulo cuando el login fue exitoso; la UI navega segun usuario.rol. */
    val usuario: Usuario? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.obtener(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(valor: String) = _uiState.update {
        it.copy(email = valor, emailError = Validadores.validarEmail(valor), errorGeneral = null)
    }

    fun onPasswordChange(valor: String) = _uiState.update {
        it.copy(password = valor, passwordError = Validadores.validarPassword(valor), errorGeneral = null)
    }

    fun iniciarSesion() {
        val actual = _uiState.value
        if (actual.cargando) return

        val errorEmail = Validadores.validarEmail(actual.email)
        val errorPassword = Validadores.validarPassword(actual.password)
        if (errorEmail != null || errorPassword != null) {
            _uiState.update { it.copy(emailError = errorEmail, passwordError = errorPassword) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            val usuario = repository.iniciarSesion(actual.email, actual.password)
            if (usuario == null) {
                _uiState.update {
                    it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos")
                }
            } else {
                Sesion.iniciar(usuario)
                _uiState.update { it.copy(cargando = false, usuario = usuario) }
            }
        }
    }

    /** Llamar despues de navegar, para no repetir la navegacion al volver atras. */
    fun consumirLogin() = _uiState.update { it.copy(usuario = null) }
}
