package com.digitalge.hiddencity



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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Lista_de_Favoritos : Fragment(R.layout.fragment_lista_de_favoritos) {
    private lateinit var binding: FragmentListaDeFavoritosBinding
    private lateinit var adapter: FavoritesAdapter
    private lateinit var favoritosDao: FavoritosDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListaDeFavoritosBinding.bind(view)

        // Inicializando o banco de dados e o DAO
        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "hiddencity.db"
        ).build()
        favoritosDao = database.FavoritosDao()  // Inicializando favoritosDao

        adapter = FavoritesAdapter(
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
                val itemToRemove = adapter.favorites[adapter.editingPosition!!]
                handleDelete(itemToRemove)
                adapter.isEditing = false
                adapter.editingPosition = -1
                binding.trashIcon.visibility = View.GONE
            }
        }

        loadData()  // Carrega os dados inicialmente
    }

    private fun handleDelete(favorito: Favoritos) {
        viewLifecycleOwner.lifecycleScope.launch {
            favoritosDao.Eliminar(favorito)  // Operação de longa duração fora da UI thread
            withContext(Dispatchers.Main) {  // Troca para a UI thread para atualizar a UI
                adapter.favorites.remove(favorito)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun handleLongClick(isEditing: Boolean, position: Int) {
        binding.trashIcon.visibility = if (isEditing) View.VISIBLE else View.GONE
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val favorites = favoritosDao.buscarTodosFavoritos()  // Operação de longa duração fora da UI thread
            withContext(Dispatchers.Main) {  // Troca para a UI thread para atualizar a UI
                adapter.favorites.addAll(favorites)
                adapter.notifyDataSetChanged()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }
}

