package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Favoritos")
data class Favoritos(
    @PrimaryKey(autoGenerate = true) val IdFavoritos: Int = 0,
    @ColumnInfo(name = "Nome") val Nome: String
)
