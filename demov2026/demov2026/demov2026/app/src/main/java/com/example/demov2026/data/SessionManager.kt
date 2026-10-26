package com.example.demov2026.data

object SessionManager {

    @Volatile
    var token: String = ""
        private set

    fun iniciar(nuevoToken: String) {
        token = nuevoToken
    }

    fun cerrar() {
        token = ""
    }
}
