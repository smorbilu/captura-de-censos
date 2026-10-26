package com.example.demov2026.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.demov2026.R
import com.example.demov2026.model.MarkerItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

private class MarcadorCluster(val marcador: MarkerItem) : ClusterItem {

    override fun getPosition(): LatLng = marcador.position

    override fun getTitle(): String = marcador.title

    override fun getSnippet(): String = marcador.snippet

    override fun getZIndex(): Float = 0f
}

@Composable
fun rememberEstiloMapa(): MapStyleOptions {
    val context = LocalContext.current
    val modoOscuro = isSystemInDarkTheme()

    return remember(modoOscuro) {
        MapStyleOptions.loadRawResourceStyle(
            context,
            if (modoOscuro) R.raw.map_style_dark else R.raw.map_style_gray
        )
    }
}

@Composable
private fun BurbujaCluster(cantidad: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Map(
    markers: List<MarkerItem>,
    cameraPosition: LatLng,
    zoom: Float,
    modifier: Modifier = Modifier,
    mostrarMiUbicacion: Boolean = false,
    onMarkerClic: (MarkerItem) -> Unit = {}
) {
    val estiloMapa = rememberEstiloMapa()
    val scope = rememberCoroutineScope()

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, zoom)
    }

    val propiedades = remember(mostrarMiUbicacion, estiloMapa) {
        MapProperties(
            isMyLocationEnabled = mostrarMiUbicacion,
            mapStyleOptions = estiloMapa
        )
    }

    val elementos = remember(markers) { markers.map { MarcadorCluster(it) } }

    LaunchedEffect(cameraPosition, zoom) {
        cameraState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(cameraPosition, zoom)
            )
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        properties = propiedades
    ) {
        Clustering(
            items = elementos,
            onClusterClick = { grupo ->
                scope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            grupo.position,
                            cameraState.position.zoom + 2f
                        )
                    )
                }
                true
            },
            onClusterItemClick = { elemento ->
                onMarkerClic(elemento.marcador)
                true
            },
            clusterContent = { grupo -> BurbujaCluster(grupo.size) }
        )
    }
}
