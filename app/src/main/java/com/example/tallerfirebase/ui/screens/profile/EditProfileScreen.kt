package com.example.tallerfirebase.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.UserData
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@Composable
fun Camara(
    fotoUri: Uri?,
    hasPermission: Boolean,
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(RoundedCornerShape(70.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(70.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (fotoUri != null && fotoUri != Uri.EMPTY) {
            AsyncImage(
                model = fotoUri,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Sin foto de perfil",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    alGuardarCambios: (Map<String, Any>, Uri?) -> Unit,
    alVolverAtras: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(),
    user: UserData
) {

    LaunchedEffect(key1 = user) {
        viewModel.cargarDatos(user)
    }

    val state by viewModel.state
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarInformacionInicial(user)
    }

    val onePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.seleccionarFoto(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.titulo_editar_perfil)) },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Camara(
                fotoUri = state.fotoUri,
                hasPermission = cameraPermissionState.status.isGranted,
                controller = controller
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.fotoUri == null || state.fotoUri == Uri.EMPTY) {
                    if (cameraPermissionState.status.isGranted) {
                        OutlinedButton(
                            onClick = { viewModel.tomarFoto(controller, context) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.tomar_foto_de_perfil))
                        }
                    }
                    
                    OutlinedButton(
                        onClick = { onePhotoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.seleccionar_foto_de_perfil))
                    }
                } else {
                    Button(
                        onClick = { viewModel.removerFoto() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.borrar_foto_de_perfil))
                    }
                }
            }

            CampoTextoPersonalizado(
                valor = state.nombre,
                alCambiarValor = { viewModel.onNombreChange(it) },
                etiqueta = stringResource(id = R.string.hint_nombre)
            )

            CampoTextoPersonalizado(
                valor = state.identificacion,
                alCambiarValor = { viewModel.onIdentificacionChange(it) },
                etiqueta = stringResource(id = R.string.hint_identificacion),
                tipoTeclado = KeyboardType.Number
            )

            CampoTextoPersonalizado(
                valor = state.telefono,
                alCambiarValor = { viewModel.onTelefonoChange(it) },
                etiqueta = stringResource(id = R.string.hint_telefono),
                tipoTeclado = KeyboardType.Phone
            )
            CampoContrasenaPersonalizado(
                valor = state.contrasena,
                alCambiarValor = { viewModel.onContrasenaChange(it) },
                etiqueta = stringResource(R.string.nueva_contrase_a_opcional)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = alVolverAtras,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(stringResource(id = R.string.boton_cancelar))
                }
                
                BotonPersonalizado(
                    texto = stringResource(id = R.string.boton_guardar),
                    alHacerClick = {
                        alGuardarCambios(viewModel.updates, state.fotoUri)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
            }
        }
    }
}
