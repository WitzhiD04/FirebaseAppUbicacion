package com.example.tallerfirebase.ui.screens.login

import android.net.Uri


data class RegisterState(
    val nombre: String = "",
    val identificacion: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val confirmPassword: String = "",
    val fotoUri: Uri? = null
)
