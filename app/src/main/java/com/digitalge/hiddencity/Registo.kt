package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.digitalge.hiddencity.databinding.ActivityLoginBinding
import com.digitalge.hiddencity.databinding.ActivityRegistoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest


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

            val numero = numeroStr.toInt()

            if (nome.isBlank() || email.isBlank() || senha.isBlank() || numero == null) {
                Toast.makeText(
                    this,
                    "Por favor, preencha todos os campos corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                val senhaHash = hashSenha(senha)
                val utilizador = Utilizador(Nome = nome, Email = email, Senha = senhaHash, Numero = numero)

                CoroutineScope(Dispatchers.IO).launch {
                    database.UtilizadorDao().inserirUtilizadores(utilizador)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Registo, "Registado com sicesso!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@Registo, Login::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    fun hashSenha(senha: String): String {
        val bytes = senha.toByteArray()
        val md = MessageDigest.getInstance("SHA.256")
        val digest = md.digest(bytes)
        return digest.fold("", {str, it -> str + "%02x".format(it)})
    }
}
