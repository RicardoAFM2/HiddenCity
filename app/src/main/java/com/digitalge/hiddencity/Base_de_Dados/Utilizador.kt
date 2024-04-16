package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text


@Entity(tableName = "Utilizador")
data class Utilizador(
    @PrimaryKey(autoGenerate = true) val IdUtilizador: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Email") val Email: String,
    @ColumnInfo(name = "Senha") val Senha: String,
    @ColumnInfo(name = "Numero") val Numero: Int
)