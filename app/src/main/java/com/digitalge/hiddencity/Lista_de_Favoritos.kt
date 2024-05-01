package com.digitalge.hiddencity



import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.digitalge.hiddencity.Adapter.FavoritesAdapter
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.Dao.FavoritosDao
import com.digitalge.hiddencity.databinding.FragmentListaDeFavoritosBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Lista_de_Favoritos : Fragment(R.layout.fragment_lista_de_favoritos) {
    private lateinit var binding: FragmentListaDeFavoritosBinding
    private lateinit var adapter: FavoritesAdapter
    private lateinit var favoritosDao: FavoritosDao
    private lateinit var placesClient: PlacesClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListaDeFavoritosBinding.bind(view)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(requireContext())


        // Inicializando o banco de dados e o DAO
        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "hiddencity.db"
        ).build()
        favoritosDao = database.FavoritosDao()  // Inicializando favoritosDao

        adapter = FavoritesAdapter(
            placesClient,
            mutableListOf(),
            requireContext(),
            onDeleteClick = { favorito -> handleDelete(favorito) },
            onLongItemClick = { isEditing, position ->
                binding.trashIcon.visibility = if (isEditing) View.VISIBLE else View.GONE })

        binding.resultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@Lista_de_Favoritos.adapter
        }

        binding.trashIcon.setOnClickListener {
            Log.d("DeleteIcon", "Clique no ícone de lixo")
            if (adapter.isEditing && adapter.editingPosition != -1) {
                adapter.removeItemAtPosition(adapter.editingPosition!!)
                adapter.isEditing = false
                adapter.editingPosition = null
                binding.trashIcon.visibility = View.GONE
            }
        }

        loadData()  // Carrega os dados inicialmente
    }

    private fun handleDelete(favorito: Favoritos) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {  // Operações de banco de dados em Dispatchers.IO
            favoritosDao.Eliminar(favorito)
            withContext(Dispatchers.Main) {  // Troca para a UI thread para atualizar a UI
                adapter.removeFavorite(favorito)
            }
        }
    }



    private fun loadData() {
        val userId = getUserId()  // Assegure-se de que este método não acessa o banco de dados diretamente
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {  // Usando Dispatchers.IO para operações de I/O
            val favorites = favoritosDao.buscarFavoritosPorUtilizadorId(userId)  // Filtra por ID de usuário
            withContext(Dispatchers.Main) {  // Troca para a UI thread para atualizar a UI
                adapter.updateFavorites(favorites)
            }
        }
    }

    private fun getUserId(): Int {
        return requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}

