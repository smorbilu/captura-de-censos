package com.example.demov2026.model.response

data class CensoDto(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val totalMiembros: Int,
    val cantidadTelevisores: Int,
    val latitud: Double,
    val longitud: Double,
    val origen: String?,
    val creadoEn: String?,
    val censista: String?
)

data class CensosResponse(
    val standardResponse: StandardResponse,
    val body: List<CensoDto>?
)
