package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.digitalge.hiddencity.Base_de_Dados.Utilizador
import com.digitalge.hiddencity.databinding.ActivityRegistoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import com.digitalge.hiddencity.Dao.UtilizadorDao


class Registo : AppCompatActivity() {

    private lateinit var binding: ActivityRegistoBinding
    private val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistoBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.Botaoparacriar.setOnClickListener {
            val nome = binding.Nome.text.toString()
            val email = binding.Email.text.toString()
            val senha = binding.Senha.text.toString()
            val numeroStr = binding.Numero.text.toString()

            if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
            } else if (numeroStr.length != 9 || numeroStr.toIntOrNull() == null) {
                Toast.makeText(this, "Certifique-se de que o número tem exatamente 9 dígitos e contém apenas números.", Toast.LENGTH_SHORT).show()
            } else {
                val numero = numeroStr.toInt()  // Neste ponto, já sabemos que é um número válido
                val senhaHash = hashSenha(senha)
                val utilizador = Utilizador(Nome = nome, Email = email, Senha = senhaHash, Numero = numero, Imagem = "")

                CoroutineScope(Dispatchers.IO).launch {
                    database.UtilizadorDao().inserirUtilizadores(utilizador)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Registo, "Registrado com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Registo, Login::class.java))
                    }
                }
            }
        }
    }


    fun hashSenha(senha: String): String {
        val bytes = senha.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", {str, it -> str + "%02x".format(it)})
    }
}
