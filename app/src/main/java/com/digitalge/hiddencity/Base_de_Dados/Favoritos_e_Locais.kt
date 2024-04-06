package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "Favoritos_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Favoritos::class,
            parentColumns = ["IdFavoritos"],
            childColumns = ["IdFavoritos"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    primaryKeys = ["IdLocais", "IdFavoritos"])

data class Favoritos_e_Locais(
   val IdLocais: Int = 0,
   val IdFavoritos: Int = 0
)
