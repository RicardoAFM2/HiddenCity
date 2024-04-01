package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GuiaDao {
    @Insert
    suspend fun inserirGuia(Guia: Guia): Long

    @Query("SELECT * FROM Guia")
    suspend fun buscarTodosGuia(): List<Guia>

    @Update
    suspend fun atualizar(Guia: Guia)

    @Delete
    suspend fun Eliminar(Guia: Guia)
}