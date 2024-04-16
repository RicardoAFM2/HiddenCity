package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import org.w3c.dom.Text

@Entity(tableName = "Comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"]
        )
    ])
data class Comentarios (
    @PrimaryKey(autoGenerate = true) val IdComentarios: Int = 0,
    @ColumnInfo(name = "Descricao") val Descricao: String,
    @ColumnInfo(name = "Avalicao") val Avalicao: Double,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)