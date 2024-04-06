package com.digitalge.hiddencity.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.digitalge.hiddencity.Base_de_Dados.FormularioBD

@Dao
interface FormularioBDDao {
    @Insert
    suspend fun inserirFormulario(FormularioBD: FormularioBD): Long

    @Query("SELECT * FROM Formulario")
    suspend fun buscarTodosFormulario(): List<FormularioBD>

    @Update
    suspend fun atualizar(FormularioBD: FormularioBD)

    @Delete
    suspend fun Eliminar(FormularioBD: FormularioBD)
}