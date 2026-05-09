package com.example.tallerfirebase.ui.screens.login


data class LoginState(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
    val email: String = "",
    val password: String = ""
)
