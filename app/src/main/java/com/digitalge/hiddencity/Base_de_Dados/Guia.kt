package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity(tableName = "Guia",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"]
        )
    ])
data class Guia(
    @PrimaryKey(autoGenerate = true) val IdGuia: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Num_PI") val Num_PI: Int,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
    )
