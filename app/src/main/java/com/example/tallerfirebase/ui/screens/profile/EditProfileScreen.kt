package com.example.tallerfirebase.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.UserData
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    alGuardarCambios: (String, String, Uri?, android.content.Context, String, String) -> Unit = { _, _, _, _, _, _ -> },
    alVolverAtras: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(),
    user: UserData
) {

    val state by viewModel.state
    val context = LocalContext.current
    val onePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.seleccionarFoto(it, context) }
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = alVolverAtras,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.boton_cancelar))
                }
                
                BotonPersonalizado(
                    texto = stringResource(id = R.string.boton_guardar),
                    alHacerClick = {
                        alGuardarCambios(state.nombre,user.uid, state.fotoUri, context,state.identificacion, state.telefono)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
