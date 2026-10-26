package com.example.demov2026.ui.theme.Censo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.CensoRepository
import com.example.demov2026.data.DatosInvalidosException
import com.example.demov2026.data.SesionInvalidaException
import com.example.demov2026.data.UbicacionProvider
import com.example.demov2026.model.CensoState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CensoViewModel : ViewModel() {

    private val repository = CensoRepository()

    private val _state = MutableStateFlow(CensoState())

    val state: StateFlow<CensoState> = _state


    fun onNombreChange(nuevoNombre: String) {
        _state.value = _state.value.copy(
            nombre = nuevoNombre,
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onApellidoChange(nuevoApellido: String) {
        _state.value = _state.value.copy(
            apellido = nuevoApellido,
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onTotalMiembrosChange(nuevoTotal: String) {
        _state.value = _state.value.copy(
            totalMiembros = nuevoTotal.filter { it.isDigit() },
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onCantidadTelevisoresChange(nuevaCantidad: String) {
        _state.value = _state.value.copy(
            cantidadTelevisores = nuevaCantidad.filter { it.isDigit() },
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onUbicacionMarcada(punto: LatLng) {
        _state.value = _state.value.copy(
            ubicacionMarcada = punto,
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onQuitarUbicacion() {
        _state.value = _state.value.copy(
            ubicacionMarcada = null,
            mensajeRes = null,
            guardadoExito = false
        )
    }


    fun onPermisoDenegado() {
        _state.value = _state.value.copy(
            mensajeRes = R.string.censo_error_location,
            guardadoExito = false
        )
    }


    fun onGuardarClic(ubicacionProvider: UbicacionProvider) {

        val datos = _state.value

        if (datos.cargando) return

        if (
            datos.nombre.isBlank() ||
            datos.apellido.isBlank() ||
            datos.totalMiembros.isBlank() ||
            datos.cantidadTelevisores.isBlank()
        ) {

            _state.value = datos.copy(
                mensajeRes = R.string.censo_error_empty,
                guardadoExito = false
            )

            return
        }

        val miembros = datos.totalMiembros.toIntOrNull()
        val televisores = datos.cantidadTelevisores.toIntOrNull()

        if (miembros == null || miembros < 1 || televisores == null) {

            _state.value = datos.copy(
                mensajeRes = R.string.censo_error_numbers,
                guardadoExito = false
            )

            return
        }

        _state.value = datos.copy(
            cargando = true,
            mensajeRes = null,
            guardadoExito = false
        )

        viewModelScope.launch {

            val punto = datos.ubicacionMarcada
                ?: ubicacionProvider.obtener()?.let { LatLng(it.latitude, it.longitude) }

            if (punto == null) {

                _state.value = _state.value.copy(
                    cargando = false,
                    mensajeRes = R.string.censo_error_location,
                    guardadoExito = false
                )

                return@launch
            }

            val resultado = repository.guardar(
                nombre = datos.nombre.trim(),
                apellido = datos.apellido.trim(),
                totalMiembros = miembros,
                cantidadTelevisores = televisores,
                latitud = punto.latitude,
                longitud = punto.longitude
            )

            resultado.onSuccess {

                _state.value = CensoState(
                    mensajeRes = R.string.censo_saved,
                    guardadoExito = true
                )

            }.onFailure { error ->

                _state.value = _state.value.copy(
                    cargando = false,
                    guardadoExito = false,
                    mensajeRes = when (error) {
                        is SesionInvalidaException -> R.string.censo_error_session
                        is DatosInvalidosException -> R.string.censo_error_invalid_data
                        else -> R.string.censo_error_connection
                    },
                    mensajeDetalle = error.message.orEmpty()
                )
            }
        }
    }
}
