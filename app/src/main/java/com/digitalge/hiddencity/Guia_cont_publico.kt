package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.contpublicoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Guia_cont_publico : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contpublicoAdapter: contpublicoAdapter
    private lateinit var creatorNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_cont_publico)

        val idGuia = intent.getIntExtra("ID_GUIA", -1)

        if (idGuia != -1) {
            loadGuiaDetails(idGuia)
        }

        recyclerView = findViewById(R.id.recyclerViewGuia)
        recyclerView.layoutManager = LinearLayoutManager(this)
        contpublicoAdapter = contpublicoAdapter(emptyList(), this) { guia ->
            // Configuração do clique que inicia DetalhesLocalActivity
            val intent = Intent(this, DetalhesLocalActivity::class.java)
            intent.putExtra("place_id", guia.placeID)
            startActivity(intent)
        }

        recyclerView.adapter = contpublicoAdapter

        creatorNameTextView = findViewById(R.id.creatorName)

        findViewById<View>(R.id.voltar).setOnClickListener { onBackPressed() }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                contpublicoAdapter.filter(s.toString())
            }
        })

        loadGuias()
    }

    private fun loadGuias() {
        val idGuia = intent.getIntExtra("ID_GUIA", -1)  // Supondo que o ID é passado através de um Intent

        // Iniciar uma corrotina para chamar a função suspensa
        lifecycleScope.launch {
            val guias = AppDatabase.getDatabase(this@Guia_cont_publico).Guia_e_LocaisDao().buscarPorIdGuia(idGuia)
            contpublicoAdapter.updateGuias(guias) // Atualiza o adapter em uma thread segura

        }
    }

    private fun loadGuiaDetails(guiaId: Int) {
        lifecycleScope.launch {
            val guia = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(applicationContext).GuiaDao().getGuiaById(guiaId)
            }

            guia?.let {
                updateGuiDetails(it.Nome, it.Nome_utilizador)
            }
        }
    }

    private fun updateGuiDetails(nomeGuia: String, nomeCriador: String) {
        findViewById<TextView>(R.id.textView9).text = nomeGuia
        findViewById<TextView>(R.id.creatorName).text = nomeCriador
    }
}

