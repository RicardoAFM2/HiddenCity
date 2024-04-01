package com.digitalge.hiddencity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Utilizador::class, Locais::class, Comentarios::class, FormularioBD::class, Guia::class, Favoritos::class, Guia_e_Locais::class, Favoritos_e_Locais::class, Comentarios_e_Locais::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun UtilizadorDao(): UtilizadorDao
    abstract fun LocaisDao(): LocaisDao
    abstract fun ComentariosDao(): ComentariosDao
    abstract fun FormularioBDDao(): FormularioBDDao
    abstract fun GuiaDao(): GuiaDao
    abstract fun FavoritosDao(): FavoritosDao
    abstract fun Guia_e_LocaisDao(): Guia_e_LocaisDao
    abstract fun Favoritos_e_LocaisDao(): Favoritos_e_LocaisDao
    abstract fun Comentarios_e_LocaisDao(): Comentarios_e_LocaisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                var instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Base_de_Dados"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}