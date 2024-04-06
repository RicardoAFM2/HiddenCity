package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.digitalge.hiddencity.databinding.ActivityHomeBinding


class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clicarnaimagem()

    }

    //quando clico na imagem ir para outra pagina
    private fun clicarnaimagem(){

        //Vai para a pagina mapa
        binding.mapa.setOnClickListener{
            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
        }

        //Vai para a pagina da conta
        binding.conta.setOnClickListener{
            val intent = Intent(this, Contas::class.java)
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
        binding.definicao.setOnClickListener {
            val intent = Intent(this, Definicoes::class.java)
            startActivity(intent)
        }
    }

}