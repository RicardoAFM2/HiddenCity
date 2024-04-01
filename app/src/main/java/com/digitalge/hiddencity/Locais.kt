package com.digitalge.hiddencity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Locais")
data class Locais (
    @PrimaryKey(autoGenerate = true) val IdLocais: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String,
    @ColumnInfo(name = "Descricao") val Descricao: String,
    @ColumnInfo(name = "Latitude") val Latitude: Float,
    @ColumnInfo(name = "Longitude") val Longitude: Float,
    @ColumnInfo(name = "Avalicao") val Avalicao: Float,
    @ColumnInfo(name = "Imagens") val Imagens: String,
    @ColumnInfo(name = "Tipo") val Tipo: String
)
