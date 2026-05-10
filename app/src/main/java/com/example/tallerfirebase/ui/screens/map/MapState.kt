package com.example.tallerfirebase.ui.screens.map

import android.location.Location
import com.example.tallerfirebase.modelo.CustomMarker
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val checkedFollowUser: Boolean = false,
    val directionText: String = "",
    val debouncedDirectionText: String = "",
    val customMarkers: List<CustomMarker> = emptyList(),
    val searchedAddress: LatLng? = null,
    val routePoints: List<LatLng> = emptyList(),
    val selectedDestination: LatLng? = null,
    val location: Location? = null,
    val locationPoints: List<LatLng> = emptyList(),
    val luminosidad: Float = 0f
)
