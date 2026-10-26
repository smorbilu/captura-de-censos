package com.example.demov2026.model.response

data class FotoBody(
    val foto: String?
)

data class FotoResponse(
    val standardResponse: StandardResponse,
    val body: FotoBody?
)
