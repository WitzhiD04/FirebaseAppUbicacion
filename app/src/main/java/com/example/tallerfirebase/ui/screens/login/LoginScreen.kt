package com.example.tallerfirebase.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tallerfirebase.R
import com.example.tallerfirebase.modelo.AuthState
import com.example.tallerfirebase.ui.componentes.BotonPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoContrasenaPersonalizado
import com.example.tallerfirebase.ui.componentes.CampoTextoPersonalizado

@Composable
fun LoginScreen(
    onClickLogin: (String, String) -> Unit = { _, _ -> },
    alIrARegistro: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    authState: AuthState = AuthState.noAutenticado,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
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
                valor = state.email,
                alCambiarValor = { viewModel.onEmailChange(it) },
                etiqueta = stringResource(id = R.string.hint_correo)
            )

            CampoContrasenaPersonalizado(
                valor = state.password,
                alCambiarValor = { viewModel.onPasswordChange(it) },
                etiqueta = stringResource(id = R.string.hint_contrasena)
            )

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
                    texto = stringResource(id = R.string.boton_iniciar_sesion),
                    alHacerClick = { onClickLogin(state.email, state.password) }
                )
            }

            TextButton(
                onClick = alIrARegistro,
                enabled = authState !is AuthState.cargando
            ) {
                Text(
                    text = stringResource(id = R.string.texto_ir_a_registro),
                    fontSize = 14.sp
                )
            }
        }
    }
}
