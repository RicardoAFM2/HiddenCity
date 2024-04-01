package com.digitalge.hiddencity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Guia_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Locais::class,
            parentColumns = ["IdLocais"],
            childColumns = ["IdLocais"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Guia::class,
            parentColumns = ["IdGuia"],
            childColumns = ["IdGuia"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    primaryKeys = ["IdLocais", "IdGuia"]
)

data class Guia_e_Locais(
    val IdLocais: Int = 0,
    val IdGuia: Int = 0
)
