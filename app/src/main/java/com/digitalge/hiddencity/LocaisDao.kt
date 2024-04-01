package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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