package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.digitalge.hiddencity.databinding.ActivityMapaBinding

class Mapa : AppCompatActivity() {

    private lateinit var binding: ActivityMapaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Clicarnaimagem()


    }

    //quando clico na imagem ir para outra pagina
    private fun Clicarnaimagem(){

        //Vai para a pagina home
        binding.home.setOnClickListener{
            val intent = Intent(this, Home::class.java)
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
        binding.definicao.setOnClickListener{
            val intent = Intent(this, Definicoes::class.java)
            startActivity(intent)
        }
    }

}