package com.paolicias.sweet.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paolicias.sweet.data.repository.Rol
import com.paolicias.sweet.ui.screens.AgendaScreen
import com.paolicias.sweet.ui.screens.CatalogoScreen
import com.paolicias.sweet.ui.screens.ConfigurarEventoScreen
import com.paolicias.sweet.ui.screens.LoginScreen
import com.paolicias.sweet.ui.screens.MisReservasScreen
import com.paolicias.sweet.ui.screens.ResumenScreen
import com.paolicias.sweet.util.Formato
import com.paolicias.sweet.util.Notificaciones
import com.paolicias.sweet.util.Sesion
import com.paolicias.sweet.viewmodel.EventoViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Compartido entre ConfigurarEvento y Resumen (vive a nivel de la Activity)
    val eventoViewModel: EventoViewModel = viewModel()

    // Permiso de notificaciones en tiempo de ejecucion (Android 13+)
    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Si lo niega, la app sigue funcionando sin avisos */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Notificaciones.tienePermiso(context)) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun cerrarSesion() {
        Sesion.cerrar()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { rol ->
                    val destino = if (rol == Rol.ADMIN) Rutas.AGENDA else Rutas.CATALOGO
                    navController.navigate(destino) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.CATALOGO) {
            CatalogoScreen(
                onCotizarEvento = {
                    eventoViewModel.reiniciar()
                    navController.navigate(Rutas.CONFIGURAR_EVENTO)
                },
                onMisReservas = { navController.navigate(Rutas.MIS_RESERVAS) },
                onCerrarSesion = { cerrarSesion() }
            )
        }
        composable(Rutas.CONFIGURAR_EVENTO) {
            ConfigurarEventoScreen(
                viewModel = eventoViewModel,
                onVolver = { navController.popBackStack() },
                onReservaGuardada = {
                    val estado = eventoViewModel.uiState.value
                    estado.eventoGuardadoId?.let { id ->
                        Notificaciones.nuevaReserva(
                            context = context,
                            eventoId = id,
                            fecha = estado.fecha?.let { Formato.fecha(it) } ?: "",
                            total = estado.total
                        )
                    }
                    navController.navigate(Rutas.RESUMEN) {
                        popUpTo(Rutas.CATALOGO)
                    }
                }
            )
        }
        composable(Rutas.RESUMEN) {
            ResumenScreen(
                viewModel = eventoViewModel,
                onVerMisReservas = {
                    navController.navigate(Rutas.MIS_RESERVAS) {
                        popUpTo(Rutas.CATALOGO)
                    }
                },
                onVolverCatalogo = { navController.popBackStack(Rutas.CATALOGO, inclusive = false) }
            )
        }
        composable(Rutas.MIS_RESERVAS) {
            MisReservasScreen(
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.AGENDA) {
            AgendaScreen(
                onCerrarSesion = { cerrarSesion() }
            )
        }
    }
}
