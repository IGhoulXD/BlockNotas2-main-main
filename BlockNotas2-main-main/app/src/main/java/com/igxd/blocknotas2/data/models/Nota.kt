package com.igxd.BlockNotas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igxd.blocknotas2.viewmodel.MultimediaItem

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val multimedia: List<MultimediaItem> = emptyList(), // Nuevo campo para multimedia
    val imagenUri: String? = null,
    val recordatorio: Long? = null

)
