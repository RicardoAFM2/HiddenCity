package com.digitalge.hiddencity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.room.Room
import com.digitalge.hiddencity.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val bd = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "hiddencity.db").build()
        val UtilizadorDao = bd.UtilizadorDao()

        CoroutineScope(Dispatchers.IO).launch {
            val Utilizador = UtilizadorDao.login(nome,email,senha)
            withContext(Dispatchers.Main){
                if (Utilizador != null){
                    val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("UteID", Utilizador.IdUtilizador)
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

}