package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.digitalge.hiddencity.databinding.ActivityDefinicoesBinding

class Definicoes : AppCompatActivity() {

    private lateinit var binding: ActivityDefinicoesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDefinicoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Mudar o texto para Privasidade
        binding.Privasidade.textViewItem.text = "Privasidade"

        //Mudar o texto para Prenser formulario
        binding.PrenserFormulario.textViewItem.text = "Prenser formulário"

        //Mudar o texto para Modificar dados
        binding.ModificarDados.textViewItem.text = "Modificar dados"

        //Mudar o texto para conectar
        binding.conectar.textViewItem.text = "Conectar a Google"

        //Mudar o texto para eliminar
        binding.Eliminar.textViewItem.text = "Eliminar a conta"

        //Mudar o texto para Sair
        binding.Sair.textViewItem.text = "Sair da Conta"

        Clicarnaimagem()
    }


    private fun Clicarnaimagem() {
        //Voltar para tras
        binding.voltar.setOnClickListener {
            onBackPressed()
        }

        //Quanto clica para ir para a pagina privasidade
        binding.Privasidade.root.setOnClickListener {
            val intent = Intent(this, Privasidade::class.java)
            startActivity(intent)
        }
        //Quanto clica para ir para a pagina Formolario
        binding.PrenserFormulario.root.setOnClickListener {
            val intent = Intent(this, Formolario::class.java)
            startActivity(intent)
        }
        //Quanto clica para ir para a pagina Modificar
        binding.ModificarDados.root.setOnClickListener {
            val intent = Intent(this, Modificar_dados::class.java)
            startActivity(intent)
        }

        //Quanto clica Elimna a conta
        binding.Eliminar.root.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


        //Quanto clica volta para o login
        binding.Sair.root.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}
