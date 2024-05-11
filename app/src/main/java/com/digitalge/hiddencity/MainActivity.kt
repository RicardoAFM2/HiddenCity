package com.digitalge.hiddencity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.digitalge.hiddencity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ClicarNaImagem()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
        }

        val fragmentTag = intent.getStringExtra("OPEN_FRAGMENT")
        val userId = intent.getIntExtra("USER_ID", -1)
        if (fragmentTag != null) {
            when (fragmentTag) {
                "Contas" -> replaceFragment(Contas())
                "Conta_publica" -> {
                    val fragment = Conta_publica().apply {
                        arguments = Bundle().apply {
                            putInt("USER_ID", userId)
                        }
                    }
                    replaceFragment(fragment)
                }
            }
        }


    }

    private fun ClicarNaImagem() {
        binding.guia.setOnClickListener {
            replaceFragment(Lista_de_Guia())
        }
        binding.favoritos.setOnClickListener {
            replaceFragment(Lista_de_Favoritos())
        }
        binding.home.setOnClickListener {
            replaceFragment(Home())
        }
        binding.mapa.setOnClickListener {
            replaceFragment(Mapa())
        }
        binding.conta.setOnClickListener {
            replaceFragment(Contas())
        }
        binding.definicao.setOnClickListener {
            val intent = Intent(this, Definicoes::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

}