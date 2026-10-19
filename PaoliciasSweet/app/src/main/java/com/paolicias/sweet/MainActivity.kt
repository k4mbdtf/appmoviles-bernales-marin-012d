package com.paolicias.sweet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paolicias.sweet.navigation.AppNavHost
import com.paolicias.sweet.ui.theme.PaoliciasSweetTheme
import com.paolicias.sweet.util.Notificaciones

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notificaciones.crearCanal(this)
        enableEdgeToEdge()
        setContent {
            PaoliciasSweetTheme {
                AppNavHost()
            }
        }
    }
}
