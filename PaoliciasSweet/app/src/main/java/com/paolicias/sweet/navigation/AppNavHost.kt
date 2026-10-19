package com.paolicias.sweet.navigation

import androidx.compose.runtime.Composable
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
import com.paolicias.sweet.util.Sesion
import com.paolicias.sweet.viewmodel.EventoViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    // Compartido entre ConfigurarEvento y Resumen (vive a nivel de la Activity)
    val eventoViewModel: EventoViewModel = viewModel()

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
                onNavigateNext = { navController.navigate(Rutas.AGENDA) }
            )
        }
        composable(Rutas.AGENDA) {
            AgendaScreen(
                onNavigateNext = { cerrarSesion() }
            )
        }
    }
}
