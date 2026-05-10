package com.example.tallerfirebase.ui.screens.profile

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tallerfirebase.R
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    alGuardarCambios: (String, String, String, String) -> Unit,
    alVolverAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estos estados deberían inicializarse con los datos del usuario real (desde el ViewModel)
    var nombre by remember { mutableStateOf("") }
    var identificacion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

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
                valor = nombre,
                alCambiarValor = { nombre = it },
                etiqueta = stringResource(id = R.string.hint_nombre)
            )

            CampoTextoPersonalizado(
                valor = identificacion,
                alCambiarValor = { identificacion = it },
                etiqueta = stringResource(id = R.string.hint_identificacion),
                tipoTeclado = KeyboardType.Number
            )

            CampoTextoPersonalizado(
                valor = telefono,
                alCambiarValor = { telefono = it },
                etiqueta = stringResource(id = R.string.hint_telefono),
                tipoTeclado = KeyboardType.Phone
            )

            CampoContrasenaPersonalizado(
                valor = contrasena,
                alCambiarValor = { contrasena = it },
                etiqueta = stringResource(id = R.string.hint_contrasena)
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
                        alGuardarCambios(nombre, identificacion, telefono, contrasena)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
