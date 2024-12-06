package com.igxd.blocknotas2.data.repository
import com.igxd.BlockNotas.data.db.NotaDao
import com.igxd.BlockNotas.data.models.Nota
import com.igxd.blocknotas2.data.db.RecordatorioDao
import com.igxd.blocknotas2.data.models.Recordatorio

class NotaRepository(
    private val notaDao: NotaDao,
    private val recordatorioDao: RecordatorioDao
) {

    // Insertar una nueva nota junto con sus recordatorios
    suspend fun insertarNotaConRecordatorios(nota: Nota, recordatorios: List<Recordatorio>) {

        notaDao.insertar(nota)

        recordatorios.forEach { recordatorio ->
            val recordatorioConNota = recordatorio.copy(id = nota.id) // Aseguramos que el recordatorio esté asociado a la nota
            recordatorioDao.insertar(recordatorioConNota)
        }
    }

    // Obtener todas las notas con sus recordatorios
    suspend fun obtenerNotasConRecordatorios(): List<Nota> {
        return notaDao.obtenerTodasLasNotas() // Esto puede cambiar si necesitas obtener notas con recordatorios
    }

    // Actualizar una nota
    suspend fun actualizar(nota: Nota) {
        notaDao.actualizar(nota)
    }

    // Eliminar una nota
    suspend fun eliminar(nota: Nota) {
        notaDao.eliminar(nota)
    }

    suspend fun obtenerRecordatoriosDeNota(notaId: Long): List<Recordatorio> {
        return recordatorioDao.obtenerRecordatoriosDeNota(notaId)
    }


    // Actualizar un recordatorio
    suspend fun actualizarRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.actualizar(recordatorio)
    }

    // Eliminar un recordatorio
    suspend fun eliminarRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.eliminar(recordatorio)
    }
}


