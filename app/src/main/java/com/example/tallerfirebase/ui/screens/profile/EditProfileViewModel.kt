package com.example.tallerfirebase.ui.screens.profile

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
import com.example.tallerfirebase.modelo.UserData
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileViewModel: ViewModel() {

    private val _state = mutableStateOf(EditProfileState())
    val state: State<EditProfileState> = _state
    val updates = mutableMapOf<String, Any>()

    private var datosCargados = false

    fun cargarDatos(userData: UserData) {
        if (!datosCargados) {
            _state.value = _state.value.copy(
                nombre = userData.nombre,
                identificacion = userData.identificacion,
                telefono = userData.telefono,
                fotoUri = if (userData.fotoUrl.isNotEmpty()) Uri.parse(userData.fotoUrl) else null
            )
            datosCargados = true
        }
    }

    fun onNombreChange(nombre: String) {
        _state.value = _state.value.copy(nombre = nombre)
        updates["nombre"] = nombre
    }

    fun onIdentificacionChange(identificacion: String) {
        _state.value = _state.value.copy(identificacion = identificacion)
        updates["identificacion"] = identificacion
    }

    fun onTelefonoChange(telefono: String) {
        _state.value = _state.value.copy(telefono = telefono)
        updates["telefono"] = telefono
    }

    fun seleccionarFoto(uri: Uri) {
        _state.value = _state.value.copy(fotoUri = uri)
    }

    fun removerFoto() {
        _state.value = _state.value.copy(fotoUri = null)
    }

    fun cargarInformacionInicial(user: UserData) {
        _state.value = _state.value.copy(
            nombre = user.nombre,
            identificacion = user.identificacion,
            telefono = user.telefono,
            fotoUri = if (user.fotoUrl.isNotEmpty()) Uri.parse(user.fotoUrl) else null
        )
    }

    fun onContrasenaChange(contrasena: String) {
        _state.value = _state.value.copy(contrasena = contrasena)
        if(contrasena.isNotEmpty()){
            updates["contrasena"] = contrasena
        }
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