package com.igxd.BlockNotas.data.db

import androidx.room.*
import com.igxd.BlockNotas.data.models.Nota

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(nota: Nota)

    @Query("SELECT * FROM notas")
    suspend fun obtenerTodasLasNotas(): List<Nota>

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)
}
