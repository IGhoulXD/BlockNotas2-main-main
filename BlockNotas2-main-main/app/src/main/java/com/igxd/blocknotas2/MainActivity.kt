package com.igxd.blocknotas2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.igxd.blocknotas2.ui.MultimediaScreen
import com.igxd.blocknotas2.ui.NotaScreen


import com.igxd.blocknotas2.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = viewModel()  // Obtenemos el themeViewModel
    val context = LocalContext.current // Obtener el contexto adecuado en un Composable

    NavHost(navController = navController, startDestination = "notas") {
        composable("notas") {
            NotaScreen(navController = navController, themeViewModel = themeViewModel,)
            // Pasamos el themeViewModel a NotaScreen
        }
        composable("multimedia") {
            MultimediaScreen(navController = navController, themeViewModel = themeViewModel)  // Pasamos el themeViewModel a MultimediaScreen
        }
    }
}
