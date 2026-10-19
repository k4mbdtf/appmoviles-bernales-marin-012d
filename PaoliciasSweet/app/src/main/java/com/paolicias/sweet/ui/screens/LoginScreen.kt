package com.paolicias.sweet.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paolicias.sweet.navigation.Rutas
import com.paolicias.sweet.ui.components.CampoValidado

@Composable
fun LoginScreen(
    onNavigateNext: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("Camila Rojas") }
    var email by remember { mutableStateOf("camila.rojas@eventosatacama.cl") }
    var telefono by remember { mutableStateOf("+56987654321") }
    var password by remember { mutableStateOf("paolicias2026") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }

    val tieneErrores = errorNombre != null || errorEmail != null || errorTelefono != null || errorPassword != null
    val colorBoton by animateColorAsState(
        targetValue = if (tieneErrores) Color(0xFFC62828) else MaterialTheme.colorScheme.primary,
        label = "colorBotonLogin"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Paolicias.Sweet",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Banquetería Corporativa y Pastelería Fina",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Acceso y Registro de Cliente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CampoValidado(
                        valor = nombre,
                        onCambio = {
                            nombre = it
                            errorNombre = if (it.isBlank()) "El nombre es obligatorio" else null
                        },
                        etiqueta = "Nombre Completo",
                        error = errorNombre,
                        icono = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CampoValidado(
                        valor = email,
                        onCambio = {
                            email = it
                            errorEmail = if (!it.contains("@")) "Ingresa un email válido" else null
                        },
                        etiqueta = "Correo Electrónico",
                        error = errorEmail,
                        icono = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CampoValidado(
                        valor = telefono,
                        onCambio = {
                            telefono = it
                            errorTelefono = if (it.length < 8) "Teléfono inválido (mínimo 8 dígitos)" else null
                        },
                        etiqueta = "Teléfono de Contacto",
                        error = errorTelefono,
                        icono = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CampoValidado(
                        valor = password,
                        onCambio = {
                            password = it
                            errorPassword = if (it.length < 6) "La contraseña debe tener al menos 6 caracteres" else null
                        },
                        etiqueta = "Contraseña",
                        error = errorPassword,
                        icono = Icons.Default.Lock,
                        esPassword = true,
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
                            errorEmail = if (!email.contains("@")) "Ingresa un email válido" else null
                            errorTelefono = if (telefono.length < 8) "Teléfono inválido (mínimo 8 dígitos)" else null
                            errorPassword = if (password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null

                            if (errorNombre == null && errorEmail == null && errorTelefono == null && errorPassword == null) {
                                onNavigateNext()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (tieneErrores) "Revisa los campos marcados" else "Ingresar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    LoginScreen(onNavigateNext = { navController.navigate(Rutas.CATALOGO) })
}
