package com.digitalge.hiddencity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.PontoCriadoAdapter
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pontos_criados_publicos : Fragment(R.layout.fragment_pontos_criados_publicos) {

    private lateinit var database: AppDatabase
    private lateinit var adapter: PontoCriadoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = PontoCriadoAdapter(emptyList(), requireContext())
        recyclerView.adapter = adapter

        database = AppDatabase.getDatabase(requireContext())
        val userId = arguments?.getInt("USER_ID", -1) ?: -1
        if (userId != -1) {
            loadPontosCriados(userId)  // Pass the userId to the method
        }

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                adapter.filter(s.toString())
            }
        })
    }

    private fun loadPontosCriados(userId: Int) {
        lifecycleScope.launch {
            val pontos = withContext(Dispatchers.IO) {
                database.LocaisDao().buscarLocaisPublicosPorCriador(userId)
            }
            adapter.updatePontos(pontos)
        }
    }
}