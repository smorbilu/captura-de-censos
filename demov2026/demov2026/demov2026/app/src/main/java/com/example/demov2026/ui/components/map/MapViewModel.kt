package com.example.demov2026.ui.components.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.CensoRepository
import com.example.demov2026.data.SesionInvalidaException
import com.example.demov2026.model.MapState
import com.example.demov2026.model.MarkerItem
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val repository = CensoRepository()

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state

    init {
        cargarCensos()
    }

    fun cargarCensos() {

        if (_state.value.cargando) return

        _state.value = _state.value.copy(cargando = true, mensajeRes = null)

        viewModelScope.launch {

            repository.obtenerTodos()
                .onSuccess { censos ->

                    val marcadores = censos.map { censo ->
                        MarkerItem(
                            position = LatLng(censo.latitud, censo.longitud),
                            title = "${censo.nombre} ${censo.apellido}",
                            censo = censo
                        )
                    }

                    _state.value = _state.value.copy(markers = marcadores, cargando = false)

                    ajustarCamara(marcadores.map { it.position })
                }
                .onFailure { error ->

                    _state.value = _state.value.copy(
                        cargando = false,
                        mensajeRes = if (error is SesionInvalidaException) {
                            R.string.censo_error_session
                        } else {
                            R.string.map_error_load
                        },
                        mensajeDetalle = error.message.orEmpty()
                    )
                }
        }
    }

    fun centrar(latlang: LatLng, zoom: Float) {
        _state.value = _state.value.copy(cameraPosition = latlang, zoom = zoom)
    }

    private fun ajustarCamara(posiciones: List<LatLng>) {

        if (posiciones.isEmpty()) return

        val latitudes = posiciones.map { it.latitude }.sorted()
        val longitudes = posiciones.map { it.longitude }.sorted()

        val inferior = (latitudes.size * 0.1).toInt()
        val superior = ((latitudes.size - 1) * 0.9).toInt()

        val latMin = latitudes[inferior]
        val latMax = latitudes[superior]
        val lonMin = longitudes[inferior]
        val lonMax = longitudes[superior]

        val centro = LatLng((latMin + latMax) / 2, (lonMin + lonMax) / 2)
        val extension = maxOf(latMax - latMin, lonMax - lonMin)

        val zoom = when {
            extension > 8 -> 4f
            extension > 4 -> 5.5f
            extension > 2 -> 7f
            extension > 1 -> 8f
            extension > 0.5 -> 9f
            extension > 0.2 -> 10.5f
            extension > 0.05 -> 12.5f
            else -> 14f
        }

        centrar(centro, zoom)
    }
}
