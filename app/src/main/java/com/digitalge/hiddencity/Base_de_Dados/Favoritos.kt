package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.w3c.dom.Text


@Entity(tableName = "Favoritos",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"]
        )])
data class Favoritos(
    @PrimaryKey(autoGenerate = true) val IdFavoritos: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "PlaceID") val PlaceID: String,
    @ColumnInfo(name = "URl") val URL: String,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)
