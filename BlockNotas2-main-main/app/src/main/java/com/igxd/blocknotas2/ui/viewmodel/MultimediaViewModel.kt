package com.igxd.blocknotas2.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import android.net.Uri
import java.io.File

class MultimediaViewModel : ViewModel() {
    // Lista mutable para almacenar los elementos multimedia
    val multimediaItems = mutableStateListOf<MultimediaItem>()

    // Método para agregar un nuevo elemento multimedia
    fun addMultimediaItem(item: MultimediaItem) {
        multimediaItems.add(item)
    }

    // Método para eliminar un elemento multimedia
    fun removeMultimediaItem(item: MultimediaItem) {
        multimediaItems.remove(item)
    }
}

// Clase sellada que representa los diferentes tipos de elementos multimedia
sealed class MultimediaItem {




    // Para las imágenes, almacenamos un archivo
    data class Image(val file: File) : MultimediaItem()

    // Para los videos, almacenamos la URI del archivo
    data class Video(val uri: Uri) : MultimediaItem()

    // Para los audios, almacenamos la URI como String
    data class Audio(val uri: String) : MultimediaItem()
}
