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


    @Query("DELETE FROM Utilizador WHERE IdUtilizador = :UtilizadorId")
    suspend fun Eliminar(UtilizadorId: Int)

    @Query("UPDATE utilizador SET Nome = :novoNome WHERE IdUtilizador = :UtilizadorId")
    suspend fun atualizarNome(UtilizadorId: Int, novoNome: String)
    @Query("UPDATE Utilizador SET senha = :novaSenha WHERE IdUtilizador = :UtilizadorId")
    suspend fun atualizarPalavrapasse(UtilizadorId: Int, novaSenha: String)

    @Query("UPDATE Utilizador SET numero = :novoNumero WHERE IdUtilizador = :UtilizadorId")
    suspend fun atualizarNumero(UtilizadorId: Int, novoNumero: String)

    @Query("UPDATE Utilizador SET Email = :novoEmail WHERE IdUtilizador = :UtilizadorId")
    suspend fun atualizarEmail(UtilizadorId: Int, novoEmail: String)

    @Query("SELECT * FROM Utilizador WHERE Nome = :Nome OR Email = :Email AND Senha = :Senha")
    suspend fun login (Nome: String, Email: String, Senha: String): Utilizador?

    @Query("UPDATE Utilizador SET Imagem = :imageUrl WHERE IdUtilizador = :userId")
    suspend fun atualizarImagemUrl(userId: Int, imageUrl: String)
    @Query("SELECT * FROM Utilizador WHERE IdUtilizador = :userId")
    suspend fun buscarPorId(userId: Int): Utilizador


}