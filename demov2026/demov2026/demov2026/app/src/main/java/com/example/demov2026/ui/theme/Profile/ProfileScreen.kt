package com.example.demov2026.ui.theme.Profile

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.model.Roles
import com.example.demov2026.ui.components.ContentCard

@Composable
fun ProfileScreen(
    nombre: String,
    apellido: String,
    usuario: String,
    telefono: String,
    rol: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMapaClick: () -> Unit = {},
    onCensoClick: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by profileViewModel.state.collectAsState()
    var mostrarOpciones by remember { mutableStateOf(false) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            profileViewModel.onFotoGaleria(context.contentResolver, uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            profileViewModel.onFotoTomada(bitmap)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) cameraLauncher.launch(null)
    }

    fun abrirCamara() {
        val yaConcedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (yaConcedido) cameraLauncher.launch(null)
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (mostrarOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarOpciones = false },
            title = {
                Text(stringResource(R.string.profile_photo_change))
            },
            text = {
                Column {
                    OpcionFoto(stringResource(R.string.profile_photo_camera)) {
                        mostrarOpciones = false
                        abrirCamara()
                    }
                    OpcionFoto(stringResource(R.string.profile_photo_gallery)) {
                        mostrarOpciones = false
                        galeriaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpciones = false }) {
                    Text(stringResource(R.string.profile_photo_cancel))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(48.dp))

        Surface(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .clickable { mostrarOpciones = true }
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            color = MaterialTheme.colorScheme.primary
        ) {
            if (state.fotoPerfil != null) {
                Image(
                    bitmap = state.fotoPerfil!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.profile_photo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${nombre.firstOrNull() ?: ""}${apellido.firstOrNull() ?: ""}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.guardandoFoto) {
            Text(
                text = stringResource(R.string.profile_photo_saving),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.mensajeRes?.let { mensajeRes ->
                Text(
                    text = stringResource(mensajeRes, state.mensajeDetalle),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.mensajeError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "$nombre $apellido",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(4.dp))

        Text(
            if (usuario.contains("@")) usuario else "@$usuario",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        ContentCard(contentPadding = 20.dp) {
            ProfileInfoRow(label = stringResource(R.string.profile_name), value = nombre)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ProfileInfoRow(label = stringResource(R.string.profile_last_name), value = apellido)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ProfileInfoRow(label = stringResource(R.string.profile_user), value = usuario)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ProfileInfoRow(
                label = stringResource(R.string.profile_phone),
                value = telefono.ifBlank { "—" }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ProfileInfoRow(
                label = stringResource(R.string.profile_role),
                value = stringResource(
                    if (rol == Roles.ADMINISTRADOR) R.string.role_administrator else R.string.role_surveyor
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        if (rol == Roles.CENSISTA) {
            Button(
                onClick = onCensoClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.profile_capture_census))
            }

            Spacer(Modifier.height(12.dp))
        }

        if (rol == Roles.ADMINISTRADOR) {
            Button(
                onClick = onMapaClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.profile_view_map))
            }

            Spacer(Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onLogoutClick,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(stringResource(R.string.profile_logout))
        }
    }
}

@Composable
private fun OpcionFoto(texto: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(texto) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
