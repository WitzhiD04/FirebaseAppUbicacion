package com.example.tallerfirebase.ui.screens.map

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val location: Location? = null,
    val locationPoints: List<LatLng> = emptyList()
)
