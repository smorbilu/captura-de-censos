package com.example.demov2026.data

import com.example.demov2026.data.remote.RetrofitClient
import com.example.demov2026.model.request.CensoRequest
import com.example.demov2026.model.response.CensoDto

class SesionInvalidaException : Exception()

class CensoRepository {

    private val apiService = RetrofitClient.apiService


    suspend fun guardar(
        nombre: String,
        apellido: String,
        totalMiembros: Int,
        cantidadTelevisores: Int,
        latitud: Double,
        longitud: Double
    ): Result<Unit> {
        return try {
            val response = apiService.crearCenso(
                CensoRequest(
                    nombre = nombre,
                    apellido = apellido,
                    totalMiembros = totalMiembros,
                    cantidadTelevisores = cantidadTelevisores,
                    latitud = latitud,
                    longitud = longitud
                )
            )

            when {
                response.isSuccessful -> Result.success(Unit)
                response.code() == 401 || response.code() == 403 ->
                    Result.failure(SesionInvalidaException())
                response.code() == 400 -> Result.failure(DatosInvalidosException())
                else -> Result.failure(ErrorConexionException("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }


    suspend fun obtenerTodos(): Result<List<CensoDto>> {
        return try {
            val response = apiService.obtenerCensos()

            when {
                response.isSuccessful -> Result.success(response.body()?.body.orEmpty())
                response.code() == 401 || response.code() == 403 ->
                    Result.failure(SesionInvalidaException())
                else -> Result.failure(ErrorConexionException("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }
}
