package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Privasitade

@Dao
interface PrivasitadeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InserirPrivasitade(privasitade: Privasitade)

    @Update
    suspend fun atualizar(privasitade: Privasitade)

    @Delete
    suspend fun Eliminar(Privasitade: Privasitade)

    @Query("SELECT * FROM Privasitade")
    suspend fun buscarTodosPrivasitade(): List<Privasitade>

    @Query("SELECT * FROM Privasitade WHERE IdUtilizador = :userId")
    fun buscarPrivasitadePorUserId(userId: Int): Privasitade?

    @Query("UPDATE Privasitade SET conta_privada = :contaPrivada, Privar_os_favoritos = :privarFavoritos, privar_os_pontos_criados = :privarPontosCriados, privar_os_pontos_visitados = :privarPontosVisitados, privar_os_guias_criados = :privarGuiasCriados WHERE IdUtilizador = :userId")
    suspend fun atualizarPrivasitade(userId: Int, contaPrivada: Int, privarFavoritos: Int, privarPontosCriados: Int, privarPontosVisitados: Int, privarGuiasCriados: Int)

}
