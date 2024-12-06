package com.igxd.BlockNotas.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.igxd.BlockNotas.data.models.Nota
import com.igxd.BlockNotas.data.models.NotaConRecordatorios

@Dao
interface NotaDao {

    @Insert
    suspend fun insertar(nota: Nota)

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)

    @Query("SELECT * FROM notas")
    suspend fun obtenerTodasLasNotas(): List<Nota>


    @Transaction
    @Query("SELECT * FROM notas")
    fun getNotasConRecordatorios(): LiveData<List<NotaConRecordatorios>>
}

