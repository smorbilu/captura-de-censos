package com.example.demov2026.ui.theme.Profile

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.DatosInvalidosException
import com.example.demov2026.data.FotoRepository
import com.example.demov2026.data.SesionInvalidaException
import com.example.demov2026.model.ProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileViewModel : ViewModel() {

    private val repository = FotoRepository()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private var subida: Job? = null

    init {
        cargarFotoRemota()
    }


    fun cargarFotoRemota() {
        viewModelScope.launch {
            repository.obtener().onSuccess { bytes ->
                if (bytes != null && _state.value.fotoPerfil == null) {
                    cargarFotoDesdeBytes(bytes)
                }
            }
        }
    }


    fun onFotoTomada(bitmap: Bitmap) {
        _state.value = _state.value.copy(fotoPerfil = bitmap)
        guardarFotoRemota()
    }


    fun onFotoGaleria(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) { decodificar(resolver, uri) }

            if (bitmap == null) {
                _state.value = _state.value.copy(
                    mensajeRes = R.string.profile_photo_error_read,
                    mensajeError = true
                )
                return@launch
            }

            onFotoTomada(bitmap)
        }
    }


    fun obtenerFotoEnByte(): ByteArray? {
        val bitmap = _state.value.fotoPerfil ?: return null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }


    fun cargarFotoDesdeBytes(bytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return
        _state.value = _state.value.copy(fotoPerfil = bitmap)
    }


    private fun guardarFotoRemota() {
        val bytes = obtenerFotoEnByte() ?: return

        subida?.cancel()

        _state.value = _state.value.copy(
            guardandoFoto = true,
            mensajeRes = null,
            mensajeError = false
        )

        subida = viewModelScope.launch {
            repository.guardar(bytes)
                .onSuccess {
                    _state.value = _state.value.copy(
                        guardandoFoto = false,
                        mensajeRes = R.string.profile_photo_saved,
                        mensajeError = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        guardandoFoto = false,
                        mensajeError = true,
                        mensajeRes = when (error) {
                            is SesionInvalidaException -> R.string.censo_error_session
                            is DatosInvalidosException -> R.string.profile_photo_error_invalid
                            else -> R.string.profile_photo_error_connection
                        },
                        mensajeDetalle = error.message.orEmpty()
                    )
                }
        }
    }


    private fun decodificar(resolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }

            if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

            var muestreo = 1
            while (maxOf(limites.outWidth, limites.outHeight) / muestreo > LADO_MAXIMO) {
                muestreo *= 2
            }

            val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
            val original = resolver.openInputStream(uri)
                ?.use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: return null

            val grados = gradosDeRotacion(resolver, uri)

            if (grados == 0f) {
                original
            } else {
                val matriz = Matrix().apply { postRotate(grados) }
                Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun gradosDeRotacion(resolver: ContentResolver, uri: Uri): Float {
        val orientacion = resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

        return when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }


    private companion object {
        const val LADO_MAXIMO = 800
    }
}
