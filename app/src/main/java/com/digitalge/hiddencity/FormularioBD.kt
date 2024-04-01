package com.digitalge.hiddencity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Formulario")
data class FormularioBD (
    @PrimaryKey(autoGenerate = true) val IdFormulario: Int = 0,
    @ColumnInfo(name = "Com_que_viaja") val Com_que_viaja: String,
    @ColumnInfo(name = "Interesses") val Interesses: String,
    @ColumnInfo(name = "Orcamento") val Orcamento: String
)