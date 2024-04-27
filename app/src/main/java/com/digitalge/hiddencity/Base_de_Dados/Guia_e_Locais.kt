package com.digitalge.hiddencity.Base_de_Dados


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Guia_e_Locais",
    foreignKeys = [
        ForeignKey(
            entity = Guia::class,
            parentColumns = ["IdGuia"],
            childColumns = ["IdGuia"],
        )
    ],
)

data class Guia_e_Locais(
    @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "placeID") val placeID: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "IdGuia") val IdGuia: Int
)
