package com.example.demov2026.ui.theme.Censo

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.data.UbicacionProvider
import com.example.demov2026.ui.components.ContentCard

@Composable
fun CensoScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    censoViewModel: CensoViewModel = viewModel()
) {

    val context = LocalContext.current
    val state by censoViewModel.state.collectAsState()
    val ubicacionProvider = remember { UbicacionProvider(context.applicationContext) }

    fun tienePermisoUbicacion() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    var permisoUbicacion by remember { mutableStateOf(tienePermisoUbicacion()) }
    var mostrarSelector by remember { mutableStateOf(false) }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        permisoUbicacion = resultado.values.any { it }
        if (permisoUbicacion) {
            censoViewModel.onGuardarClic(ubicacionProvider)
        } else {
            censoViewModel.onPermisoDenegado()
        }
    }

    val selectorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        permisoUbicacion = resultado.values.any { it }
        mostrarSelector = true
    }

    if (mostrarSelector) {
        SelectorUbicacionDialog(
            ubicacionInicial = state.ubicacionMarcada,
            ubicacionProvider = ubicacionProvider,
            permisoUbicacion = permisoUbicacion,
            onConfirmar = { punto ->
                censoViewModel.onUbicacionMarcada(punto)
                mostrarSelector = false
            },
            onCancelar = { mostrarSelector = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.censo_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))


        ContentCard(contentPadding = 20.dp) {

            OutlinedTextField(
                value = state.nombre,
                onValueChange = {
                    censoViewModel.onNombreChange(it)
                },
                label = {
                    Text(stringResource(R.string.censo_name))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.apellido,
                onValueChange = {
                    censoViewModel.onApellidoChange(it)
                },
                label = {
                    Text(stringResource(R.string.censo_last_name))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.totalMiembros,
                onValueChange = {
                    censoViewModel.onTotalMiembrosChange(it)
                },
                label = {
                    Text(stringResource(R.string.censo_members))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.cantidadTelevisores,
                onValueChange = {
                    censoViewModel.onCantidadTelevisoresChange(it)
                },
                label = {
                    Text(stringResource(R.string.censo_televisions))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(16.dp))


            OutlinedButton(
                onClick = {
                    if (tienePermisoUbicacion()) {
                        permisoUbicacion = true
                        mostrarSelector = true
                    } else {
                        selectorLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.censo_mark_location))
            }


            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = state.ubicacionMarcada?.let {
                    stringResource(
                        R.string.censo_location_marked,
                        formatearCoordenada(it.latitude),
                        formatearCoordenada(it.longitude)
                    )
                } ?: stringResource(R.string.censo_location_auto),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )


            if (state.ubicacionMarcada != null) {

                TextButton(
                    onClick = { censoViewModel.onQuitarUbicacion() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.censo_use_current_location))
                }
            }


            Spacer(modifier = Modifier.height(12.dp))


            Button(
                onClick = {
                    if (state.ubicacionMarcada != null || tienePermisoUbicacion()) {
                        censoViewModel.onGuardarClic(ubicacionProvider)
                    } else {
                        permisoLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !state.cargando,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                if (state.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.censo_save))
                }
            }


            state.mensajeRes?.let { mensajeRes ->

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(mensajeRes, state.mensajeDetalle),
                    color = if (state.guardadoExito) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))


        TextButton(onClick = onVolver) {

            Text("← ${stringResource(R.string.map_back)}")
        }
    }
}
