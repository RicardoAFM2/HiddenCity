package com.digitalge.hiddencity.Base_de_Dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Privasitade",
    foreignKeys = [
        ForeignKey(
            entity = Utilizador::class,
            parentColumns = ["IdUtilizador"],
            childColumns = ["IdUtilizador"],
        )
    ],
)

class Privasitade(
    @PrimaryKey(autoGenerate = true) val IdPriva: Int = 0,
    @ColumnInfo(name = "conta_privada") val conta_privada: Int,
    @ColumnInfo(name = "Privar_os_favoritos") val Privar_os_favoritos: Int,
    @ColumnInfo(name = "privar_os_pontos_criados") val privar_os_pontos_criados: Int,
    @ColumnInfo(name = "privar_os_pontos_visitados") val privar_os_pontos_visitados: Int,
    @ColumnInfo(name = "privar_os_guias_criados") val privar_os_guias_criados: Int,
    @ColumnInfo(name = "IdUtilizador") val IdUtilizador: Int
)