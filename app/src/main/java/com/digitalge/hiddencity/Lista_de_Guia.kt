package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.digitalge.hiddencity.databinding.ActivityListaDeFavoritosBinding
import com.digitalge.hiddencity.databinding.ActivityListaDeGuiaBinding

class Lista_de_Guia : AppCompatActivity() {

    private lateinit var binding: ActivityListaDeGuiaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaDeGuiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Clicarnaimagem()


    }

    //quando clico na imagem ir para outra pagina
    private fun Clicarnaimagem(){

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


        //Vai para a pagina home
        binding.home.setOnClickListener{
            val intent = Intent(this, Home::class.java)
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