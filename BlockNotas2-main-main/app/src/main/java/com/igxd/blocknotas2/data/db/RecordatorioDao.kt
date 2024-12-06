package com.igxd.blocknotas2.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.igxd.blocknotas2.data.models.Recordatorio

@Dao
interface RecordatorioDao {

    @Insert
    suspend fun insertar(recordatorio: Recordatorio)

    @Update
    suspend fun actualizar(recordatorio: Recordatorio)

    @Delete
    suspend fun eliminar(recordatorio: Recordatorio)

    @Query("SELECT * FROM recordatorios")
    suspend fun obtenerTodos(): List<Recordatorio>

    @Query("SELECT * FROM recordatorios WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Recordatorio?

    @Query("SELECT * FROM recordatorios WHERE id = :notaId")
    suspend fun getRecordatoriosDeNota(notaId: Long): List<Recordatorio>
    abstract fun obtenerRecordatoriosDeNota(notaId: Long): List<Recordatorio>
}
