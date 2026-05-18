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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.key
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
import com.example.tallerfirebase.ui.componentes.CustomMapMarker
import com.example.tallerfirebase.ui.theme.MoradoClaro
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PinConfig
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapTopBar(modifier: Modifier = Modifier, user: UserData, viewModel: MainViewModel) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(16.dp),
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
    mainViewModel: MainViewModel = viewModel(),
    otrosUsuarios: List<OtroUser> = emptyList()
){
    val state by viewModel.state.collectAsState()
    val locationPermissionsState = com.google.accompanist.permissions.rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(!locationPermissionsState.allPermissionsGranted){
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    val strPermisoUbicacion = stringResource(R.string.se_requiere_permiso_de_ubicaci_n_para_continuar)
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if(!locationPermissionsState.allPermissionsGranted){
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
                    .navigationBarsPadding()
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
            if(locationPermissionsState.allPermissionsGranted || locationPermissionsState.revokedPermissions.size < 2){
                val fusedLocationClient = remember {
                    LocationServices.getFusedLocationProviderClient(context)
                }

                val strLocation = stringResource(R.string.location)

                DisposableEffect(user.conectado) {
                    if(!user.conectado) {
                        viewModel.desconectarse()
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
                                val lat = newLocation.latitude
                                val lng = newLocation.longitude
                                mainViewModel.actualizarLngLat(lat, lng, user.uid, context)
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
            }

            Map(
                currentLocation = state.location,
                userRoutePoints = state.locationPoints, 
                otroUsers = otrosUsuarios,
                modifier = Modifier.weight(1f),
                context = context,
                viewModel = viewModel,
                userData = user
            )
        }
    }
}



@Composable
fun Map(
    currentLocation: Location?,
    userRoutePoints: List<LatLng>,
    otroUsers: List<OtroUser>,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    viewModel: MapViewModel,
    userData: UserData,
){
    val latLng = if(currentLocation != null){
        LatLng(currentLocation.latitude, currentLocation.longitude)
    }else{
        LatLng(29.4383, 85.17577)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    val markerState = rememberMarkerState()

    val isDarkTheme = isSystemInDarkTheme()
    val mapProperties = if (isDarkTheme) {
        MapProperties(mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark))
    } else {
        MapProperties()
    }

    LaunchedEffect(currentLocation) {
        if(currentLocation != null){
            val newLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            markerState.position = newLatLng
            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 15f)
        }
    }

    key(isDarkTheme) {
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            googleMapOptionsFactory = {
                if (isDarkTheme) {
                    GoogleMapOptions()
                } else {
                    GoogleMapOptions().mapId("3edf23cba8f50247eb3ee63d")
                }
            },
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                mapToolbarEnabled = true,
                compassEnabled = true
            )
        ) {
        if (userRoutePoints.size > 1) {
            Polyline(
                points = userRoutePoints,
                clickable = true,
                color = MoradoClaro,
                width = 12f,
                startCap = RoundCap(),
                endCap = RoundCap(),
                geodesic = true
            )
        }

        if (currentLocation != null) {
            CustomMapMarker(
                imageUrl = userData.fotoUrl,
                fullName = "Tu ubicación",
                location = markerState.position
            ) { }
        }

        otroUsers.forEach { otherUser ->
            if (otherUser.routePoints.size > 1) {
                Polyline(
                    points = otherUser.routePoints,
                    clickable = true,
                    color = Color.Red,
                    width = 10f,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    geodesic = true
                )
            }

            CustomMapMarker(
                imageUrl = otherUser.fotoUri,
                fullName = otherUser.nombre,
                location = otherUser.ubicacion,
            ) { }

        }
        }
    }
}

