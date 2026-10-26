package com.example.demov2026.model.request

data class CensoRequest(
    val nombre: String,
    val apellido: String,
    val totalMiembros: Int,
    val cantidadTelevisores: Int,
    val latitud: Double,
    val longitud: Double
)
