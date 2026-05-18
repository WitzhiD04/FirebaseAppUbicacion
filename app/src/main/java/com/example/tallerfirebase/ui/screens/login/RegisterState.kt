package com.example.tallerfirebase.ui.screens.login

import android.content.ContentValues
import android.net.Uri
import androidx.camera.core.ImageCapture


data class RegisterState(
    val nombre: String = "",
    val identificacion: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val confirmPassword: String = "",
    val fotoUri: Uri? = null,
    val outputFileOptions: ImageCapture.OutputFileOptions? = null,
    val contentValues: ContentValues? = null
)
