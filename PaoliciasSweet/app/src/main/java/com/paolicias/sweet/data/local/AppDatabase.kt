package com.paolicias.sweet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, Producto::class, TarifaRango::class, Evento::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun tarifaDao(): TarifaDao
    abstract fun eventoDao(): EventoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "paolicias.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instancia = it }
            }
    }
}
