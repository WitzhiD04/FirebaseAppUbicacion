package com.example.tallerfirebase.ui.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.CustomMarker
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
import com.google.android.gms.maps.model.PinConfig
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun lightSensor(context: Context = LocalContext.current): State<Float>{
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val lightSensor = remember{
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    val luminosidad = remember {
        mutableStateOf(0f)
    }

    DisposableEffect(Unit) {
        val listener = object: SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val lux = event.values[0]
                luminosidad.value = lux
            }
        }
        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
    return luminosidad
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    alEditarPerfil: () -> Unit = {},
    alCerrarSesion: () -> Unit = {},
    alCambiarConexion: (Boolean) -> Unit = {}
){
    val state by viewModel.state.collectAsState()
    val permissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val context = LocalContext.current
    val luminosidad by lightSensor()

    LaunchedEffect(luminosidad) {
        viewModel.updateLuminosidad(luminosidad)
    }

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

    val onAddMarker = { latLng: LatLng ->
        viewModel.onAddMarker(latLng, context)
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {innerPadding ->
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
                val strPermisoDenegado = stringResource(R.string.permiso_denegado)
                val strErrorObtenerUbicacion = stringResource(R.string.error_al_obtener_ubicacion)

                LaunchedEffect(state.checkedFollowUser) {
                    if(!state.checkedFollowUser){
                        try{
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                viewModel.setFirstLocation(loc)
                            }
                        }catch (e: SecurityException){
                            Log.e(strLocation, strPermisoDenegado, e)
                        }catch (e: Exception){
                            Log.e(strLocation, strErrorObtenerUbicacion, e)
                        }
                    }
                }
                DisposableEffect(state.checkedFollowUser) {
                    if(!state.checkedFollowUser) return@DisposableEffect onDispose {}
                    val locationRequest =
                        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
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

                Config(
                    checkedFollowUser = state.checkedFollowUser,
                    onCheckedChange = { viewModel.onFollowUserChanged(it) },
                    directionText = state.directionText,
                    onDirectionChange = { viewModel.onDirectionTextChanged(it, context) }
                )
                Map(
                    currentLocation = state.location,
                    directionText = state.directionText,
                    locationPoints = state.locationPoints,
                    searchedAddress = state.searchedAddress,
                    customMarkers = state.customMarkers,
                    routePoints = state.routePoints,
                    luminosidad = luminosidad,
                    followUser = state.checkedFollowUser,
                    onAddMarker = onAddMarker,
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
    directionText: String,
    locationPoints: List<LatLng>,
    searchedAddress: LatLng?,
    customMarkers: List<CustomMarker>,
    routePoints: List<LatLng>,
    luminosidad: Float,
    followUser: Boolean,
    onAddMarker: (LatLng) -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
){
    val latLng = if(currentLocation != null){
        LatLng(currentLocation.latitude, currentLocation.longitude)
    }else{//default
        LatLng(4.628829, -74.063589)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 10f)
    }
    val markerState = rememberMarkerState()
    val searchedMarkerState = rememberMarkerState()
    LaunchedEffect(currentLocation, followUser) {
        if(currentLocation != null){
            val newLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            markerState.position = newLatLng
            if(followUser){
                cameraPositionState.position =
                    CameraPosition.
                    fromLatLngZoom(
                        newLatLng,
                        15f
                    )
            }
        }
    }

    LaunchedEffect(searchedAddress) {
        if(searchedAddress != null){
            searchedMarkerState.position = searchedAddress
            cameraPositionState.position = CameraPosition.fromLatLngZoom(searchedAddress,15f)
        }
    }

    var mapaOscuro by remember { mutableStateOf(false) }

    LaunchedEffect(luminosidad) {
        if(luminosidad < 10f && !mapaOscuro){
            mapaOscuro = true
        }else if(luminosidad > 10f && mapaOscuro){
            mapaOscuro = false
        }
    }

    val mapId = if(!mapaOscuro){
        stringResource(R.string.id_mapa_claro)
    }
    else{
        stringResource(R.string.id_mapa_oscuro)
    }

    key(mapId){
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId(mapId)
            },
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                mapToolbarEnabled = false,
                compassEnabled = false
            ),
            onMapLongClick = {latLng ->
                onAddMarker(latLng)
            }
        )
        {
            if(locationPoints.size > 1 && followUser){
                Polyline(
                    points = locationPoints,
                    clickable = true,
                    color = Color(android.graphics.Color.parseColor(stringResource(R.string.polyline_color))),
                    width = 12f,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    geodesic = true
                )
            }

            if(routePoints.isNotEmpty()){
                Polyline(
                    points = routePoints,
                    clickable = true,
                    color = Color.Green,
                    width = 15f,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    geodesic = true
                )
            }
            Marker (
                state = markerState,
                title = stringResource(R.string.ubicaci_n_actual),
                snippet = stringResource(R.string.tu_ubicaci_n)
            )

            if(searchedAddress != null){
                Marker(
                    state = searchedMarkerState,
                    title = stringResource(R.string.direccion),
                    snippet = directionText
                )
            }

            customMarkers.forEach { customMarker ->
                AdvancedMarker(
                    state = rememberMarkerState(position = customMarker.position),
                    title = customMarker.title,
                    snippet = customMarker.snippet,
                    pinConfig = PinConfig.builder()
                        .setBackgroundColor(customMarker.color.toInt())
                        .setBorderColor(android.graphics.Color.WHITE)
                        .build()
                )
            }
        }
    }
}

@Composable
fun Config(
    checkedFollowUser: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    directionText: String,
    onDirectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(
                    R.string.mover_el_mapa_con_la_posici_n_del_usuario
                ),
                modifier = Modifier.padding(end = 8.dp)
            )

            Switch(
                checked = checkedFollowUser,
                onCheckedChange = onCheckedChange
            )
        }

        OutlinedTextField(
            value = directionText,
            onValueChange = onDirectionChange,
            modifier = Modifier.fillMaxWidth(),
            label = {Text(stringResource(R.string.direccion))},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = stringResource(R.string.icono_direccion)
                )
            }
        )
    }
}
