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
import com.digitalge.hiddencity.Adapter.PublicoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Lista_de_Guia_publico : Fragment() {

    private lateinit var adapter: PublicoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lista_de__guia_publico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PublicoAdapter(emptyList()) { guia ->
            val intent = Intent(activity, Guia_cont_publico::class.java)
            intent.putExtra("ID_GUIA", guia.IdGuia)
            intent.putExtra("NOME_GUIA", guia.Nome)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val userId = arguments?.getInt("USER_ID", -1) ?: -1
        if (userId != -1) {
            loadPublicGuias(userId)
        }

        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadPublicGuias(userId: Int) {
        lifecycleScope.launch {
            val guiasPublicos = withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                db.GuiaDao().buscarGuiasPublicosPorUsuario(userId)
            }
            adapter.updateGuias(guiasPublicos)
        }
    }
}