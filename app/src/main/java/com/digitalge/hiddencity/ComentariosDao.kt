package com.digitalge.hiddencity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ComentariosDao {
    @Insert
    suspend fun inserirComentario(comentario: Comentarios): Long

    @Query("SELECT * FROM Comentarios")
    suspend fun buscarTodosComentarios(): List<Comentarios>

    @Update
    suspend fun atualizar(comentario: Comentarios)

    @Delete
    suspend fun Eliminar(comentario: Comentarios)
}