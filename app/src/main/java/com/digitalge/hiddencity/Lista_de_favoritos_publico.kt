package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.FavoritospublicoAdapter
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Lista_de_favoritos_publico : Fragment(R.layout.fragment_lista_de_favoritos_publico) {

    private lateinit var adapter: FavoritospublicoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        val placesClient = Places.createClient(requireContext())

        // Initialize adapter and set it to the RecyclerView
        adapter = FavoritospublicoAdapter(emptyList(), placesClient) { placeId ->
            navigateToDetails(placeId)
        }
        recyclerView.adapter = adapter

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                adapter.filter(s.toString())
            }
        })

        // Load data
        arguments?.getInt("USER_ID", -1)?.let { userId ->
            if (userId != -1) {
                loadFavoritos(userId)
            }
        }
    }

    private fun navigateToDetails(placeId: String) {
        val intent = Intent(context, DetalhesLocalActivity::class.java).apply {
            putExtra("place_id", placeId)
        }
        startActivity(intent)
    }

    private fun loadFavoritos(userId: Int) {
        lifecycleScope.launch {
            val favoritos = withContext(Dispatchers.IO) {
                // Supondo que AppDatabase é sua classe de acesso ao banco de dados e FavoritosDao é seu DAO
                val db = AppDatabase.getDatabase(requireContext())
                db.FavoritosDao().buscarFavoritosPorUsuario(userId)
            }
            // Atualiza a lista no adaptador
            if (isAdded) { // Verifique se o Fragment ainda está ativo
                adapter.updateFavoritos(favoritos)
            }
        }
    }
}

