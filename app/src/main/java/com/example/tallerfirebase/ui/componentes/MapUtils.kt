package com.example.tallerfirebase.ui.componentes

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.autofill.ContentType
import com.example.tallerfirebase.R
import com.example.tallerfirebase.ui.maps.api.KtorApiClient
import com.example.tallerfirebase.modelo.dto.RouteResponse
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.BuildConfig
import com.google.maps.android.PolyUtil
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.util.Locale

fun generateRandomColor(): Long{
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color.rgb(red, green, blue).toLong()
}

fun getLatLngFromAddress(
    address: String,
    context: Context
): LatLng? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try{
        val addresses: List<Address> = geocoder.getFromLocationName(address, 1) ?: emptyList()
        if(addresses.isNotEmpty()){
            val location = addresses[0]
            return LatLng(location.latitude, location.longitude)
        }else{
            Toast.makeText(
                context,
                context.getString(R.string.direccion_no_encontrada),
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
    }catch (e: Exception){
        e.printStackTrace()
        Toast.makeText(
            context,
            context.getString(R.string.error_al_buscar_direcci_n),
            Toast.LENGTH_SHORT
        ).show()
        return null
    }
}

fun getAddressFromLatLng(
    latLng: LatLng,
    context: Context
): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses: List<Address> =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                ?: emptyList()
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            address.getAddressLine(0)
                ?: context.getString(R.string.ubicaci_n_desconocida)
        } else {
            return context.getString(R.string.ubicaci_n_desconocida)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return context.getString(R.string.ubicaci_n_desconocida)
    }
}

suspend fun getRoute(
    origin: LatLng,
    destination: LatLng
): List<LatLng>?{
    return try{
        val client = KtorApiClient.client
        val req = """{
              "origin": {
                "location": {
                  "latLng": {
                    "latitude": ${origin.latitude},
                    "longitude": ${origin.longitude}
                  }
                }
              },
              "destination": {
                "location": {
                  "latLng": {
                    "latitude": ${destination.latitude},
                    "longitude": ${destination.longitude}
                  }
                }
              },
              "travelMode": "DRIVE",
              "units": "METRIC"
            }"""

        val res = client.post("https://routes.googleapis.com/directions/v2:computeRoutes"){
            header("X-Goog-Api-Key", com.example.tallerfirebase.BuildConfig.MAPS_API_KEY)
            header("X-Goog-FieldMask", "routes.polyline.encodedPolyline")
            header("Content-Type", "application/json")
            setBody(req)
        }

        if(res.status == HttpStatusCode.OK) {
            val routeRes = res.body<RouteResponse>()
            if (routeRes.routes != null && routeRes.routes.isNotEmpty()) {
                val encodedPolyline = routeRes.routes[0].polyline.encodedPolyline
                return PolyUtil.decode(encodedPolyline)
            }
        }
        return null
    }catch (e: Exception){
        Log.e("API Routes", "Exception: ${e.message}")
        return null
    }
}