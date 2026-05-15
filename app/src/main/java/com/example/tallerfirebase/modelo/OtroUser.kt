package com.example.tallerfirebase.modelo

import com.google.android.gms.maps.model.LatLng

data class OtroUser(
    val uid: String,
    val nombre: String,
    val ubicacion: LatLng,
    val routePoints: List<LatLng>
)