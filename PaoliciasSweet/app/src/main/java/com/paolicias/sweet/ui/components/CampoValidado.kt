package com.paolicias.sweet.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

val VerdeValido = Color(0xFF2E7D32)

/**
 * Campo de texto con validacion visual: el borde cambia de color con animacion,
 * muestra un icono de error o de valido y el mensaje de error aparece animado.
 * No valida por su cuenta: recibe el error ya calculado por el ViewModel.
 */
@Composable
fun CampoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    mostrarValido: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val valido = mostrarValido && error == null && valor.isNotBlank()

    val colorFoco by animateColorAsState(
        targetValue = when {
            error != null -> MaterialTheme.colorScheme.error
            valido -> VerdeValido
            else -> MaterialTheme.colorScheme.primary
        },
        label = "colorFoco"
    )
    val colorReposo by animateColorAsState(
        targetValue = when {
            error != null -> MaterialTheme.colorScheme.error
            valido -> VerdeValido
            else -> MaterialTheme.colorScheme.outline
        },
        label = "colorReposo"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                if (trailingIcon != null) trailingIcon() else IconoEstado(error = error, valido = valido)
            },
            isError = error != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorFoco,
                unfocusedBorderColor = colorReposo,
                focusedLeadingIconColor = colorFoco,
                unfocusedLeadingIconColor = colorReposo,
                focusedLabelColor = colorFoco
            )
        )
        MensajeError(error = error)
    }
}

/**
 * Selector que se abre con un toque (fecha, por ejemplo), con la misma
 * validacion visual que CampoValidado.
 */
@Composable
fun CampoSelector(
    etiqueta: String,
    valor: String,
    icono: ImageVector,
    error: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val valido = error == null && valor.isNotBlank()
    val color by animateColorAsState(
        targetValue = when {
            error != null -> MaterialTheme.colorScheme.error
            valido -> VerdeValido
            else -> MaterialTheme.colorScheme.outline
        },
        label = "colorSelector"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(if (error != null) 2.dp else 1.dp, color)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icono, contentDescription = null, tint = color)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
                    Text(
                        text = valor.ifBlank { "Toca para seleccionar" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (valor.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconoEstado(error = error, valido = valido)
            }
        }
        MensajeError(error = error)
    }
}

/** Icono que cambia con transicion entre error, valido y neutro. */
@Composable
fun IconoEstado(error: String?, valido: Boolean) {
    val estado = when {
        error != null -> 1
        valido -> 2
        else -> 0
    }
    Crossfade(targetState = estado, label = "iconoEstado") { actual ->
        when (actual) {
            1 -> Icon(Icons.Filled.Error, contentDescription = "Campo con error", tint = MaterialTheme.colorScheme.error)
            2 -> Icon(Icons.Filled.CheckCircle, contentDescription = "Campo valido", tint = VerdeValido)
            else -> Spacer(Modifier.size(0.dp))
        }
    }
}

/** Mensaje de error con icono que aparece y desaparece con animacion. */
@Composable
fun MensajeError(error: String?, modifier: Modifier = Modifier) {
    var ultimoError by remember { mutableStateOf("") }
    if (error != null) ultimoError = error

    AnimatedVisibility(
        visible = error != null,
        modifier = modifier,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(ultimoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
