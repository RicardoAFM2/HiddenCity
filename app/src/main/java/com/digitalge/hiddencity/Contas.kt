package com.digitalge.hiddencity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.databinding.FragmentContasBinding
import kotlinx.coroutines.selects.SelectInstance
import java.util.zip.Inflater

class Contas : Fragment() {

    private var _binding: FragmentContasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Clicarnaimagem()
        setupText()
    }

    private fun Clicarnaimagem(){
        binding.includeFavoritos.root.setOnClickListener {
            navigateToFragment(Lista_de_Favoritos())
        }
        binding.includeGuiasCriados.root.setOnClickListener {
            navigateToFragment(Lista_de_Guia())
        }
        binding.includePontosCriados.root.setOnClickListener {
            navigateToFragment(Pontos_criados())
        }
        binding.includePontosVisitados.root.setOnClickListener {
            navigateToFragment(Pontos_visitados())
        }
    }

    private fun setupText() {
        //Mudar o texto para Favoritos
        binding.includeFavoritos.textViewItem.text = "Favoritos"

        //Mudar o texto para Guias Criadas
        binding.includeGuiasCriados.textViewItem.text = "Guias Criadas"

        //Mudar o texto para Pontos Criadas
        binding.includePontosCriados.textViewItem.text = "Pontos Criados"

        //Mudar o texto para Pontos Visitados
        binding.includePontosVisitados.textViewItem.text = "Pontos Visitados"
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}
