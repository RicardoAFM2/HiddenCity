package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Guia

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

    @Query("SELECT * FROM Guia WHERE IdUtilizador = :userId")
    fun buscarGuiaPorUtilizadorId(userId: Int): List<Guia>
}