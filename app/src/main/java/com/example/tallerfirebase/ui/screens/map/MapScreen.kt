package com.example.tallerfirebase.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tallerfirebase.R

/**
 * Pantalla principal que muestra el mapa y permite controlar el estado de conexión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    alEditarPerfil: () -> Unit,
    alCerrarSesion: () -> Unit,
    alCambiarConexion: (Boolean) -> Unit
) {
    var conectado by remember { mutableStateOf(false) }
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (conectado) 
                                stringResource(id = R.string.estado_conectado) 
                            else 
                                stringResource(id = R.string.estado_desconectado),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = conectado,
                            onCheckedChange = {
                                conectado = it
                                alCambiarConexion(it)
                            }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menu_modificar_perfil)) },
                            onClick = {
                                menuExpandido = false
                                alEditarPerfil()
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menu_cerrar_sesion)) },
                            onClick = {
                                menuExpandido = false
                                alCerrarSesion()
                            },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder para el Mapa
            Text(
                text = stringResource(id = R.string.placeholder_mapa),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.DarkGray
            )
        }
    }
}
