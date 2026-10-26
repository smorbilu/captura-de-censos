package com.example.demov2026.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class UbicacionProvider(context: Context) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val executor = ContextCompat.getMainExecutor(context)

    private val proveedores = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER
    )

    @SuppressLint("MissingPermission")
    suspend fun obtener(): Location? {
        for (proveedor in proveedores) {
            if (!locationManager.isProviderEnabled(proveedor)) continue

            val actual = withTimeoutOrNull(TIEMPO_ESPERA_MS) { ubicacionActual(proveedor) }
            if (actual != null) return actual

            val ultima = locationManager.getLastKnownLocation(proveedor)
            if (ultima != null) return ultima
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun ubicacionActual(proveedor: String): Location? =
        suspendCancellableCoroutine { continuacion ->
            val cancelacion = CancellationSignal()
            continuacion.invokeOnCancellation { cancelacion.cancel() }

            LocationManagerCompat.getCurrentLocation(
                locationManager,
                proveedor,
                cancelacion,
                executor
            ) { ubicacion ->
                if (continuacion.isActive) continuacion.resume(ubicacion)
            }
        }

    private companion object {
        const val TIEMPO_ESPERA_MS = 8_000L
    }
}
