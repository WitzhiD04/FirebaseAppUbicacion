package com.example.tallerfirebase.ui.screens.login

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class RegisterViewModel : ViewModel() {
    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    fun onNombreChange(nombre: String) {
        _state.value = _state.value.copy(nombre = nombre)
    }

    fun onIdentificacionChange(identificacion: String) {
        _state.value = _state.value.copy(identificacion = identificacion)
    }

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onTelefonoChange(telefono: String) {
        _state.value = _state.value.copy(telefono = telefono)
    }

    fun seleccionarFoto(uri: Uri, context: Context) {
        context.contentResolver.getType(uri)
        _state.value = _state.value.copy(fotoUri = uri)
    }

    fun removerFoto() {
        _state.value = _state.value.copy(fotoUri = null)
    }

    fun captura(context: Context) {
        val nuevoNombre = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            Locale.US
        ).format(System.currentTimeMillis())

        val nuevosValores = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nuevoNombre)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
            }
        }

        val salida = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            nuevosValores
        ).build()

        _state.value = _state.value.copy(
            contentValues = nuevosValores,
            outputFileOptions = salida
        )
    }

    fun tomarFoto(controller: LifecycleCameraController, context: Context) {
        captura(context)
        val outputFileOptions = _state.value.outputFileOptions
        if (outputFileOptions != null) {
            controller.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val uri = outputFileResults.savedUri
                        _state.value = _state.value.copy(fotoUri = uri)
                        Log.i(
                            "CUSTOM CAMERA",
                            "Image capture success: ${outputFileResults.savedUri}"
                        )
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CUSTOM CAMERA", "Image capture failed", exception)
                    }
                }
            )
        }
    }
}
