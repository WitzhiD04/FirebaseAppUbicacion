package com.example.tallerfirebase.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.UserData
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

@OptIn(ExperimentalMaterial3Api::class)
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { onePhotoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = state.fotoUri ?: user.fotoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
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
                etiqueta = "Nueva contraseña (opcional)"
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
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(stringResource(id = R.string.boton_cancelar))
                }
                
                BotonPersonalizado(
                    texto = stringResource(id = R.string.boton_guardar),
                    alHacerClick = {
                        alGuardarCambios(viewModel.updates, state.fotoUri)
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
        }
    }
}
