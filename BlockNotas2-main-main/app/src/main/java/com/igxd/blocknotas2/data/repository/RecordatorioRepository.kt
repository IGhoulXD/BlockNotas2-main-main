package com.igxd.blocknotas2.repository

import com.igxd.blocknotas2.data.db.RecordatorioDao
import com.igxd.blocknotas2.data.models.Recordatorio

class RecordatorioRepository(private val recordatorioDao: RecordatorioDao) {

    suspend fun insertar(recordatorio: Recordatorio) {
        recordatorioDao.insertar(recordatorio)
    }

    suspend fun actualizar(recordatorio: Recordatorio) {
        recordatorioDao.actualizar(recordatorio)
    }

    suspend fun eliminar(recordatorio: Recordatorio) {
        recordatorioDao.eliminar(recordatorio)
    }

    suspend fun obtenerTodos(): List<Recordatorio> {
        return recordatorioDao.obtenerTodos()
    }

    suspend fun obtenerPorId(id: Int): Recordatorio? {
        return recordatorioDao.obtenerPorId(id)
    }
}
