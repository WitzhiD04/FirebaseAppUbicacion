package com.example.tallerfirebase.modelo.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val routes: List<Route>? = null
)