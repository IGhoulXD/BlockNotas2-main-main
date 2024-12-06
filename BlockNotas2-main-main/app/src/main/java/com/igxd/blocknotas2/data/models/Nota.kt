package com.igxd.BlockNotas.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.igxd.blocknotas2.data.models.Recordatorio
import com.igxd.blocknotas2.viewmodel.MultimediaItem
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "notas")
data class Nota(
    val id: Long, // Asegúrate de que 'id' es de tipo Long o String dependiendo de cómo lo manejes
    val titulo: String,
    val contenido: String,
    val multimedia: List<MultimediaItem>,
    val recordatorioFecha: LocalDate?,
    val recordatorioHora: LocalTime?,
    val notaParaEditar: Nothing?,

)


data class MultimediaItem(
    val id: Int,
    val tipo: String, // Puede ser "imagen", "video", etc.
    val uri: String // La URI del archivo multimedia
)

data class NotaConRecordatorios(
    @Embedded val nota: Nota,
    @Relation(
        parentColumn = "id",
        entityColumn = "notaId"
    )
    val recordatorios: List<Recordatorio>
)

