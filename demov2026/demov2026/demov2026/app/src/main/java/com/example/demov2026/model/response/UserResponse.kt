package com.example.demov2026.model.response

data class UserResponse(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val usuario: String,
    val email: String,
    val telefono: String?,
    val rol: String?,
    val token: String
)
