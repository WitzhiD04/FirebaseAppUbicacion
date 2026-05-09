package com.example.tallerfirebase.ui.screens.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.AuthState
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onClickRegister: (String, String, String, String, String, Uri?, android.content.Context) -> Unit = { _, _, _, _, _, _, _ -> },
    alVolverAtras: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
    authState: AuthState = AuthState.noAutenticado
) {
    val state by viewModel.state
    val context = LocalContext.current
    val onePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.seleccionarFoto(it, context) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.titulo_registro)) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ingresa tus datos",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Start)
            )

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
                valor = state.email,
                alCambiarValor = { viewModel.onEmailChange(it) },
                etiqueta = stringResource(id = R.string.hint_correo),
                tipoTeclado = KeyboardType.Email
            )

            CampoContrasenaPersonalizado(
                valor = state.password,
                alCambiarValor = { viewModel.onPasswordChange(it) },
                etiqueta = stringResource(id = R.string.hint_contrasena)
            )

            CampoTextoPersonalizado(
                valor = state.telefono,
                alCambiarValor = { viewModel.onTelefonoChange(it) },
                etiqueta = stringResource(id = R.string.hint_telefono),
                tipoTeclado = KeyboardType.Phone
            )

            OutlinedButton(
                onClick = { onePhotoPickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = stringResource(R.string.seleccionar_foto_de_perfil))
            }

            if (state.fotoUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = state.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (authState is AuthState.Error) {
                Text(
                    text = authState.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (authState is AuthState.cargando) {
                CircularProgressIndicator()
            } else {
                BotonPersonalizado(
                    texto = stringResource(id = R.string.boton_registrarse),
                    alHacerClick = {
                        onClickRegister(
                            state.nombre,
                            state.email,
                            state.password,
                            state.identificacion,
                            state.telefono,
                            state.fotoUri,
                            context
                        )
                    },
                    habilitado = state.nombre.isNotBlank() && state.email.isNotBlank() && 
                                 state.password.isNotBlank() && state.identificacion.isNotBlank() && 
                                 state.telefono.isNotBlank()
                )
            }
        }
    }
}
