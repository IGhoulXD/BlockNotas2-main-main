package com.igxd.blocknotas2.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.igxd.blocknotas2.data.models.Recordatorio

@Database(entities = [Recordatorio::class], version = 1)
abstract class BlockNotasDatabase : RoomDatabase() {
    abstract fun recordatorioDao(): RecordatorioDao
}
