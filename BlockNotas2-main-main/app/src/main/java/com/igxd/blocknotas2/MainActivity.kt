package com.igxd.blocknotas2

import NotaScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.igxd.blocknotas2.ui.MultimediaScreen
import com.igxd.blocknotas2.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = viewModel()  // Obtenemos el themeViewModel

    NavHost(navController = navController, startDestination = "notas") {
        composable("notas") {
            NotaScreen(navController = navController, themeViewModel = themeViewModel)  // Pasamos el themeViewModel a NotaScreen
        }
        composable("multimedia") {
            MultimediaScreen(navController = navController, themeViewModel = themeViewModel)  // Pasamos el themeViewModel a MultimediaScreen
        }
    }
}
