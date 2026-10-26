package com.example.demov2026.ui.theme.Register

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.model.SesionUsuario
import com.example.demov2026.ui.components.ContentCard

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel = viewModel(),
    onVolverLogin: () -> Unit = {},
    onRegistroExitoso: (SesionUsuario) -> Unit = {}
) {

    val state by registerViewModel.state.collectAsState()


    LaunchedEffect(state.registroExito) {

        if (state.registroExito) {
            onRegistroExitoso(
                SesionUsuario(
                    nombre = state.nombre,
                    apellido = state.apellido,
                    usuario = state.usuario,
                    telefono = state.telefono,
                    rol = state.rol
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))


        ContentCard(contentPadding = 20.dp) {

            OutlinedTextField(
                value = state.nombre,
                onValueChange = {
                    registerViewModel.onNombreChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_name))
                },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.apellido,
                onValueChange = {
                    registerViewModel.onApellidoChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_last_name))
                },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    registerViewModel.onEmailChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_email))
                },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.telefono,
                onValueChange = {
                    registerViewModel.onTelefonoChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_phone))
                },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    registerViewModel.onPasswordChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_password))
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = state.confirmarPassword,
                onValueChange = {
                    registerViewModel.onConfirmarPasswordChange(it)
                },
                label = {
                    Text(stringResource(R.string.register_confirm_password))
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {
                    registerViewModel.onRegisterClic()
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
                    Text(stringResource(R.string.register_button))
                }
            }


            state.mensajeRes?.let { mensajeRes ->

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(mensajeRes, state.mensajeDetalle),
                    color = if (state.registroExito) {
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


        TextButton(
            onClick = onVolverLogin
        ) {

            Text(stringResource(R.string.register_have_account))
        }
    }
}
