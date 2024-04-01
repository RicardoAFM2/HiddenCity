package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.digitalge.hiddencity.databinding.ActivityContasBinding


class Contas : AppCompatActivity() {
    private lateinit var binding: ActivityContasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Clicarnaimagem()
        //Mudar o texto para Favoritos
        binding.Favoritos.textViewItem.text = "Favoritos"

        //Mudar o texto para Guias Criadas
        binding.includeGuiasCriados.textViewItem.text = "Guias Criadas"

        //Mudar o texto para Pontos Criadas
        binding.includePontoCriados.textViewItem.text = "Pontos Criados"

        //Mudar o texto para Pontos Visitados
        binding.includePontoVisitados.textViewItem.text = "Pontos Visitados"
    }

    //quando clico na imagem ir para outra pagina
    private fun Clicarnaimagem(){

        //Vai para a pagina mapa
        binding.mapa.setOnClickListener{
            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
        }

        //Vai para a pagina Lista de Guia
        binding.guia.setOnClickListener{
            val intent = Intent(this, Lista_de_Guia::class.java)
            startActivity(intent)
        }

        //Vai para a pagina Lista de Favoritos
        binding.favoritos.setOnClickListener{
            val intent = Intent(this, Lista_de_Favoritos::class.java)
            startActivity(intent)
        }

        //Vai para a pagina Definição
        binding.definicao.setOnClickListener{
            val intent = Intent(this, Definicoes::class.java)
            startActivity(intent)
        }

        //Vai para a pagina home
        binding.home.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }


        //Quanto clica vai para a pagina dos favoritos
        binding.Favoritos.root.setOnClickListener {
            val intent = Intent(this, Lista_de_Favoritos::class.java)
            startActivity(intent)
        }
        //Quanto clica vai para a pagina dos guias
        binding.includeGuiasCriados.root.setOnClickListener {
            val intent = Intent(this, Lista_de_Guia::class.java)
            startActivity(intent)
        }

        //Quanto clica vai para a pagina dos Pontos Criados
        binding.includePontoCriados.root.setOnClickListener {
            val intent = Intent(this, Pontos_criados::class.java)
            startActivity(intent)
        }

        //Quanto clica vai para a pagina dos pontos visitados
        binding.includePontoVisitados.root.setOnClickListener {
            val intent = Intent(this, Pontos_visitados::class.java)
            startActivity(intent)
        }
    }
}