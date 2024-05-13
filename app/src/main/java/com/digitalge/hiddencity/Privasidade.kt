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
import kotlinx.coroutines.withContext


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
        loadPrivacySettings()
    }

    private fun loadPrivacySettings() {
        val userId = getUserId()
        lifecycleScope.launch {
            // Executar chamada de banco de dados em uma thread de I/O
            val privasitade = withContext(Dispatchers.IO) {
                database.PrivasitadeDao().buscarPrivasitadePorUserId(userId)
            }
            privasitade?.let {
                binding.contaPrivada.switch1.isChecked = it.conta_privada == 1
                binding.privarOsGuiasCriados.switch1.isChecked = it.privar_os_guias_criados == 1
                binding.PrivarOsFavoritos.switch1.isChecked = it.Privar_os_favoritos == 1
                binding.privarOsPontosCriados.switch1.isChecked = it.privar_os_pontos_criados == 1
                binding.privarOsPontosVisitados.switch1.isChecked = it.privar_os_pontos_visitados == 1
            }
        }
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
        lifecycleScope.launch {
            // Verificar e buscar/atualizar na thread de I/O
            val existingPrivacy = withContext(Dispatchers.IO) {
                database.PrivasitadeDao().buscarPrivasitadePorUserId(userId)
            }
            val privasitade = Privasitade(
                conta_privada = if (contaPrivada) 1 else 0,
                Privar_os_favoritos = if (privarFavoritos) 1 else 0,
                privar_os_pontos_criados = if (privarPontosCriados) 1 else 0,
                privar_os_pontos_visitados = if (privarPontosVisitados) 1 else 0,
                privar_os_guias_criados = if (privarGuiasCriados) 1 else 0,
                IdUtilizador = userId
            )

            if (existingPrivacy == null) {
                withContext(Dispatchers.IO) {
                    database.PrivasitadeDao().InserirPrivasitade(privasitade)
                }
            } else {
                lifecycleScope.launch {
                    // Convert boolean to int for database storage
                    val contaPrivadaInt = if (contaPrivada) 1 else 0
                    val privarFavoritosInt = if (privarFavoritos) 1 else 0
                    val privarPontosCriadosInt = if (privarPontosCriados) 1 else 0
                    val privarPontosVisitadosInt = if (privarPontosVisitados) 1 else 0
                    val privarGuiasCriadosInt = if (privarGuiasCriados) 1 else 0

                    withContext(Dispatchers.IO) {
                        database.PrivasitadeDao().atualizarPrivasitade(
                            userId,
                            contaPrivadaInt,
                            privarFavoritosInt,
                            privarPontosCriadosInt,
                            privarPontosVisitadosInt,
                            privarGuiasCriadosInt
                        )
                    }
                }
                // A atualização da UI deve ser feita na thread principal
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Configurações de privacidade atualizadas com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

        private fun getUserId(): Int {
            return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
        }
}

