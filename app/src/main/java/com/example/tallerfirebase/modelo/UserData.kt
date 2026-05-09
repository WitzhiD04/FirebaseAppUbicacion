package com.example.tallerfirebase.modelo

import com.google.firebase.Timestamp

data class UserData(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val conectado: Boolean = false,
    val creacion: Timestamp,
    val fotoUrl: String = "",
    val identificacion: String = "",
)
