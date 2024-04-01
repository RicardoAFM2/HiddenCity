package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoritosDao {
    @Insert
    suspend fun inserirFavoritos(Favoritos: Favoritos): Long

    @Query("SELECT * FROM Favoritos")
    suspend fun buscarTodosFavoritos(): List<Favoritos>

    @Update
    suspend fun atualizar(Favoritos: Favoritos)

    @Delete
    suspend fun Eliminar(Favoritos: Favoritos)
}