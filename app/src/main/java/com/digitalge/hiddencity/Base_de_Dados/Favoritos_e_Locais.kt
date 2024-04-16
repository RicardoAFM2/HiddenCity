package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "Favoritos_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"]
        ),
        ForeignKey(
            entity = Favoritos::class,
            parentColumns = ["IdFavoritos"],
            childColumns = ["IdFavoritos"]
        )
    ],
    primaryKeys = ["IdLocais", "IdFavoritos"])

data class Favoritos_e_Locais(
   val IdLocais: Int,
   val IdFavoritos: Int
)
