package com.example.tallerfirebase.ui.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.OtroUser
import com.example.tallerfirebase.modelo.UserData
import com.example.tallerfirebase.ui.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PinConfig
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapTopBar(modifier: Modifier = Modifier, user: UserData, viewModel: MainViewModel) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.bienvenido, user.nombre),
                style = typography.titleLarge
            )
            Icon(Icons.Filled.Directions, contentDescription = "Ubicación")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = if (user.conectado) stringResource(R.string.conectado) else stringResource(R.string.no_conectado))
            Switch(
                checked = user.conectado,
                onCheckedChange = { viewModel.conectado() }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    alCerrarSesion: () -> Unit = {},
    user: UserData,
    alVerPerfil: () -> Unit = {},
    mainViewModel: MainViewModel = viewModel()
){
    val state by viewModel.state.collectAsState()
    val permissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(!permissionState.status.isGranted){
            permissionState.launchPermissionRequest()
        }
    }

    val strPermisoUbicacion = stringResource(R.string.se_requiere_permiso_de_ubicaci_n_para_continuar)
    LaunchedEffect(permissionState.status.isGranted) {
        if(!permissionState.status.isGranted){
            Toast.makeText(
                context,
                strPermisoUbicacion,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MapTopBar(
                modifier = Modifier.fillMaxWidth(),
                user = user,
                viewModel = mainViewModel
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = alVerPerfil) { 
                    Text(stringResource(R.string.ver_mi_perfil))
                }
                Button(onClick = alCerrarSesion) {
                    Text(stringResource(R.string.cerrar_sesi_n))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if(permissionState.status.isGranted){
                val fusedLocationClient = remember {
                    LocationServices.getFusedLocationProviderClient(context)
                }

                val strLocation = stringResource(R.string.location)

                // La actualización de ubicación ahora depende del switch: user.conectado
                DisposableEffect(user.conectado) {
                    if(!user.conectado) {
                        // TODO: Limpiar ruta (polyline) si el usuario se desconecta (25%)
                        return@DisposableEffect onDispose {}
                    }
                    val locationRequest =
                        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                            .apply {
                                setMinUpdateDistanceMeters(3F)
                                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                                setWaitForAccurateLocation(true)
                            }.build()
                    
                    val locationCallback = object: LocationCallback(){
                        override fun onLocationResult(locationResult: LocationResult) {
                            val newLocation = locationResult.lastLocation
                            if(newLocation != null){
                                viewModel.updateLocation(newLocation)
                                // TODO: Aquí se debe actualizar la posición en Firestore en tiempo real (25%)
                            }
                        }
                    }
                    try{
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }catch (e: SecurityException){
                        Log.e(strLocation, e.toString())
                    }

                    onDispose {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }

                Map(
                    currentLocation = state.location,
                    userRoutePoints = state.locationPoints, 
                    otroUsers = emptyList(), // TODO: Obtener y pasar la lista de otros usuarios conectados desde Firestore (25%)
                    modifier = Modifier.weight(1f),
                    context = context
                )
            }
        }
    }
}



@Composable
fun Map(
    currentLocation: Location?,
    userRoutePoints: List<LatLng>,
    otroUsers: List<OtroUser>,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
){
    val latLng = if(currentLocation != null){
        LatLng(currentLocation.latitude, currentLocation.longitude)
    }else{
        LatLng(4.628829, -74.063589) // Default (Bogotá)
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    val markerState = rememberMarkerState()

    LaunchedEffect(currentLocation) {
        if(currentLocation != null){
            val newLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            markerState.position = newLatLng
            // Centrar la cámara en la posición actual
            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 15f)
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            zoomGesturesEnabled = true,
            mapToolbarEnabled = false,
            compassEnabled = false
        )
    ) {
        // Dibujar ruta (polyline) del usuario actual (25%)
        if(userRoutePoints.size > 1){
            Polyline(
                points = userRoutePoints,
                clickable = true,
                color = Color.Blue, 
                width = 12f,
                startCap = RoundCap(),
                endCap = RoundCap(),
                geodesic = true
            )
        }

        // Marcador del usuario actual.
        // REQUERIMIENTO (20%): El diseño es libre pero no usar los default de Google Maps.
        // Usamos AdvancedMarker con un color personalizado como base.
        if (currentLocation != null) {
            AdvancedMarker(
                state = markerState,
                title = stringResource(R.string.ubicaci_n_actual),
                snippet = "Tu posición",
                pinConfig = PinConfig.builder()
                    .setBackgroundColor(android.graphics.Color.BLUE) 
                    .setBorderColor(android.graphics.Color.WHITE)
                    .build()
            )
        }

        // Dibujar otros usuarios y sus rutas (25%)
        otroUsers.forEach { otherUser ->
            if (otherUser.routePoints.size > 1) {
                Polyline(
                    points = otherUser.routePoints,
                    clickable = true,
                    color = Color.Red, // Diferente color para diferenciar de la ruta propia
                    width = 10f,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    geodesic = true
                )
            }
            
            AdvancedMarker(
                state = rememberMarkerState(position = otherUser.ubicacion),
                title = otherUser.nombre,
                pinConfig = PinConfig.builder()
                    .setBackgroundColor(android.graphics.Color.RED)
                    .setBorderColor(android.graphics.Color.WHITE)
                    .build()
            )
        }
    }
}

