package com.example.demov2026.model

import androidx.annotation.StringRes
import com.example.demov2026.model.response.CensoDto
import com.google.android.gms.maps.model.LatLng

data class MarkerItem(
    val position: LatLng,
    val title: String = "",
    val snippet: String = "",
    val censo: CensoDto? = null
)

data class MapState(
    val markers: List<MarkerItem> = emptyList(),
    val cameraPosition: LatLng = LatLng(14.634262, -90.507758),
    val zoom: Float = 6f,
    val cargando: Boolean = false,
    @StringRes val mensajeRes: Int? = null,
    val mensajeDetalle: String = ""
)
