package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Favoritos

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

    @Query("SELECT * FROM Favoritos WHERE IdUtilizador = :UtilizadorId")
    fun buscarFavoritosPorUtilizadorId(UtilizadorId: Int): List<Favoritos>
    @Query("SELECT EXISTS(SELECT * FROM Utilizador WHERE IdUtilizador = :idUtilizador)")
    suspend fun isUserExists(idUtilizador: Int): Boolean
}