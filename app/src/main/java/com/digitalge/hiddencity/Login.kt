package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.digitalge.hiddencity.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "hiddencity.db").build()

        binding.Login.setOnClickListener {
            val nome = binding.Nome.text.toString()
            val email = binding.Nome.text.toString()
            val senha = binding.Senha.text.toString()

            if (nome.isNotBlank() || email.isNotBlank() && senha.isNotBlank()){
                VerrificarLogin(nome, email, senha)
            }else{
                Toast.makeText(this, "Por favor, insira o nome ou o email e a senha", Toast.LENGTH_SHORT).show()
            }
        }
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, Registo::class.java)
            startActivity(intent)
        }

    }


    fun VerrificarLogin(nome: String, email: String, senha: String){
        CoroutineScope(Dispatchers.IO).launch {
            val Utilizador = database.UtilizadorDao().login(nome,email,senha)
            withContext(Dispatchers.Main){
                if (Utilizador != null && checkSenhaHash(senha, Utilizador.Senha)){
                    val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("UteID", Utilizador.IdUtilizador)
                        putString("UteNome", Utilizador.Nome)
                        apply()
                    }
                    val intent = Intent(
                        this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@Login, "Nome ou email ou senha incorretos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun checkSenhaHash(senhaInput: String, senhaHashed: String): Boolean {
        val senhaInputHashed = hashSenha(senhaInput)
        return senhaInputHashed == senhaHashed
    }

    fun hashSenha(senha: String): String {
        val bytes = senha.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", {str, it -> str + "%02x".format(it)})
    }

}