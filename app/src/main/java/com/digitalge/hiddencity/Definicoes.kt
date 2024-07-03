package com.digitalge.hiddencity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.digitalge.hiddencity.databinding.ActivityDefinicoesBinding
import kotlinx.coroutines.launch

class Definicoes : AppCompatActivity() {

    private lateinit var binding: ActivityDefinicoesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDefinicoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Mudar o texto para Privasidade
        binding.Privasidade.textViewItem.text = "Privacidade"

        //Mudar o texto para Prenser formulario
        binding.PrenserFormulario.textViewItem.text = "Preencher formulário"

        //Mudar o texto para Modificar dados
        binding.ModificarDados.textViewItem.text = "Modificar dados"

        //Mudar o texto para conectar
        binding.conectar.textViewItem.text = "Conectar a Google"

        //Mudar o texto para eliminar
        binding.Eliminar.textViewItem.text = "Eliminar a conta"

        //Mudar o texto para Sair
        binding.Sair.textViewItem.text = "Sair da Conta"

        Clicarnaimagem()
    }


    private fun Clicarnaimagem() {
        //Voltar para tras
        binding.voltar.setOnClickListener {
            onBackPressed()
        }

        //Quanto clica para ir para a pagina privasidade
        binding.Privasidade.root.setOnClickListener {
            val intent = Intent(this, Privasidade::class.java)
            startActivity(intent)
        }
        //Quanto clica para ir para a pagina Formolario
        binding.PrenserFormulario.root.setOnClickListener {
            val intent = Intent(this, Formolario::class.java)
            startActivity(intent)
        }
        //Quanto clica para ir para a pagina Modificar
        binding.ModificarDados.root.setOnClickListener {
            val intent = Intent(this, Modificar_dados::class.java)
            startActivity(intent)
        }

        //Quanto clica Elimna a conta
        binding.Eliminar.root.setOnClickListener {
            mostrarDialogoConfirmacao()
        }


        //Quanto clica volta para o login
        binding.Sair.root.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    private fun mostrarDialogoConfirmacao() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja eliminar sua conta? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { dialog, which ->
                excluirConta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirConta() {
            val usuarioId = getUserId() // Suponha que você tenha um método que recupera o ID do usuário logado

            lifecycleScope.launch {
                try {
                    getDatabase().UtilizadorDao().Eliminar(usuarioId)
                    Toast.makeText(this@Definicoes, "Conta eliminada com sucesso!", Toast.LENGTH_SHORT).show()
                    // Redirecionar para a tela de login ou qualquer outra tela inicial
                    startActivity(Intent(this@Definicoes, Login::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@Definicoes, "Erro ao eliminar conta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "hiddencity.db").build()
    }

    private fun getUserId(): Int {
        return getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("UteID", -1)
    }
}

