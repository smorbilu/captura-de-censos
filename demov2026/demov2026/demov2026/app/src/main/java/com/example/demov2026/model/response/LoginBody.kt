package com.example.demov2026.model.response

data class LoginBody(
    val token: String,
    val user: UserResponse
)
