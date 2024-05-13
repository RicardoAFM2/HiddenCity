package com.digitalge.hiddencity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Base_de_Dados.Privasitade
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import com.digitalge.hiddencity.databinding.FragmentContasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Conta_publica : Fragment() {

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

        val userId = arguments?.getInt("USER_ID", -1) ?: -1
        if (userId != -1) {
            loadUserData(userId)
        }


        binding.includeFavoritos.root.setOnClickListener {
            navigateToFragment(Lista_de_favoritos_publico(), userId)
        }

        binding.includeGuiasCriados.root.setOnClickListener {
            navigateToFragment(Lista_de_Guia_publico(), userId)
        }

        binding.includePontosCriados.root.setOnClickListener {
            navigateToFragment(Pontos_criados_publicos(), userId)
        }

        binding.includePontosVisitados.root.setOnClickListener {
            navigateToFragment(pontos_visitados_publico(), userId)
        }

        setupText()
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

    private fun navigateToFragment(fragment: Fragment, userId: Int? = null) {
        // Criar um bundle para passar o userId
        val args = Bundle()
        userId?.let {
            args.putInt("USER_ID", it)
            args.putString("USER_NAME", it.toString())
        }
        fragment.arguments = args

        // Navegar para o fragmento
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }



    private fun loadImageFromUri(imageUri: String) {

        Glide.with(this)
            .load(imageUri)  // Diretamente a Uri, se você está seguro que é uma Uri válida
            .error(R.drawable.ic_launcher_background)  // Imagem de erro
            .into(binding.imageViewAvatar)
        Log.d("LoadImage", "Attempting to load image from URI: $imageUri")

    }

    private fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(requireContext(), AppDatabase::class.java, "hiddencity.db").build()
    }

    private fun loadUserData(userId: Int) {
        lifecycleScope.launch {
            val utilizador: Utilizador?
            val privacidade: Privasitade?
            withContext(Dispatchers.IO) {
                // Executando consultas no thread de I/O
                utilizador = getDatabase().UtilizadorDao().buscarPorId(userId)
                privacidade = getDatabase().PrivasitadeDao().buscarPrivasitadePorUserId(userId)
            }
            // Atualizando a UI no thread principal
            utilizador?.let {
                updateUI(it, privacidade)
            }
        }
    }

    private fun updateUI(utilizador: Utilizador, privacidade: Privasitade?) {
        binding.textViewNome.text = utilizador.Nome
        binding.textViewIdUtilizador.text = utilizador.IdUtilizador.toString()
        loadImageFromUri(utilizador.Imagem)
        if (privacidade?.conta_privada == 1) {
            binding.includeFavoritos.root.visibility = View.GONE
            binding.includeGuiasCriados.root.visibility = View.GONE
            binding.includePontosCriados.root.visibility = View.GONE
            binding.includePontosVisitados.root.visibility = View.GONE
        }

        binding.includeFavoritos.root.visibility = if (privacidade?.Privar_os_favoritos == 1) View.GONE else View.VISIBLE
        binding.includeGuiasCriados.root.visibility = if (privacidade?.privar_os_guias_criados == 1) View.GONE else View.VISIBLE
        binding.includePontosCriados.root.visibility = if (privacidade?.privar_os_pontos_criados == 1) View.GONE else View.VISIBLE
        binding.includePontosVisitados.root.visibility = if (privacidade?.privar_os_pontos_visitados == 1) View.GONE else View.VISIBLE
    }

}
