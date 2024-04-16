package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity(tableName = "Formulario",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"]
        )]
)


data class FormularioBD (
    @PrimaryKey(autoGenerate = true) val IdFormulario: Int = 0,
    @ColumnInfo(name = "Com_que_viaja") val Com_que_viaja: String,
    @ColumnInfo(name = "Interesses") val Interesses: String,
    @ColumnInfo(name = "Orcamento") val Orcamento: String,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)



