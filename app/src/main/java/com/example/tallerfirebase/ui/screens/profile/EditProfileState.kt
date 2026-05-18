package com.example.tallerfirebase.ui.screens.profile

import android.content.ContentValues
import android.net.Uri
import androidx.camera.core.ImageCapture

data class EditProfileState (
    val nombre: String = "",
    val identificacion: String = "",
    val telefono: String = "",
    val fotoUri: Uri? = null,
    val contrasena: String = "",
    val outputFileOptions: ImageCapture.OutputFileOptions? = null,
    val contentValues: ContentValues? = null
)