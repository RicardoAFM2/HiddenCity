package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Utilizador

@Dao
interface UtilizadorDao {
    @Insert
    suspend fun inserirUtilizadores(Utilizador: Utilizador): Long

    @Query("SELECT * FROM Utilizador")
    suspend fun buscartodosUtilizador(): List<Utilizador>

    @Update
    suspend fun atualizar(Utilizador: Utilizador)

    @Delete
    suspend fun Eliminar(Utilizador: Utilizador)

    @Query("SELECT * FROM Utilizador WHERE Nome = :Nome OR Email = :Email AND Senha = :Senha")
    suspend fun login (Nome: String, Email: String, Senha: String): Utilizador?

}