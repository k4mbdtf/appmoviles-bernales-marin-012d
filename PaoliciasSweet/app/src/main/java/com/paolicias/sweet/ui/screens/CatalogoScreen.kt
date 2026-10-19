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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paolicias.sweet.navigation.Rutas

data class ProductoCatalogo(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: String,
    val esPasteleria: Boolean
)

private val listaProductosEjemplo = listOf(
    ProductoCatalogo(
        id = 1,
        nombre = "Torta Personalizada Fondant 30 Porciones",
        categoria = "Pastelería Fina",
        precio = "$65.000 CLP",
        esPasteleria = true
    ),
    ProductoCatalogo(
        id = 2,
        nombre = "Coffee Break Ejecutivo Corporativo",
        categoria = "Banquetería Corporativa",
        precio = "$8.500 CLP / persona",
        esPasteleria = false
    ),
    ProductoCatalogo(
        id = 3,
        nombre = "Cóctel Gourmet Lanzamiento & Seminarios",
        categoria = "Banquetería Corporativa",
        precio = "$12.900 CLP / persona",
        esPasteleria = false
    ),
    ProductoCatalogo(
        id = 4,
        nombre = "Mesa Dulce & Mini Pastelería Artesanal",
        categoria = "Pastelería Fina",
        precio = "$9.500 CLP / persona",
        esPasteleria = true
    ),
    ProductoCatalogo(
        id = 5,
        nombre = "Torta Milhojas Manjar Nuez Vallenar",
        categoria = "Pastelería Fina",
        precio = "$48.000 CLP",
        esPasteleria = true
    ),
    ProductoCatalogo(
        id = 6,
        nombre = "Banquetería Integral Celebración Institucional",
        categoria = "Banquetería Corporativa",
        precio = "$18.500 CLP / persona",
        esPasteleria = false
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

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (producto.esPasteleria) Icons.Default.Cake else Icons.Default.RestaurantMenu,
                                    contentDescription = producto.nombre,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = producto.categoria,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = producto.precio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(onClick = onNavigateNext) {
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
