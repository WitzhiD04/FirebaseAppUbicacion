package com.example.tallerfirebase.ui.screens.login

/**
 * Estado para la pantalla de inicio de sesión.
 * Mantiene la información del formulario y estados de carga o error.
 */
data class LoginState(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)
