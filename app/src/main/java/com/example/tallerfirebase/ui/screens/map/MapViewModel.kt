package com.example.tallerfirebase.ui.screens.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerfirebase.modelo.CustomMarker
import com.example.tallerfirebase.ui.componentes.getAddressFromLatLng
import com.example.tallerfirebase.ui.componentes.getLatLngFromAddress
import com.example.tallerfirebase.ui.componentes.getRoute
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.plus

class MapViewModel : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private var debounceJob: Job? = null

    fun onFollowUserChanged(checked: Boolean) {
        _state.update { it.copy(checkedFollowUser = checked) }
    }

    fun onDirectionTextChanged(text: String, context: Context) {
        _state.update { it.copy(directionText = text) }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            if (text.isNotEmpty()) {
                delay(1000)
                _state.update { it.copy(debouncedDirectionText = text) }
                val latLng = getLatLngFromAddress(text, context)
                _state.update { it.copy(searchedAddress = latLng) }
            } else {
                _state.update {
                    it.copy(
                        debouncedDirectionText = "",
                        searchedAddress = null,
                        routePoints = emptyList()
                    )
                }
            }
        }
    }

    fun onAddMarker(latLng: LatLng, context: Context) {
        val address = getAddressFromLatLng(latLng, context)
        val newMarker = CustomMarker(
            id = _state.value.customMarkers.size,
            position = latLng,
            title = address,
            snippet = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
        )

        _state.update {
            it.copy(
                customMarkers = it.customMarkers + newMarker,
                selectedDestination = latLng
            )
        }

        calculateRoute()
    }

    fun updateLocation(location: Location?) {
        if (location != null) {
            _state.update { state ->
                // Only add to history if location changed significantly, or just add
                state.copy(
                    location = location,
                    locationPoints = state.locationPoints + LatLng(location.latitude, location.longitude)
                )
            }
            calculateRoute()
        }
    }

    fun setFirstLocation(location: Location?) {
        _state.update { it.copy(location = location) }
    }

    fun updateLuminosidad(luminosidad: Float) {
        _state.update { it.copy(luminosidad = luminosidad) }
    }

    private fun calculateRoute() {
        val dest = _state.value.selectedDestination
        val loc = _state.value.location
        if (dest != null && loc != null) {
            viewModelScope.launch {
                try {
                    val currentLatLng = LatLng(loc.latitude, loc.longitude)
                    val points = getRoute(currentLatLng, dest)
                    if (points != null && points.isNotEmpty()) {
                        _state.update { it.copy(routePoints = points) }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("Route", "Error: ${e.message}")
                }
            }
        }
    }
}
