package com.example.demov2026.data

import com.example.demov2026.data.remote.RetrofitClient
import com.example.demov2026.model.request.LoginRequest
import com.example.demov2026.model.request.RegisterRequest
import com.example.demov2026.model.response.LoginResponse

class CredencialesIncorrectasException : Exception()

class CorreoYaRegistradoException : Exception()

class DatosInvalidosException : Exception()

class ErrorConexionException(detalle: String?) : Exception(detalle)

class AuthRepository {

    private val apiService = RetrofitClient.apiService


    suspend fun login(usuario: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(usuario, password))

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(CredencialesIncorrectasException())
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }


    suspend fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        telefono: String
    ): Result<LoginResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    telefono = telefono,
                    password = password
                )
            )

            when {
                response.isSuccessful -> {
                    val sesion = response.body()
                    if (sesion?.body != null) Result.success(sesion) else login(email, password)
                }
                response.code() == 409 -> Result.failure(CorreoYaRegistradoException())
                response.code() == 400 -> Result.failure(DatosInvalidosException())
                else -> Result.failure(ErrorConexionException("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(ErrorConexionException(e.message))
        }
    }
}
