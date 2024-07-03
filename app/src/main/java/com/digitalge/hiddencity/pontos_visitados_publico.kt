package com.digitalge.hiddencity

import android.content.Context
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
import com.digitalge.hiddencity.Adapter.PontoVisitadoAdapter
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class pontos_visitados_publico : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pontos_visitados_publico, container, false)
    }
    private lateinit var adapter: PontoVisitadoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicialize o PlacesClient
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "") // Substitua YOUR_API_KEY pela sua chave de API
        }
        val placesClient = Places.createClient(requireContext())

        // Crie o adaptador passando o PlacesClient
        adapter = PontoVisitadoAdapter(emptyList(), placesClient, requireContext())
        recyclerView.adapter = adapter

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                adapter.filter(s.toString())
            }
        })

        val userId = arguments?.getInt("USER_ID", -1) ?: -1
        if (userId != -1) {
            loadPontosVisitados(userId)
        }


    }

    private fun loadPontosVisitados(userId: Int) {
        lifecycleScope.launch {
            val pontosVisitados = withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                db.Favoritos_e_LocaisDao().buscarPontosVisitadosPorUsuario(userId)
            }
            if (isAdded) { // Verifique se o Fragment ainda está ativo
                adapter.updatePontos(pontosVisitados)
            }
        }
    }

}