package com.example.tallerfirebase.modelo
sealed class AuthState {
    object autenticado : AuthState()
    object noAutenticado : AuthState()
    object cargando : AuthState()
    data class Error(val mensaje: String) : AuthState()
}