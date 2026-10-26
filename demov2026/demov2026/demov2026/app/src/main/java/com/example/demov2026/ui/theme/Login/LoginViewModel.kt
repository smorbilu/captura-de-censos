package com.example.demov2026.ui.theme.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.data.CredencialesIncorrectasException
import com.example.demov2026.data.SessionManager
import com.example.demov2026.model.LoginState
import com.example.demov2026.model.Roles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> = _state

    fun onEmailChange(nuevoEmail: String) {
        _state.value = _state.value.copy(
            email = nuevoEmail
        )
    }

    fun onPasswordChange(nuevoPassword: String) {
        _state.value = _state.value.copy(
            password = nuevoPassword
        )
    }

    fun onLoginClic() {

        if (_state.value.cargando) return

        _state.value = _state.value.copy(cargando = true, mensajeRes = null)

        viewModelScope.launch {

            val resultado = repository.login(_state.value.email, _state.value.password)

            resultado.onSuccess { response ->

                val user = response.body?.user

                SessionManager.iniciar(user?.token.orEmpty())

                _state.value = _state.value.copy(
                    loginExito = true,
                    cargando = false,
                    mensajeRes = null,
                    usuario = user?.usuario ?: _state.value.email,
                    token = user?.token ?: "",
                    nombre = user?.nombre ?: "",
                    apellido = user?.apellido ?: "",
                    telefono = user?.telefono.orEmpty(),
                    rol = user?.rol ?: Roles.CENSISTA
                )

            }.onFailure { error ->

                _state.value = _state.value.copy(
                    loginExito = false,
                    cargando = false,
                    mensajeRes = if (error is CredencialesIncorrectasException) {
                        R.string.login_error_credentials
                    } else {
                        R.string.login_error_connection
                    },
                    mensajeDetalle = error.message.orEmpty()
                )
            }
        }
    }
}