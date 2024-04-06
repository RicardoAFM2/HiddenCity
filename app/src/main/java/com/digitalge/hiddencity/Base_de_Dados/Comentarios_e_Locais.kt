package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "Comentarios_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Comentarios::class,
            parentColumns = ["IdComentarios"],
            childColumns = ["IdComentarios"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    primaryKeys = ["IdLocais", "IdComentarios"]
)


data class Comentarios_e_Locais(
    val IdLocais: Int = 0,
    val IdComentarios: Int = 0
)
