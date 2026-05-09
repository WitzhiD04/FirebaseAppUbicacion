package com.example.tallerfirebase.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tallerfirebase.R
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

/**
 * Pantalla de registro de nuevos usuarios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    alRegistrarse: (String, String, String, String, String) -> Unit,
    alVolverAtras: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var identificacion by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

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
                valor = correo,
                alCambiarValor = { correo = it },
                etiqueta = stringResource(id = R.string.hint_correo),
                tipoTeclado = KeyboardType.Email
            )

            CampoContrasenaPersonalizado(
                valor = contrasena,
                alCambiarValor = { contrasena = it },
                etiqueta = stringResource(id = R.string.hint_contrasena)
            )

            CampoTextoPersonalizado(
                valor = telefono,
                alCambiarValor = { telefono = it },
                etiqueta = stringResource(id = R.string.hint_telefono),
                tipoTeclado = KeyboardType.Phone
            )

            BotonPersonalizado(
                texto = stringResource(id = R.string.boton_registrarse),
                alHacerClick = {
                    alRegistrarse(nombre, identificacion, correo, contrasena, telefono)
                }
            )
        }
    }
}
