package com.digitalge.hiddencity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitalge.hiddencity.databinding.ActivityContasBinding
import com.digitalge.hiddencity.databinding.ActivityModificarDadosBinding


class Modificar_dados : AppCompatActivity() {

    private lateinit var binding: ActivityModificarDadosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityModificarDadosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Mudar o texto para Modificar o nome
        binding.ModificasNome.textViewItem.text = "Modificar o nome"

        //Mudar o texto para Modificar a senha
        binding.ModificasSenha.textViewItem.text = "Modificar o senha"

        //Mudar o texto para Modificar o emial
        binding.ModificasEmail.textViewItem.text = "Modificar o email"

        //Mudar o texto para Modificar o numero
        binding.ModificasNumero.textViewItem.text = "Modificar o numero"

        Clicarnaimagem()
    }

    private fun Clicarnaimagem(){
        binding.voltar.setOnClickListener {
            onBackPressed()
        }

    }
}