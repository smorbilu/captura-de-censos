package com.example.demov2026.model

import android.graphics.Bitmap
import androidx.annotation.StringRes

data class ProfileState(
    val fotoPerfil: Bitmap? = null,
    val guardandoFoto: Boolean = false,
    @StringRes val mensajeRes: Int? = null,
    val mensajeError: Boolean = false,
    val mensajeDetalle: String = ""
)
