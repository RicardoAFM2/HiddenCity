package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Adapter.contpublicoAdapter
import kotlinx.coroutines.launch

class Guia_cont_publico : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contpublicoAdapter: contpublicoAdapter
    private lateinit var creatorNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_cont_publico)

        val idGuia = intent.getIntExtra("ID_GUIA", -1)
        val nomeGuia = intent.getStringExtra("NOME_GUIA")

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
}

