package com.example.tallerfirebase.ui.screens.profile

import android.net.Uri

data class EditProfileState (
    val nombre: String = "",
    val identificacion: String = "",
    val telefono: String = "",
    val fotoUri: Uri? = null,
    val contrasena: String = ""
)