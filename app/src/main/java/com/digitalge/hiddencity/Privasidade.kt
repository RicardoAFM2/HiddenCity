package com.digitalge.hiddencity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitalge.hiddencity.databinding.ActivityContasBinding
import com.digitalge.hiddencity.databinding.ActivityPrivasidadeBinding


class Privasidade : AppCompatActivity() {
    private lateinit var binding: ActivityPrivasidadeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivasidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Mudar o texto para Privar a conta
        binding.contaPrivada.textViewItem.text = "Deixar a conta privada"

        //Mudar o texto para Privar a guias criados
        binding.privarOsGuiasCriados.textViewItem.text = "Deixar a lista de guias criados privado"

        //Mudar o texto para Privar os favoritos
        binding.PrivarOsFavoritos.textViewItem.text = "Deixar a lista favoritos privado"

        //Mudar o texto para Privar os pontos criados
        binding.privarOsPontosCriados.textViewItem.text = "Deixar a lista de pontos criados privado"

        //Mudar o texto para Privar os pontos visitados
        binding.privarOsPontosVisitados.textViewItem.text = "Deixar a lista de pontos visitados privada"

        Clicarnaimagem()
    }

    private fun Clicarnaimagem(){
        //Voltar para tras
        binding.voltra1.setOnClickListener {
            onBackPressed()
        }
    }
}