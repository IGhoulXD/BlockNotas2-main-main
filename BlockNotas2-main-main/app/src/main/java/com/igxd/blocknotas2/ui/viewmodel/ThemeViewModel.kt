package com.igxd.blocknotas2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    // Estado del tema: oscuro o claro
    var isDarkTheme by mutableStateOf(false)
        private set

    // Función para cambiar el estado del tema
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

}
