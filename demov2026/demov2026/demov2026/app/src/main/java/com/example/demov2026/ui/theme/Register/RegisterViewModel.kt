package com.example.demov2026.ui.theme.Register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.data.CorreoYaRegistradoException
import com.example.demov2026.data.DatosInvalidosException
import com.example.demov2026.data.SessionManager
import com.example.demov2026.model.RegisterState
import com.example.demov2026.model.Roles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow(RegisterState())

    val state: StateFlow<RegisterState> = _state

    private val formatoCorreo = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")


    fun onNombreChange(nuevoNombre: String) {

        _state.value = _state.value.copy(
            nombre = nuevoNombre
        )
    }


    fun onApellidoChange(nuevoApellido: String) {

        _state.value = _state.value.copy(
            apellido = nuevoApellido
        )
    }


    fun onEmailChange(nuevoEmail: String) {

        _state.value = _state.value.copy(
            email = nuevoEmail
        )
    }


    fun onTelefonoChange(nuevoTelefono: String) {

        _state.value = _state.value.copy(
            telefono = nuevoTelefono
        )
    }


    fun onPasswordChange(nuevoPassword: String) {

        _state.value = _state.value.copy(
            password = nuevoPassword
        )
    }


    fun onConfirmarPasswordChange(nuevoPassword: String) {

        _state.value = _state.value.copy(
            confirmarPassword = nuevoPassword
        )
    }


    fun onRegisterClic() {

        val datos = _state.value

        if (datos.cargando) return

        if (
            datos.nombre.isBlank() ||
            datos.apellido.isBlank() ||
            datos.email.isBlank() ||
            datos.telefono.isBlank() ||
            datos.password.isBlank()
        ) {

            _state.value = datos.copy(
                mensajeRes = R.string.register_error_empty,
                registroExito = false
            )

            return
        }


        if (!formatoCorreo.matches(datos.email.trim())) {

            _state.value = datos.copy(
                mensajeRes = R.string.register_error_invalid_email,
                registroExito = false
            )

            return
        }


        if (datos.password != datos.confirmarPassword) {

            _state.value = datos.copy(
                mensajeRes = R.string.register_error_password_mismatch,
                registroExito = false
            )

            return
        }


        _state.value = datos.copy(
            cargando = true,
            mensajeRes = null,
            registroExito = false
        )

        viewModelScope.launch {

            val resultado = repository.registrar(
                datos.nombre.trim(),
                datos.apellido.trim(),
                datos.email.trim(),
                datos.password,
                datos.telefono.trim()
            )

            resultado.onSuccess { response ->

                val user = response.body?.user

                SessionManager.iniciar(user?.token.orEmpty())

                _state.value = _state.value.copy(
                    cargando = false,
                    mensajeRes = null,
                    registroExito = true,
                    usuario = user?.usuario ?: datos.email.trim().lowercase(),
                    token = user?.token.orEmpty(),
                    nombre = user?.nombre ?: datos.nombre.trim(),
                    apellido = user?.apellido ?: datos.apellido.trim(),
                    telefono = user?.telefono ?: datos.telefono.trim(),
                    rol = user?.rol ?: Roles.CENSISTA
                )

            }.onFailure { error ->

                _state.value = _state.value.copy(
                    cargando = false,
                    registroExito = false,
                    mensajeRes = when (error) {
                        is CorreoYaRegistradoException -> R.string.register_error_email_exists
                        is DatosInvalidosException -> R.string.register_error_invalid_data
                        else -> R.string.register_error_connection
                    },
                    mensajeDetalle = error.message.orEmpty()
                )
            }
        }
    }
}
