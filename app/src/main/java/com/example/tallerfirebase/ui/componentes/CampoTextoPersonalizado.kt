package com.example.tallerfirebase.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tallerfirebase.ui.theme.BordeActivo
import com.example.tallerfirebase.ui.theme.BordeSutil
import com.example.tallerfirebase.ui.theme.ColorError
import com.example.tallerfirebase.ui.theme.FondoInput
import com.example.tallerfirebase.ui.theme.TextoDeshabilitado
import com.example.tallerfirebase.ui.theme.TextoPrimario
import com.example.tallerfirebase.ui.theme.TextoSecundario

@Composable
fun CampoTextoPersonalizado(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modificador: Modifier = Modifier,
    esError: Boolean = false,
    mensajeError: String = "",
    transformacionVisual: VisualTransformation = VisualTransformation.None,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default,
    accionesTeclado: KeyboardActions = KeyboardActions.Default,
    iconoFinal: @Composable (() -> Unit)? = null,
    habilitado: Boolean = true,
    lineaUnica: Boolean = true
) {
    val fuenteInteraccion = remember { MutableInteractionSource() }
    val estaEnfocado by fuenteInteraccion.collectIsFocusedAsState()

    val colorBorde by animateColorAsState(
        targetValue = when {
            esError -> ColorError
            estaEnfocado -> BordeActivo
            else -> BordeSutil
        },
        animationSpec = tween(durationMillis = 250),
        label = "animacionColorBorde"
    )

    val anchoBorde by animateDpAsState(
        targetValue = if (estaEnfocado || esError) 1.5.dp else 1.dp,
        animationSpec = tween(durationMillis = 250),
        label = "animacionAnchoBorde"
    )

    Column(modifier = modificador) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = anchoBorde,
                    color = colorBorde,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            TextField(
                value = valor,
                onValueChange = alCambiarValor,
                label = {
                    Text(
                        text = etiqueta,
                        color = if (estaEnfocado) BordeActivo else TextoSecundario
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icono,
                        contentDescription = etiqueta,
                        tint = if (estaEnfocado) BordeActivo else TextoSecundario
                    )
                },
                trailingIcon = iconoFinal,
                visualTransformation = transformacionVisual,
                keyboardOptions = opcionesTeclado,
                keyboardActions = accionesTeclado,
                singleLine = lineaUnica,
                enabled = habilitado,
                isError = esError,
                interactionSource = fuenteInteraccion,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextoPrimario,
                    unfocusedTextColor = TextoPrimario,
                    disabledTextColor = TextoDeshabilitado,
                    errorTextColor = TextoPrimario,
                    focusedContainerColor = FondoInput,
                    unfocusedContainerColor = FondoInput,
                    disabledContainerColor = FondoInput.copy(alpha = 0.5f),
                    errorContainerColor = FondoInput,
                    cursorColor = BordeActivo,
                    errorCursorColor = ColorError,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedLabelColor = BordeActivo,
                    unfocusedLabelColor = TextoSecundario,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (esError && mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = ColorError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
