package com.digitalge.hiddencity

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.digitalge.hiddencity.Base_de_Dados.Privasitade
import com.digitalge.hiddencity.databinding.ActivityContasBinding
import com.digitalge.hiddencity.databinding.ActivityPrivasidadeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Privasidade : AppCompatActivity() {
    private lateinit var binding: ActivityPrivasidadeBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivasidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar o banco de dados
        database = AppDatabase.getDatabase(applicationContext)

        // Configurar os textos
        setupTexts()

        // Configurar os listeners
        setupListeners()
    }

    private fun setupTexts() {
        binding.contaPrivada.textViewItem.text = "Deixar a conta privada"
        binding.privarOsGuiasCriados.textViewItem.text = "Deixar a lista de guias criados privado"
        binding.PrivarOsFavoritos.textViewItem.text = "Deixar a lista favoritos privado"
        binding.privarOsPontosCriados.textViewItem.text = "Deixar a lista de pontos criados privado"
        binding.privarOsPontosVisitados.textViewItem.text = "Deixar a lista de pontos visitados privada"
    }

    private fun setupListeners() {
        binding.button2.setOnClickListener {
            savePrivacySettings(
                binding.contaPrivada.switch1.isChecked,
                binding.privarOsGuiasCriados.switch1.isChecked,
                binding.PrivarOsFavoritos.switch1.isChecked,
                binding.privarOsPontosCriados.switch1.isChecked,
                binding.privarOsPontosVisitados.switch1.isChecked
            )
        }

        binding.voltra1.setOnClickListener {
            onBackPressed()
        }
    }

    private fun savePrivacySettings(contaPrivada: Boolean, privarGuiasCriados: Boolean, privarFavoritos: Boolean, privarPontosCriados: Boolean, privarPontosVisitados: Boolean) {
        val userId = getUserId()
        val privasitade = Privasitade(
            conta_privada = if (contaPrivada) 1 else 0,
            Privar_os_favoritos = if (privarFavoritos) 1 else 0,
            privar_os_pontos_criados = if (privarPontosCriados) 1 else 0,
            privar_os_pontos_visitados = if (privarPontosVisitados) 1 else 0,
            privar_os_guias_criados = if (privarGuiasCriados) 1 else 0,
            IdUtilizador = userId
        )

        lifecycleScope.launch {
            database.PrivasitadeDao().InserirPrivasitade(privasitade)
            runOnUiThread {
                Toast.makeText(applicationContext, "Configurações guardado com sucesso.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }
}
