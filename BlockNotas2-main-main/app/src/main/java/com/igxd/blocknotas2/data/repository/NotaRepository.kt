package com.igxd.BlockNotas.data.repository

import com.igxd.BlockNotas.data.db.NotaDao
import com.igxd.BlockNotas.data.models.Nota

class NotaRepository(private val notaDao: NotaDao) {
    suspend fun insertar(nota: Nota) {
        notaDao.insertar(nota)
    }

    suspend fun obtenerTodasLasNotas(): List<Nota> {
        return notaDao.obtenerTodasLasNotas()
    }

    suspend fun actualizar(nota: Nota) {
        notaDao.actualizar(nota)
    }

    suspend fun eliminar(nota: Nota) {
        notaDao.eliminar(nota)
    }
}
