package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.Privasitade

@Dao
interface PrivasitadeDao {

    @Insert
    suspend fun InserirPrivasitade(Privasitade: Privasitade)

    @Update
    suspend fun atualizar(Privasitade: Privasitade)

    @Delete
    suspend fun Eliminar(Privasitade: Privasitade)

    @Query("SELECT * FROM Privasitade")
    suspend fun buscarTodosPrivasitade(): List<Privasitade>
}