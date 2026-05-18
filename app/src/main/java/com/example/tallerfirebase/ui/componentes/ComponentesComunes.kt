package com.example.tallerfirebase.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.tallerfirebase.ui.theme.AcentoPrincipalClaro
import com.example.tallerfirebase.ui.theme.FondoMarcadores
import com.example.tallerfirebase.ui.theme.TextoPrimario
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState

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
            .fillMaxWidth(),
        enabled = habilitado
    ) {
        Text(texto)
    }
}

@Composable
fun CustomMapMarker(
    imageUrl: String?,
    fullName: String,
    location: LatLng,
    onClick: () -> Unit
){
    val markerState = rememberMarkerState(position = location)
    LaunchedEffect(location) {
        markerState.position = location
    }
    val shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp)
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .build()
    )
    var expandMarker by remember { mutableStateOf(false) }

    MarkerComposable(
        keys = arrayOf(fullName, painter.state, expandMarker),
        state = markerState,
        title = fullName,
        anchor = Offset(0.5f, 1f),
        onClick = {
            onClick()
            expandMarker = !expandMarker
            true
        }
    ) {
        val snippet = "Online 🟢"
        Box(
            modifier = Modifier
                .size(if (expandMarker) 100.dp else 48.dp)
                .clip(shape)
                .background(AcentoPrincipalClaro)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ){
            if(!imageUrl.isNullOrEmpty()){
                if(!expandMarker){
                    Image(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }else{
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize().weight(1f).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = TextoPrimario
                        )
                        Text(
                            text = snippet,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color.Green
                        )
                    }
                }
            }else{
                Text(
                    text = fullName.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}