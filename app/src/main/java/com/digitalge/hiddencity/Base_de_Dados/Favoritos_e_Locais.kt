package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Favoritos_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"]
        )
                ],
)


data class Favoritos_e_Locais(
    @PrimaryKey(autoGenerate = true) val IDvisitado: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "IDlocal") val IDlocal: String,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)
