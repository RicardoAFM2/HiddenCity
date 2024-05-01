package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Guia_e_Locais

@Dao
interface Guia_e_LocaisDao {
    @Insert
    suspend fun inserirGuia_e_Locais(Guia_e_Locais: Guia_e_Locais): Long

    @Query("SELECT * FROM Guia_e_Locais")
    suspend fun buscarTodosGuia_e_Locais(): List<Guia_e_Locais>

    @Update
    suspend fun atualizar(Guia_e_Locais: Guia_e_Locais)

    @Delete
    suspend fun Eliminar(Guia_e_Locais: Guia_e_Locais)

    @Query("SELECT * FROM Guia_e_Locais WHERE IdGuia = :idGuia")
    suspend fun buscarPorIdGuia(idGuia: Int): List<Guia_e_Locais>


    @Query("SELECT * FROM Guia_e_Locais WHERE placeID = :placeID")
    fun buscarPorPlaceID(placeID: String): List<Guia_e_Locais>
}