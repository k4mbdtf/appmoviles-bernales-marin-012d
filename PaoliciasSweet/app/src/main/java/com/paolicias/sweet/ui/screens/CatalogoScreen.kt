package com.paolicias.sweet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.paolicias.sweet.navigation.Rutas

data class ProductoCatalogo(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: String,
    val imagenUrl: String
)

private val listaProductosEjemplo = listOf(
    ProductoCatalogo(
        id = 1,
        nombre = "Torta Personalizada Fondant 30 Porciones",
        categoria = "Pastelería Fina",
        precio = "$65.000 CLP",
        imagenUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600"
    ),
    ProductoCatalogo(
        id = 2,
        nombre = "Coffee Break Ejecutivo Corporativo",
        categoria = "Banquetería Corporativa",
        precio = "$8.500 CLP / persona",
        imagenUrl = "https://images.unsplash.com/photo-1555244162-803834f70033?w=600"
    ),
    ProductoCatalogo(
        id = 3,
        nombre = "Cóctel Gourmet Lanzamiento & Seminarios",
        categoria = "Banquetería Corporativa",
        precio = "$12.900 CLP / persona",
        imagenUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"
    ),
    ProductoCatalogo(
        id = 4,
        nombre = "Mesa Dulce & Mini Pastelería Artesanal",
        categoria = "Pastelería Fina",
        precio = "$9.500 CLP / persona",
        imagenUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=600"
    ),
    ProductoCatalogo(
        id = 5,
        nombre = "Torta Milhojas Manjar Nuez Vallenar",
        categoria = "Pastelería Fina",
        precio = "$48.000 CLP",
        imagenUrl = "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=600"
    ),
    ProductoCatalogo(
        id = 6,
        nombre = "Banquetería Integral Celebración Institucional",
        categoria = "Banquetería Corporativa",
        precio = "$18.500 CLP / persona",
        imagenUrl = "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=600"
    )
)

@Composable
fun CatalogoScreen(
    onNavigateNext: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Catálogo Paolicias.Sweet",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Selecciona un producto o menú para configurar tu evento (máx. 100 asistentes)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaProductosEjemplo) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                AsyncImage(
                                    model = producto.imagenUrl,
                                    contentDescription = producto.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = producto.categoria,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = producto.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = producto.precio,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onNavigateNext,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Cotizar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogoScreen(navController: NavController) {
    CatalogoScreen(onNavigateNext = { navController.navigate(Rutas.CONFIGURAR_EVENTO) })
}
