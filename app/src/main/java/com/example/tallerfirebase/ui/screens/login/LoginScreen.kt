package com.example.tallerfirebase.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerfirebase.R
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

/**
 * Pantalla de inicio de sesión de la aplicación.
 */
@Composable
fun LoginScreen(
    alIniciarSesion: (String, String) -> Unit,
    alIrARegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titulo_inicio_sesion),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            CampoTextoPersonalizado(
                valor = correo,
                alCambiarValor = { correo = it },
                etiqueta = stringResource(id = R.string.hint_correo)
            )

            CampoContrasenaPersonalizado(
                valor = contrasena,
                alCambiarValor = { contrasena = it },
                etiqueta = stringResource(id = R.string.hint_contrasena)
            )

            BotonPersonalizado(
                texto = stringResource(id = R.string.boton_iniciar_sesion),
                alHacerClick = { alIniciarSesion(correo, contrasena) }
            )

            TextButton(onClick = alIrARegistro) {
                Text(
                    text = stringResource(id = R.string.texto_ir_a_registro),
                    fontSize = 14.sp
                )
            }
        }
    }
}
