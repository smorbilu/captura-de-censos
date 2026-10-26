package com.example.demov2026.model

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng

data class CensoState(
    val nombre: String = "",
    val apellido: String = "",
    val totalMiembros: String = "",
    val cantidadTelevisores: String = "",
    val ubicacionMarcada: LatLng? = null,
    @StringRes val mensajeRes: Int? = null,
    val mensajeDetalle: String = "",
    val guardadoExito: Boolean = false,
    val cargando: Boolean = false
)
