package com.digitalge.hiddencity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.digitalge.hiddencity.Base_de_Dados.FormularioBD
import com.digitalge.hiddencity.databinding.ActivityFormolarioBinding
import com.digitalge.hiddencity.Dao.FormularioBDDao
import kotlinx.coroutines.launch
import com.digitalge.hiddencity.Dao.UtilizadorDao

class Formolario : AppCompatActivity() {

    private lateinit var binding: ActivityFormolarioBinding
    private lateinit var formularioDao: FormularioBDDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormolarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formularioDao = AppDatabase.getDatabase(this).FormularioBDDao()

        setupSpinners()
        Clicarnaimagem()
    }

    private fun Clicarnaimagem(){
        //Voltar para tras
        binding.voltra1.setOnClickListener {
            onBackPressed()
        }

        binding.button.setOnClickListener {
            saveFormData()
        }
    }

    private fun saveFormData() {
        val comQueViaja = binding.spinnerTravelCompanion.selectedItem.toString()
        val interesses = binding.spinnerTravelInteresses.selectedItem.toString()
        val orcamento = binding.spinnerTravelOrcamento.selectedItem.toString()
        val idUtilizador = getUserId()

        val editor = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit()
        editor.putString("comQueViaja", comQueViaja)
        editor.putString("interesses", interesses)
        editor.putString("orcamento", orcamento)
        editor.apply()

        val formulario = FormularioBD(
            Com_que_viaja = comQueViaja,
            Interesses = interesses,
            Orcamento = orcamento,
            IdUtilizador = idUtilizador
        )

        lifecycleScope.launch {
            if (checkUserExists(idUtilizador)) {
                formularioDao.inserirFormulario(formulario)
                runOnUiThread {
                    Toast.makeText(this@Formolario, "Dados salvos com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    onBackPressed()
                }
            }
        }

    }
    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }

    suspend fun verifyUserIdAndSaveFormData(idUtilizador: Int, formulario: FormularioBD) {
        val exists = checkUserExists(idUtilizador)
        if (exists) {
            saveFormData()
        } else {
            // Mostrar uma mensagem de erro ou tratar de outra forma
            runOnUiThread {
                Toast.makeText(this@Formolario, "Erro: Usuário não encontrado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun checkUserExists(idUtilizador: Int): Boolean {
        // Supondo que você tenha um método em seu DAO para verificar isso
        return formularioDao.isUserExists(idUtilizador)
    }

    private fun setupSpinners() {
        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val comQueViaja = prefs.getString("comQueViaja", "")
        val interesses = prefs.getString("interesses", "")
        val orcamento = prefs.getString("orcamento", "")

        // Ajustando os spinners para mostrar os valores salvos
        setSpinnerToValue(binding.spinnerTravelCompanion, comQueViaja)
        setSpinnerToValue(binding.spinnerTravelInteresses, interesses)
        setSpinnerToValue(binding.spinnerTravelOrcamento, orcamento)
    }

    private fun setSpinnerToValue(spinner: Spinner, value: String?) {
        val adapter = spinner.adapter
        for (position in 0 until adapter.count) {
            if (adapter.getItem(position).toString() == value) {
                spinner.setSelection(position)
                return
            }
        }
    }
}