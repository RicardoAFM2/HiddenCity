package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Locais

@Dao
interface LocaisDao {
    @Insert
    suspend fun inserir(Local: Locais): Long

    @Update
    suspend fun atualizar(Local: Locais)

    @Delete
    suspend fun Eliminar(Local: Locais)

    @Query("SELECT * FROM Locais")
    suspend fun buscarTodosLocais(): List<Locais>
}