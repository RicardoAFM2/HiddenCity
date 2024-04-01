package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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
}