package com.example.tallerfirebase.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerfirebase.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    alEditarPerfil: () -> Unit,
    alVolverAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nombreUsuario = "Juan Pérez"
    val correoUsuario = "juan.perez@example.com"
    val telefonoUsuario = "3001234567"
    val identificacionUsuario = "123456789"

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.menu_modificar_perfil)) },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = alEditarPerfil) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Perfil")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de Perfil
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoItem(etiqueta = "Nombre", valor = nombreUsuario)
            InfoItem(etiqueta = "Identificación", valor = identificacionUsuario)
            InfoItem(etiqueta = "Correo", valor = correoUsuario)
            InfoItem(etiqueta = "Teléfono", valor = telefonoUsuario)
        }

        Button(
            onClick = alVolverAtras //Cambiar despues
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}

@Composable
fun InfoItem(etiqueta: String, valor: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
    }
}
