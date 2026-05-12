package com.example.tallerfirebase.modelo

import com.google.android.gms.maps.model.LatLng

// TODO: Este modelo te servirá como base para traer la información de Firestore de los otros usuarios
data class OtroUser(
    val uid: String,
    val nombre: String,
    val ubicacion: LatLng,
    val routePoints: List<LatLng>
)