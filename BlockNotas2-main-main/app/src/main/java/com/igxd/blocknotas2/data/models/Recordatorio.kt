package com.igxd.blocknotas2.data.models

import androidx.room.*

@Entity(tableName = "recordatorios")
data class Recordatorio(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val titulo: String,
    val contenido: String,
    val fechaHora: Long, // Fecha y hora en milisegundos
    val mensaje: String

) {

}
