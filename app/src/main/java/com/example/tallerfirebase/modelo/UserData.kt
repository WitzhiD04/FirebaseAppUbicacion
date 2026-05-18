package com.example.tallerfirebase.modelo

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class UserData(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val ubicacion: GeoPoint = GeoPoint(0.0, 0.0),
    val conectado: Boolean = false,
    val creacion: Timestamp = Timestamp.now(),
    val fotoUrl: String = "",
    val identificacion: String = "",
    val ultimoActualizado: Timestamp = Timestamp.now(),
    val historial: List<GeoPoint> = emptyList()
)
