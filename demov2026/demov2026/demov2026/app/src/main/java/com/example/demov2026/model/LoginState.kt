package com.example.demov2026.model

import androidx.annotation.StringRes

data class LoginState(
    val email: String = "",
    val password: String = "",
    @StringRes val mensajeRes: Int? = null,
    val mensajeDetalle: String = "",
    val loginExito: Boolean = false,
    val cargando: Boolean = false,
    val usuario: String = "",
    val token: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val rol: String = ""
)
