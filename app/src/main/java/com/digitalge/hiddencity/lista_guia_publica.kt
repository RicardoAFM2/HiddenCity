package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.PublicoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class lista_guia_publica : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var publicoAdapter: PublicoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_guia_publica, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewGuia)
        recyclerView.layoutManager = LinearLayoutManager(context)

        publicoAdapter = PublicoAdapter(emptyList()) { guia ->
            val intent = Intent(activity, Guia_cont_publico::class.java)
            intent.putExtra("ID_GUIA", guia.IdGuia)
            intent.putExtra("NOME_GUIA", guia.Nome)
            startActivity(intent)
        }
        recyclerView.adapter = publicoAdapter



        val osteus: TextView = view.findViewById(R.id.textView)
        osteus.setOnClickListener {
            replaceFragment(Lista_de_Guia())
        }

        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                publicoAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loadGuiasPublicos()

        return view
    }


    fun replaceFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainer, fragment)
            ?.addToBackStack(null)  // Adiciona a transação à pilha de volta para navegação de retorno
            ?.commit()
    }

    private fun loadGuiasPublicos() {
        GlobalScope.launch(Dispatchers.IO) {
            val guiasPublicos = AppDatabase.getDatabase(requireContext()).GuiaDao().buscarGuiaPublicos()
            launch(Dispatchers.Main) {
                publicoAdapter.setOriginalGuias(guiasPublicos)
                publicoAdapter.updateGuias(guiasPublicos)
            }
        }
    }

}