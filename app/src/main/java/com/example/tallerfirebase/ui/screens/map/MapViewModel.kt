package com.example.tallerfirebase.ui.screens.map

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun updateLocation(location: Location?) {
        if (location != null) {
            _state.update { state ->
                state.copy(
                    location = location,
                    locationPoints = state.locationPoints + LatLng(location.latitude, location.longitude)
                )
            }
        }
    }

    fun setFirstLocation(location: Location?) {
        _state.update { it.copy(location = location) }
    }
}
