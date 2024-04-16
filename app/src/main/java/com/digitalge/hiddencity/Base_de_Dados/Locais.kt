package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity(tableName = "Locais")
data class Locais (
    @PrimaryKey(autoGenerate = true) val IdLocais: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Descricao") val Descricao: String,
    @ColumnInfo(name = "Latitude") val Latitude: Double,
    @ColumnInfo(name = "Longitude") val Longitude: Double,
    @ColumnInfo(name = "Avalicao") val Avalicao: Float,
    @ColumnInfo(name = "Imagens") val Imagens: String,
    @ColumnInfo(name = "Tipo") val Tipo: String
)
