package com.example.demov2026.model

import androidx.annotation.StringRes

data class RegisterState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    @StringRes val mensajeRes: Int? = null,
    val mensajeDetalle: String = "",
    val registroExito: Boolean = false,
    val cargando: Boolean = false,
    val usuario: String = "",
    val token: String = "",
    val rol: String = ""
)
