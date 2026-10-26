package com.example.demov2026.model.request

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val password: String
)
