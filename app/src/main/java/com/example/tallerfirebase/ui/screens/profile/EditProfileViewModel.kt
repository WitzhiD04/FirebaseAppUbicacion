package com.example.tallerfirebase.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tallerfirebase.modelo.UserData

class EditProfileViewModel: ViewModel() {

    private val _state = mutableStateOf(EditProfileState())
    val state: State<EditProfileState> = _state
    val updates = mutableMapOf<String, Any>()

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

    fun seleccionarFoto(uri: Uri, context: Context) {
        context.contentResolver.getType(uri)
        _state.value = _state.value.copy(fotoUri = uri)
    }

    fun cargarInformacionInicial(user: UserData) {
        _state.value = _state.value.copy(
            nombre = user.nombre,
            identificacion = user.identificacion,
            telefono = user.telefono
        )
    }

}