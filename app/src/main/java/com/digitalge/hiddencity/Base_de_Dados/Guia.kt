package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Guia",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Guia(
    @PrimaryKey(autoGenerate = true) val IdGuia: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Num_PI") val Num_PI: Int,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
    )
