package com.digitalge.hiddencity


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.AppDatabase.Companion.getDatabase
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import com.digitalge.hiddencity.databinding.FragmentContasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectInstance
import kotlinx.coroutines.withContext
import java.io.File
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
        loadUserData()
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

    private fun getUserIdFromPreferences(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("UteID", -1)  // -1 seria um valor padrão indicando que nenhum usuário está logado
    }



    private fun loadImageFromUri(imageUri: String) {
        val fileUri = Uri.fromFile(File(imageUri))
        Glide.with(this)
            .load(fileUri)

            .error(R.drawable.ic_launcher_background)   // Substitua pelo seu drawable de erro
            .into(binding.imageViewAvatar)
    }
    private fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(requireContext(), AppDatabase::class.java, "hiddencity.db").build()
    }

    private fun loadUserData() {
        val userId = getUserIdFromPreferences()  // Suponha que esta função obtenha o ID do usuário das SharedPreferences

        lifecycleScope.launch {
            val utilizador = getDatabase().UtilizadorDao().buscarPorId(userId)
            withContext(Dispatchers.Main) {
                updateUI(utilizador)
            }
        }
    }

    private fun updateUI(utilizador: Utilizador) {
        binding.textViewNome.text = utilizador.Nome
        binding.textViewIdUtilizador.text = utilizador.IdUtilizador.toString()
        loadImageFromUri(utilizador.Imagem)
    }

}
