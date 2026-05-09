package com.example.tallerfirebase.ui.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel para gestionar el estado del mapa y la conexión.
 */
class MapViewModel : ViewModel() {
    var estado by mutableStateOf(MapState())
        private set

    fun cambiarEstadoConexion(conectado: Boolean) {
        estado = estado.copy(estaConectado = conectado)
    }
}
