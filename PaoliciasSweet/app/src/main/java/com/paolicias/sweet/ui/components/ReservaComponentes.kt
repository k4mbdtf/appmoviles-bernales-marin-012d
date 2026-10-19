package com.paolicias.sweet.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.paolicias.sweet.data.repository.EstadoAbono
import com.paolicias.sweet.util.Formato
import java.io.File
import java.time.LocalDate

/** Etiqueta de estado del abono; el color cambia con animacion. */
@Composable
fun EstadoChip(estado: String) {
    val texto = when (estado) {
        EstadoAbono.ABONO_50 -> "Abono validado"
        EstadoAbono.COMPLETADO -> "Pagado"
        else -> "Pendiente de abono"
    }
    val colorObjetivo = when (estado) {
        EstadoAbono.ABONO_50 -> VerdeValido
        EstadoAbono.COMPLETADO -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
    val color by animateColorAsState(targetValue = colorObjetivo, label = "colorEstado")

    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
        Text(
            texto,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Miniatura del comprobante guardado en almacenamiento interno. */
@Composable
fun ComprobanteMiniatura(ruta: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = File(ruta),
        contentDescription = "Comprobante de abono",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

/** "2026-11-20" -> "20/11/2026" */
fun fechaLegible(iso: String): String =
    runCatching { Formato.fecha(LocalDate.parse(iso)) }.getOrDefault(iso)
