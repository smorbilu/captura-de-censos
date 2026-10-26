package com.example.demov2026.data

import android.util.Base64
import com.example.demov2026.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class FotoRepository {

    private val apiService = RetrofitClient.apiService


    suspend fun guardar(foto: ByteArray): Result<Unit> {
        return try {
            val response = apiService.guardarFoto(foto.toRequestBody("image/jpeg".toMediaType()))

            when {
                response.isSuccessful -> Result.success(Unit)
                response.code() == 401 || response.code() == 403 ->
                    Result.failure(SesionInvalidaException())
                response.code() == 400 || response.code() == 413 ->
                    Result.failure(DatosInvalidosException())
                else -> Result.failure(ErrorConexionException("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }


    suspend fun obtener(): Result<ByteArray?> {
        return try {
            val response = apiService.obtenerFoto()

            when {
                response.isSuccessful -> {
                    val base64 = response.body()?.body?.foto
                    Result.success(base64?.let { Base64.decode(it, Base64.DEFAULT) })
                }
                response.code() == 401 || response.code() == 403 ->
                    Result.failure(SesionInvalidaException())
                else -> Result.failure(ErrorConexionException("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }
}
