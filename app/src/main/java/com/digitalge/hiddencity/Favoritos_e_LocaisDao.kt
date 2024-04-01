package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Favoritos_e_LocaisDao {
    @Insert
    suspend fun inserirFavoritos_e_Locais(Favoritos_e_Locais: Favoritos_e_Locais): Long

    @Query("SELECT * FROM Favoritos_e_Locais")
    suspend fun buscarTodosFavoritos_e_Locais(): List<Favoritos_e_Locais>

    @Update
    suspend fun atualizar(Favoritos_e_Locais: Favoritos_e_Locais)

    @Delete
    suspend fun Eliminar(Favoritos_e_Locais: Favoritos_e_Locais)
}