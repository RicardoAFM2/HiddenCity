package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "Guia_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"],
        ),
        ForeignKey(
            entity = Guia::class,
            parentColumns = ["IdGuia"],
            childColumns = ["IdGuia"],
        )
    ],
    primaryKeys = ["IdLocais", "IdGuia"]
)

data class Guia_e_Locais(
    val IdLocais: Int,
    val IdGuia: Int
)
