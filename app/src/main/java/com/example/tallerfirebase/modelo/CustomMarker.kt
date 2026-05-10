package com.example.tallerfirebase.modelo

import com.example.tallerfirebase.ui.componentes.generateRandomColor
import com.google.android.gms.maps.model.LatLng

data class CustomMarker(
    val id: Int,
    val position: LatLng,
    val title: String,
    val snippet: String,
    val color: Long = generateRandomColor()
)