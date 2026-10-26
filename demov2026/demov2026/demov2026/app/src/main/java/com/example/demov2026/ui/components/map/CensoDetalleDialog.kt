package com.example.demov2026.ui.components.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.demov2026.R
import com.example.demov2026.model.response.CensoDto
import com.example.demov2026.ui.theme.Censo.formatearCoordenada
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun CensoDetalleDialog(
    censo: CensoDto,
    onCerrar: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text(stringResource(R.string.map_dialog_close))
            }
        },
        title = {
            Text("${censo.nombre} ${censo.apellido}")
        },
        text = {
            Column {
                DetalleFila(stringResource(R.string.censo_members), censo.totalMiembros.toString())
                DetalleFila(stringResource(R.string.censo_televisions), censo.cantidadTelevisores.toString())
                DetalleFila(stringResource(R.string.map_dialog_surveyor), censo.censista?.ifBlank { null } ?: "—")
                DetalleFila(stringResource(R.string.map_dialog_date), formatearFecha(censo.creadoEn))
                DetalleFila(
                    stringResource(R.string.map_dialog_origin),
                    stringResource(
                        if (censo.origen == "masivo") R.string.map_origin_bulk else R.string.map_origin_normal
                    )
                )
                DetalleFila(
                    stringResource(R.string.map_dialog_location),
                    "${formatearCoordenada(censo.latitud)}, ${formatearCoordenada(censo.longitud)}"
                )
            }
        }
    )
}

@Composable
private fun DetalleFila(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatearFecha(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"

    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val salida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        salida.format(entrada.parse(iso)!!)
    } catch (e: Exception) {
        iso
    }
}
