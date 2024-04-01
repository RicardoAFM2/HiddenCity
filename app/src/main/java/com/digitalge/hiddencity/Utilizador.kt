package com.digitalge.hiddencity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Utilizador")
data class Utilizador (
    @PrimaryKey(autoGenerate = true) val IdUtilizador: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Email") val Email: String,
    @ColumnInfo(name = "Senha") val Senha: String,
    @ColumnInfo(name = "Numero") val Numero: Int
)