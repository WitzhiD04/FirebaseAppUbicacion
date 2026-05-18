package com.example.tallerfirebase.ui.screens.map

import android.location.Location
import com.example.tallerfirebase.modelo.OtroUser
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val location: Location? = null,
    val locationPoints: List<LatLng> = emptyList(),
    var otrosUser: List<OtroUser> = emptyList()
)
