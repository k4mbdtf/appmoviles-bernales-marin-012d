package com.paolicias.sweet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paolicias.sweet.ui.screens.AgendaScreen
import com.paolicias.sweet.ui.screens.CatalogoScreen
import com.paolicias.sweet.ui.screens.ConfigurarEventoScreen
import com.paolicias.sweet.ui.screens.LoginScreen
import com.paolicias.sweet.ui.screens.MisReservasScreen
import com.paolicias.sweet.ui.screens.ResumenScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onNavigateNext = { navController.navigate(Rutas.CATALOGO) }
            )
        }
        composable(Rutas.CATALOGO) {
            CatalogoScreen(
                onNavigateNext = { navController.navigate(Rutas.CONFIGURAR_EVENTO) }
            )
        }
        composable(Rutas.CONFIGURAR_EVENTO) {
            ConfigurarEventoScreen(
                onNavigateNext = { navController.navigate(Rutas.RESUMEN) }
            )
        }
        composable(Rutas.RESUMEN) {
            ResumenScreen(
                onNavigateNext = { navController.navigate(Rutas.MIS_RESERVAS) }
            )
        }
        composable(Rutas.MIS_RESERVAS) {
            MisReservasScreen(
                onNavigateNext = { navController.navigate(Rutas.AGENDA) }
            )
        }
        composable(Rutas.AGENDA) {
            AgendaScreen(
                onNavigateNext = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
