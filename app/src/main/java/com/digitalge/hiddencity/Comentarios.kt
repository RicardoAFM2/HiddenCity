package com.digitalge.hiddencity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "Comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Comentarios (
    @PrimaryKey(autoGenerate = true) val IdComentarios: Int = 0,
    @ColumnInfo(name = "Descricao") val Descricao: String?,
    @ColumnInfo(name = "Avalicao") val Avalicao:Float,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)