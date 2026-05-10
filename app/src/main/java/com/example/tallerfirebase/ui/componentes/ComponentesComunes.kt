package com.example.tallerfirebase.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CampoTextoPersonalizado(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = true
    )
}


@Composable
fun CampoContrasenaPersonalizado(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    var mostrarContrasena by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
        visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val imagen = if (mostrarContrasena) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val descripcion = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"

            IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                Icon(imageVector = imagen, contentDescription = descripcion)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = true
    )
}

@Composable
fun BotonPersonalizado(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = alHacerClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        enabled = habilitado
    ) {
        Text(texto)
    }
}
