package com.igxd.blocknotas2.data.models

import androidx.room.*

@Entity(tableName = "recordatorios")
data class Recordatorio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fechaHora: Long // Representa la fecha y hora del recordatorio

)
