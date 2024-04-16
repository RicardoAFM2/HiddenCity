package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "Comentarios_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"]
        ),
        ForeignKey(
            entity = Comentarios::class,
            parentColumns = ["IdComentarios"],
            childColumns = ["IdComentarios"]
        )
    ],
    primaryKeys = ["IdLocais", "IdComentarios"]
)


data class Comentarios_e_Locais(
    val IdLocais: Int,
    val IdComentarios: Int
)
