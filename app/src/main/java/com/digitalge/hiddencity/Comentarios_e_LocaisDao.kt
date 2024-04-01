package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Comentarios_e_LocaisDao {
    @Insert
    suspend fun inserirComentarios_e_Locais(Comentarios_e_Locais: Comentarios_e_Locais): Long

    @Query("SELECT * FROM Comentarios_e_Locais")
    suspend fun buscarTodosComentarios_e_Locais(): List<Comentarios_e_Locais>

    @Update
    suspend fun atualizar(Comentarios_e_Locais: Comentarios_e_Locais)

    @Delete
    suspend fun Eliminar(Comentarios_e_Locais: Comentarios_e_Locais)
}