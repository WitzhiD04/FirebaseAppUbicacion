package com.example.tallerfirebase.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel para gestionar la lógica del inicio de sesión.
 */
class LoginViewModel : ViewModel() {
    var estado by mutableStateOf(LoginState())
        private set

    fun iniciarSesion(correo: String, contrasena: String) {
        // TODO: Implementar lógica de Firebase
    }
}
