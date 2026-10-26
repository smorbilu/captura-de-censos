package com.example.demov2026.data.remote

import com.example.demov2026.model.request.CensoRequest
import com.example.demov2026.model.request.LoginRequest
import com.example.demov2026.model.request.RegisterRequest
import com.example.demov2026.model.response.CensoResponse
import com.example.demov2026.model.response.CensosResponse
import com.example.demov2026.model.response.FotoResponse
import com.example.demov2026.model.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("api/censos")
    suspend fun crearCenso(@Body request: CensoRequest): Response<CensoResponse>

    @GET("api/censos")
    suspend fun obtenerCensos(): Response<CensosResponse>

    @PUT("api/perfil/foto")
    suspend fun guardarFoto(@Body foto: RequestBody): Response<FotoResponse>

    @GET("api/perfil/foto")
    suspend fun obtenerFoto(): Response<FotoResponse>
}
